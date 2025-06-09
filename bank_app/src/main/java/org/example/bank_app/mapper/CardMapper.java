package org.example.bank_app.mapper;



import org.example.bank_app.dto.CardDto;
import org.example.bank_app.dto.NewCardDto;
import org.example.bank_app.dto.UserCardDto;
import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "number", source = "number")
    Card toCard(NewCardDto newCardDto, User user, String number);

    CardDto toCardDto(Card card);

    List<CardDto> toCardDtoList(List<Card> cards);

    @Mapping(target = "number", source = "number")
    UserCardDto toUserCardDto(Card card, String number);

//    @Mapping(target = "number", source = "number")
    List<UserCardDto> toUserCardDtoList(List<Card> cards);
}

