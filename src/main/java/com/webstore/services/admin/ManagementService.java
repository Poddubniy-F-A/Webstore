package com.webstore.services.admin;

import com.webstore.model.entities.Good;
import com.webstore.exceptions.GoodNotFoundException;
import com.webstore.repositories.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final GoodsRepository goodsRepository;

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

    public Good getGoodById(Long id) throws GoodNotFoundException {
        Optional<Good> response = goodsRepository.findById(id);
        if (response.isEmpty()) {
            throw new GoodNotFoundException();
        }

        return response.get();
    }
}
