package com.webstore.services.shop;

import com.webstore.entities.Good;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.exceptions.IllegalGoodsCountException;
import com.webstore.repositories.GoodsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GoodsService {
    private GoodsRepository goodsRepository;

    public List<Good> getAllGoods() {
        return goodsRepository.findAll();
    }

    public void addGood(
            String picturePath,
            String label,
            String description,
            String brand,
            String category,
            int price
    ) {
        Good good = new Good();
        good.setPicturePath(picturePath);
        good.setLabel(label);
        good.setDescription(description);
        good.setBrand(brand);
        good.setCategory(category);
        good.setPrice(price);
        goodsRepository.save(good);
    }

    public void updateGood(
            Long id,
            String picturePath,
            String label,
            String description,
            String brand,
            String category,
            int price
    ) throws GoodNotFoundException {
        Good good = getGoodById(id);
        good.setPicturePath(picturePath);
        good.setLabel(label);
        good.setDescription(description);
        good.setBrand(brand);
        good.setCategory(category);
        good.setPrice(price);
        goodsRepository.save(good);
    }

    public void handleDelivery(Long goodId, int count) throws GoodNotFoundException, IllegalGoodsCountException {
        if (count <= 0) {
            throw new IllegalGoodsCountException();
        }

        Good good = getGoodById(goodId);
        good.setCount(good.getCount() + count);
        goodsRepository.save(good);
    }

    public Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }
}
