package com.prueba.dto;

import com.prueba.domain.CardStatus;
import com.prueba.domain.Card;


import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;

public record IssueCardResponse (
        UUID cardId,
        String cardNumber,
        CardStatus status,
        LocalDate expiresAt,
        BigDecimal balance,
        String currency
) {
    public static IssueCardResponse from(Card card) {
        return new IssueCardResponse(
            card.getCardId(),
            card.getCardNumber(),
            card.getStatus(),
            card.getExpiresAt(),
            card.getBalance(),
            card.getCurrency()
        );
    }
}
