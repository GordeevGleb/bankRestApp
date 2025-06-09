package org.example.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.example.bank_app.dto.TransferDto;
import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.CardStatus;
import org.example.bank_app.exception.NotFoundException;
import org.example.bank_app.util.Encryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardService cardService;
    private final Encryptor encryptor;
    private final UserService userService;

    @Override
    public TransferDto transfer(TransferDto transferDto, String login) {
        User user = userService.findByLogin(login);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        String encryptedFrom = encryptor.encrypt(transferDto.getFromNumber());
        String encryptedTo = encryptor.encrypt(transferDto.getToNumber());

        List<Card> cards = cardService.findByUserIdAndStatusAndNumberIn(user.getId(),
                CardStatus.ACTIVE, List.of(transferDto.getFromNumber(), transferDto.getToNumber()));

        if (cards.size() != 2) {
            throw new NotFoundException("incorrect input data");
        }

        Card fromCard = cards.stream()
                .filter(c -> c.getNumber().equals(encryptedFrom))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Карта отправителя не найдена"));

        Card toCard = cards.stream()
                .filter(c -> c.getNumber().equals(encryptedTo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Карта получателя не найдена"));

        double amount = transferDto.getDifference();
        if (fromCard.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств на карте отправителя");
        }

        fromCard.setBalance(fromCard.getBalance() - amount);
        toCard.setBalance(toCard.getBalance() + amount);

        return TransferDto.builder()
                .fromNumber(encryptor.encrypt(fromCard.getNumber()))
                .toNumber(encryptor.encrypt(toCard.getNumber()))
                .difference(amount)
                .build();
    }
}
