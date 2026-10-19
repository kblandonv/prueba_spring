package com.prueba.dto;

import com.prueba.domain.Card;
import com.prueba.domain.CardStatus;

import java.util.UUID;

public record CardStatusResponse(
        UUID cardId,
        CardStatus status
) {

    public static CardStatusResponse from(Card card) {
        return new CardStatusResponse(card.getCardId(), card.getStatus());
    }
}