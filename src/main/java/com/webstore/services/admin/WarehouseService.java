package com.webstore.services.admin;

import com.webstore.model.entities.Good;
import com.webstore.exceptions.warehouse.IllegalGoodIdException;
import com.webstore.exceptions.warehouse.IllegalGoodQuantityException;
import com.webstore.repositories.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final GoodsRepository goodsRepository;

    public void handleDelivery(Long goodId, int count) throws IllegalGoodQuantityException, IllegalGoodIdException {
        if (count <= 0) {
            throw new IllegalGoodQuantityException();
        }

        Optional<Good> response = goodsRepository.findById(goodId);
        if (response.isEmpty()) {
            throw new IllegalGoodIdException();
        }

        Good good = response.get();
        good.setCount(good.getCount() + count);
        goodsRepository.save(good);
    }
}
