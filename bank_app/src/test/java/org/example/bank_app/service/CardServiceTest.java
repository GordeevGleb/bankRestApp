package org.example.bank_app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.bank_app.dto.CardDto;
import org.example.bank_app.dto.NewCardDto;
import org.example.bank_app.dto.UserCardDto;
import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.CardStatus;
import org.example.bank_app.entity.enums.Roles;
import org.example.bank_app.exception.NotFoundException;
import org.example.bank_app.exception.WrongOperationException;
import org.example.bank_app.mapper.CardMapper;
import org.example.bank_app.repository.CardRepository;
import org.example.bank_app.util.Encryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock private Encryptor encryptor;
    @Mock private UserService userService;
    @Mock private CardMapper cardMapper;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void create_validUser_shouldCreateCard() {
        Long userId = 1L;
        NewCardDto dto = NewCardDto.builder()
                .cardNumber("1234-1234-1234-1234")
                .expiryDate(LocalDate.now().plusDays(1))
                .build();
        User user = User.builder().id(userId).isBlocked(false).build();
        Card card = new Card();
        Card savedCard = new Card();
        CardDto expectedDto = new CardDto();

        when(userService.findById(userId)).thenReturn(user);
        when(encryptor.encrypt("1234-1234-1234-1234")).thenReturn("encrypted");
        when(cardMapper.toCard(dto, user, "encrypted")).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(savedCard);
        when(cardMapper.toCardDto(savedCard)).thenReturn(expectedDto);

        CardDto result = cardService.create(dto, userId);

        assertEquals(expectedDto, result);
        verify(cardRepository).save(card);
    }

    @Test
    void create_blockedUser_shouldThrowException() {
        Long userId = 2L;
        NewCardDto dto = NewCardDto.builder()
                .cardNumber("1234-1234-1234-1234")
                .expiryDate(LocalDate.now().plusDays(1))
                .build();
        User user = User.builder().id(userId).isBlocked(true).build();

        when(userService.findById(userId)).thenReturn(user);

        assertThrows(WrongOperationException.class, () -> cardService.create(dto, userId));
        verifyNoInteractions(cardRepository);
    }

    @Test
    void updateStatus_adminNotBlocked_shouldUpdate() {
        Long cardId = 1L;
        User user = User.builder()
                .roles(Set.of(Roles.ADMIN))
                .isBlocked(false)
                .build();
        Card card = new Card();
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toCardDto(card)).thenReturn(new CardDto());

        CardDto result = cardService.updateStatus(cardId, CardStatus.ACTIVE);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void updateStatus_userNotBlockedBlocking_shouldUpdate() {
        Long cardId = 2L;
        User user = User.builder()
                .roles(Set.of(Roles.USER))
                .isBlocked(false)
                .build();
        Card card = new Card();
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toCardDto(card)).thenReturn(new CardDto());

        CardDto result = cardService.updateStatus(cardId, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void updateStatus_userNotBlockedWrongStatus_shouldThrow() {
        Long cardId = 3L;
        User user = User.builder()
                .roles(Set.of(Roles.USER))
                .isBlocked(false)
                .build();
        Card card = new Card();
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(WrongOperationException.class, () ->
                cardService.updateStatus(cardId, CardStatus.ACTIVE));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateStatus_userBlocked_shouldThrow() {
        Long cardId = 4L;
        User user = User.builder()
                .roles(Set.of(Roles.ADMIN))  // даже если админ
                .isBlocked(true)
                .build();
        Card card = new Card();
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        WrongOperationException ex = assertThrows(WrongOperationException.class,
                () -> cardService.updateStatus(cardId, CardStatus.BLOCKED));

        assertEquals("user is blocked", ex.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateStatus_cardNotFound_shouldThrow() {
        Long cardId = 404L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> cardService.updateStatus(cardId, CardStatus.BLOCKED));

        assertEquals("Card with id 404 not found", ex.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getAll_withUserId_shouldReturnCards() {
        Long userId = 1L;
        int from = 0, size = 5;

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Card> cq = mock(CriteriaQuery.class);
        Root<Card> root = mock(Root.class);
        TypedQuery<Card> query = mock(TypedQuery.class);

        List<Card> cards = List.of(new Card());
        List<CardDto> dtos = List.of(new CardDto());

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Card.class)).thenReturn(cq);
        when(cq.from(Card.class)).thenReturn(root);
        when(entityManager.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(from)).thenReturn(query);
        when(query.setMaxResults(size)).thenReturn(query);
        when(query.getResultList()).thenReturn(cards);
        when(cardMapper.toCardDtoList(cards)).thenReturn(dtos);

        List<CardDto> result = cardService.getAll(userId, from, size);
        assertEquals(dtos, result);
    }

    @Test
    void getAll_withoutUserId_shouldReturnAllCards() {
        int from = 0, size = 10;

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Card> cq = mock(CriteriaQuery.class);
        Root<Card> root = mock(Root.class);
        TypedQuery<Card> query = mock(TypedQuery.class);

        List<Card> cards = List.of(new Card());
        List<CardDto> dtos = List.of(new CardDto());

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Card.class)).thenReturn(cq);
        when(cq.from(Card.class)).thenReturn(root);
        when(entityManager.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(from)).thenReturn(query);
        when(query.setMaxResults(size)).thenReturn(query);
        when(query.getResultList()).thenReturn(cards);
        when(cardMapper.toCardDtoList(cards)).thenReturn(dtos);

        List<CardDto> result = cardService.getAll(null, from, size);
        assertEquals(dtos, result);
    }

    @Test
    void getByHolder_shouldReturnUserCards() {
        String login = "testUser";
        User user = new User();
        int from = 0;
        int size = 2;

        List<Card> cards = List.of(new Card(), new Card());
        List<UserCardDto> dtos = List.of(new UserCardDto(), new UserCardDto());
        Page<Card> page = new PageImpl<>(cards);

        when(userService.findByLogin(login)).thenReturn(user);
        when(cardRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(page);
        when(cardMapper.toUserCardDtoList(cards)).thenReturn(dtos);

        List<UserCardDto> result = cardService.getByHolder(login, from, size);

        assertEquals(dtos, result);
    }

    @Test
    void getByHolder_userNotFound_shouldThrow() {
        String login = "testUser";
        when(userService.findByLogin(login)).thenReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                cardService.getByHolder(login, 0, 5)
        );

        assertEquals("User not found", ex.getMessage());
    }



    @Test
    void delete_shouldCallRepository() {
        Long cardId = 5L;

        cardService.delete(cardId);

        verify(cardRepository).deleteById(cardId);
    }
}
