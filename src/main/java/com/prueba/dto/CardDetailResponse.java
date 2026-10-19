package com.prueba.dto;

import com.prueba.domain.Card;
import com.prueba.domain.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardDetailResponse(
        UUID cardId,
        String cardNumber,
        String holderName,
        CardStatus status,
        String blockedAt,
        String blockedReason,
        LocalDate expiresAt,
        BigDecimal balance,
        String currency
) {

    public static CardDetailResponse from(Card card) {
        return new CardDetailResponse(
                card.getCardId(),
                card.getCardNumber(),
                card.getHolderName(),
                card.getStatus(),
                card.getBlockedAt() != null ? card.getBlockedAt().toString() : "",
                card.getBlockedReason() != null ? card.getBlockedReason() : "",
                card.getExpiresAt(),
                card.getBalance(),
                card.getCurrency()
        );
    }
}