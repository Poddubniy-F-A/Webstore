package com.webstore.services.shop.customer;

import com.webstore.model.entities.Feedback;
import com.webstore.model.entities.Good;
import com.webstore.model.entities.Order;
import com.webstore.model.entities.User;
import com.webstore.exceptions.feedbacks.FeedbackNotFoundException;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.feedbacks.IllegalRatingTryException;
import com.webstore.repositories.FeedbacksRepository;
import com.webstore.repositories.GoodsRepository;
import com.webstore.repositories.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbacksService {

    private final FeedbacksRepository feedbacksRepository;
    private final OrdersRepository ordersRepository;
    private final GoodsRepository goodsRepository;

    public HashMap<Good, Feedback> getGoodsWithFeedbacksBy(User user) {
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
            User user,
            Long goodId,
            String text,
            int rating
    ) throws GoodNotFoundException, IllegalRatingTryException {
        Good good = getGoodById(goodId);
        if (ordersRepository.findByUser(user).stream().noneMatch(order -> order.getGood().equals(good))) {
            throw new IllegalRatingTryException();
        }

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setGood(good);
        feedback.setText(text);
        feedback.setRating(rating);
        feedbacksRepository.save(feedback);

        updateRating(good, rating);
    }

    @Transactional
    public void handleFeedbackEditing(
            User user,
            Long goodId,
            String text,
            int rating
    ) throws GoodNotFoundException, FeedbackNotFoundException {
        Good good = getGoodById(goodId);

        Feedback feedback = getFeedbackAboutBy(good, user);
        feedback.setText(text);
        feedback.setRating(rating);
        feedbacksRepository.save(feedback);

        updateRating(good, rating);
    }

    private void updateRating(Good good, int rating) {
        good.setRatingsCount(good.getRatingsCount() + 1);
        good.setRatingsSum(good.getRatingsSum() + rating);
        goodsRepository.save(good);
    }

    public Feedback getFeedbackAboutBy(Good good, User user) throws FeedbackNotFoundException {
        List<Feedback> response = feedbacksRepository.findByGoodAndUser(good, user);
        if (response.isEmpty()) {
            throw new FeedbackNotFoundException();
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
