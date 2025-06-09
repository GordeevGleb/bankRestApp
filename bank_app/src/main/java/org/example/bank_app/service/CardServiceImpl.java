package org.example.bank_app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final Encryptor encryptor;
    private final UserService userService;
    private final CardMapper cardMapper;
    private final EntityManager entityManager;

    @Override
    public CardDto create(NewCardDto newCardDto, Long holderId) {
        User user = userService.findById(holderId);
        if (user.getIsBlocked()) {
            throw new WrongOperationException(String.format("user id %d is blocked", holderId));
        }
        String encryptedNumber = encryptor.encrypt(newCardDto.getCardNumber());
        Card card = cardMapper.toCard(newCardDto, user, encryptedNumber);
        card.setStatus(CardStatus.ACTIVE);
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    @Override
    public CardDto updateStatus(Long cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException(String.format("Card with id %d not found", cardId))
        );

        User user = card.getUser();
        if (user.getIsBlocked()) {
            throw new WrongOperationException("user is blocked");
        }

        boolean isAdmin = user.getRoles().contains(Roles.ADMIN);
        boolean isBlockingByUser = !isAdmin && status == CardStatus.BLOCKED;

        if (!isAdmin && !isBlockingByUser) {
            throw new WrongOperationException("wrong operation");
        }

        card.setStatus(status);
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDto> getAll(Long userId, int from, int size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
        Root<Card> root = criteriaQuery.from(Card.class);

        List<Predicate> predicates = new ArrayList<>();

        if (userId != null) {
            predicates.add(criteriaBuilder.equal(root.get("user"), userId));
        }
        List<Card> cards = entityManager.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        return cardMapper.toCardDtoList(cards);
    }

    @Override
    public List<UserCardDto> getByHolder(String login, int from, int size) {
        User user = userService.findByLogin(login);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Card> cardPage = cardRepository.findAllByUser(user, pageable);

        return cardMapper.toUserCardDtoList(cardPage.getContent());
    }


    @Override
    public void delete(Long id) {
        cardRepository.deleteById(id);
    }

    @Override
    public List<Card> findByUserIdAndStatusAndNumberIn(Long userId, CardStatus status, List<String> numbers){
        return cardRepository.findByUserIdAndStatusAndNumberIn(userId, status, numbers);
    }
}
