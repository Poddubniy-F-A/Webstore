package com.webstore.repositories;

import com.webstore.model.entities.Good;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends JpaRepository<Good, Long> {
}
