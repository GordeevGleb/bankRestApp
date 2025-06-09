package org.example.bank_app.repository;

import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByUserId(Long customerId);
    List<Card> findByUserIdAndStatusAndNumberIn(Long userId, CardStatus status, List<String> numbers);
    Page<Card> findAllByUser(User user, Pageable pageable);
}
