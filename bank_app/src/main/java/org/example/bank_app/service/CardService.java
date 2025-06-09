package org.example.bank_app.service;

import org.example.bank_app.dto.CardDto;
import org.example.bank_app.dto.NewCardDto;
import org.example.bank_app.dto.UserCardDto;
import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.enums.CardStatus;

import java.util.List;

public interface CardService {

    CardDto create(NewCardDto newCardDto, Long holderId);

    CardDto updateStatus(Long cardId, CardStatus cardStatus);

    void delete(Long cardId);

    List<CardDto> getAll(Long userId, int page, int size);

    List<UserCardDto> getByHolder(String login, int page, int size);

    List<Card> findByUserIdAndStatusAndNumberIn(Long userId, CardStatus status, List<String> numbers);
}
