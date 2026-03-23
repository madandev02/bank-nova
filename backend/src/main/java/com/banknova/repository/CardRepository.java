package com.banknova.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banknova.entity.Card;
import com.banknova.entity.User;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);

    List<Card> findByUserAndStatusIn(User user, List<Card.CardStatus> statuses);

    Optional<Card> findByUserAndIsDefault(User user, Boolean isDefault);

    Optional<Card> findByTokenizedCardNumber(String tokenizedCardNumber);

    List<Card> findByUserAndStatusAndIsDefault(User user, Card.CardStatus status, Boolean isDefault);
}
