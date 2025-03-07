package com.webstore.services.shop.customer;

import com.webstore.entities.Feedback;
import com.webstore.entities.Good;
import com.webstore.entities.Order;
import com.webstore.entities.User;
import com.webstore.exceptions.DuplicateFeedbackException;
import com.webstore.exceptions.FeedbackNotFoundException;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.NoFeedbacksException;
import com.webstore.repositories.FeedbacksRepository;
import com.webstore.repositories.GoodsRepository;
import com.webstore.repositories.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeedbacksService {
    private FeedbacksRepository feedbacksRepository;
    private OrdersRepository ordersRepository;
    private GoodsRepository goodsRepository;

    public List<Feedback> getFeedbacksAbout(Long goodId) throws GoodNotFoundException {
        return feedbacksRepository.findByGood(getGoodById(goodId));
    }

    public HashMap<Good, Feedback> getGoodsWithFeedbacksBy(User user) throws DuplicateFeedbackException {
        HashMap<Good, Feedback> result = new HashMap<>();
        for (Good good : ordersRepository.findByUser(user).stream().map(Order::getGood).collect(Collectors.toSet())) {
            try {
                result.put(good, getFeedbackAboutBy(good, user));
            } catch (FeedbackNotFoundException e) {
                result.put(good, null);
            }
        }
        return result;
    }

    @Transactional
    public void handleFeedbackCreating(
            User user, Long goodId, String text, int rate
    ) throws GoodNotFoundException, NoFeedbacksException {
        Good good = getGoodById(goodId);

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setGood(good);
        feedback.setText(text);
        feedback.setRate(rate);
        feedbacksRepository.save(feedback);

        updateRating(good);
    }

    @Transactional
    public void handleFeedbackEditing(
            User user, Long goodId, String text, int rate
    ) throws GoodNotFoundException, FeedbackNotFoundException, DuplicateFeedbackException, NoFeedbacksException {
        Good good = getGoodById(goodId);

        Feedback feedback = getFeedbackAboutBy(good, user);
        feedback.setText(text);
        feedback.setRate(rate);
        feedbacksRepository.save(feedback);

        updateRating(good);
    }

    private void updateRating(Good good) throws NoFeedbacksException {
        List<Feedback> feedbacks = feedbacksRepository.findByGood(good);
        if (feedbacks.isEmpty()) {
            throw new NoFeedbacksException("Не найдено отзывов про " + good);
        }
        good.setRatingsCount(feedbacks.size());
        good.setRate(feedbacks.stream().mapToInt(Feedback::getRate).average().orElse(0));
        goodsRepository.save(good);
    }

    public Feedback getFeedbackAboutBy(
            Good good, User user
    ) throws FeedbackNotFoundException, DuplicateFeedbackException {
        List<Feedback> response = feedbacksRepository.findByGoodAndUser(good, user);
        if (response.isEmpty()) {
            throw new FeedbackNotFoundException();
        }
        if (response.size() > 1) {
            throw new DuplicateFeedbackException(user + " сделал более одного отзыва на " + good);
        }
        return response.getFirst();
    }

    public Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }
}
