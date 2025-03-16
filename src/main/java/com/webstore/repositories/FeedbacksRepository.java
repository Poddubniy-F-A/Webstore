package com.webstore.repositories;

import com.webstore.model.entities.Feedback;
import com.webstore.model.entities.Good;
import com.webstore.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbacksRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByGood(Good good);

    List<Feedback> findByGoodAndUser(Good good, User user);
}
