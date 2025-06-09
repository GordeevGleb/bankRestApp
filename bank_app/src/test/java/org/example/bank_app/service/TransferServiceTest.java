package org.example.bank_app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.aspectj.weaver.ast.Not;
import org.example.bank_app.dto.TransferDto;
import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.CardStatus;
import org.example.bank_app.exception.NotFoundException;
import org.example.bank_app.util.Encryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock private CardService cardService;
    @Mock private Encryptor encryptor;
    @Mock private UserService userService;

    @InjectMocks
    private TransferServiceImpl transferService;

    private final String login = "user1";
    private final String fromNumber = "1111";
    private final String toNumber = "2222";
    private final double amount = 100.0;

    private User user;
    private TransferDto transferDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        transferDto = TransferDto.builder()
                .fromNumber(fromNumber)
                .toNumber(toNumber)
                .difference(amount)
                .build();
    }

    @Test
    void transfer_successfulTransfer() {
        String encryptedFrom = "enc1111";
        String encryptedTo = "enc2222";

        Card fromCard = new Card();
        fromCard.setNumber(encryptedFrom);
        fromCard.setBalance(200.0);

        Card toCard = new Card();
        toCard.setNumber(encryptedTo);
        toCard.setBalance(50.0);

        when(userService.findByLogin(login)).thenReturn(user);
        when(encryptor.encrypt(fromNumber)).thenReturn(encryptedFrom);
        when(encryptor.encrypt(toNumber)).thenReturn(encryptedTo);
        when(cardService.findByUserIdAndStatusAndNumberIn(user.getId(), CardStatus.ACTIVE,
                List.of(fromNumber, toNumber))).thenReturn(List.of(fromCard, toCard));
        when(encryptor.encrypt(fromCard.getNumber())).thenReturn(encryptedFrom);
        when(encryptor.encrypt(toCard.getNumber())).thenReturn(encryptedTo);

        TransferDto result = transferService.transfer(transferDto, login);

        assertEquals(encryptedFrom, result.getFromNumber());
        assertEquals(encryptedTo, result.getToNumber());
        assertEquals(Optional.of(amount).get(), result.getDifference());

        assertEquals(Optional.of(100.0).get(), fromCard.getBalance());
        assertEquals(Optional.of(150.0).get(), toCard.getBalance());
    }

    @Test
    void transfer_userNotFound_throwsNotFoundException() {
        when(userService.findByLogin(login)).thenReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> transferService.transfer(transferDto, login));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void transfer_cardsSizeNotTwo_throwsNotFoundException() {
        when(userService.findByLogin(login)).thenReturn(user);
        when(encryptor.encrypt(anyString())).thenReturn("encrypted");
        when(cardService.findByUserIdAndStatusAndNumberIn(anyLong(), any(), anyList()))
                .thenReturn(List.of(new Card())); // only one card

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> transferService.transfer(transferDto, login));

        assertEquals("incorrect input data", ex.getMessage());
    }

    @Test
    void transfer_CardFromNotFound_throwsNotFoundException() {
        String encryptedTo = "2222-2222-2222-2222";
        Card toCard = new Card();
        toCard.setNumber(encryptedTo);

        when(userService.findByLogin(login)).thenReturn(user);
        when(encryptor.encrypt(fromNumber)).thenReturn("enc1111");
        when(encryptor.encrypt(toNumber)).thenReturn(encryptedTo);
        when(cardService.findByUserIdAndStatusAndNumberIn(anyLong(), any(), anyList()))
                .thenReturn(List.of(toCard)); // missing fromCard

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> transferService.transfer(transferDto, login));

        assertEquals("incorrect input data", ex.getMessage());
    }

    @Test
    void transfer_CardToNotFound_throwsNotFoundException() {
        String encryptedFrom = "2222-2222-2222-2222";
        Card fromCard = new Card();
        fromCard.setNumber(encryptedFrom);

        when(userService.findByLogin(login)).thenReturn(user);
        when(encryptor.encrypt(fromNumber)).thenReturn(encryptedFrom);
        when(encryptor.encrypt(toNumber)).thenReturn("enc2222");
        when(cardService.findByUserIdAndStatusAndNumberIn(anyLong(), any(), anyList()))
                .thenReturn(List.of(fromCard)); // missing toCard

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> transferService.transfer(transferDto, login));

        assertEquals("incorrect input data", ex.getMessage());
    }

    @Test
    void transfer_insufficientFunds_throwsIllegalArgumentException() {
        String encryptedFrom = "enc1111";
        String encryptedTo = "enc2222";

        Card fromCard = new Card();
        fromCard.setNumber(encryptedFrom);
        fromCard.setBalance(50.0); // less than amount

        Card toCard = new Card();
        toCard.setNumber(encryptedTo);

        when(userService.findByLogin(login)).thenReturn(user);
        when(encryptor.encrypt(fromNumber)).thenReturn(encryptedFrom);
        when(encryptor.encrypt(toNumber)).thenReturn(encryptedTo);
        when(cardService.findByUserIdAndStatusAndNumberIn(user.getId(), CardStatus.ACTIVE,
                List.of(fromNumber, toNumber))).thenReturn(List.of(fromCard, toCard));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(transferDto, login));

        assertEquals("Недостаточно средств на карте отправителя", ex.getMessage());
    }
}



