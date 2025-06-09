package org.example.bank_app.security;

import lombok.RequiredArgsConstructor;
import org.example.bank_app.repository.CardRepository;
import org.springframework.stereotype.Component;

@Component("cardSecurity")
@RequiredArgsConstructor
public class CardSecurity {

    private final CardRepository cardRepository;

    public boolean isCardOwnerIsNotBlocked(Long cardId, String username) {
        return cardRepository.findById(cardId)
                .map(card -> {
                    if (card.getUser().getLogin().equals(username) && !card.getUser().getIsBlocked()) {
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean isCardOwner(Long cardId, String username) {
        return cardRepository.findById(cardId)
                .map(card -> card.getUser().getLogin().equals(username))
                .orElse(false);
    }
}

