package com.webstore.services.shop;

import com.webstore.model.entities.Feedback;
import com.webstore.model.entities.Good;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.repositories.FeedbacksRepository;
import com.webstore.repositories.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final GoodsRepository goodsRepository;
    private final FeedbacksRepository feedbacksRepository;

    public List<Good> getAllGoods() {
        return goodsRepository.findAll();
    }

    public List<Feedback> getFeedbacksAbout(Long goodId) throws GoodNotFoundException {
        return feedbacksRepository.findByGood(getGoodById(goodId));
    }

    public Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }
}
