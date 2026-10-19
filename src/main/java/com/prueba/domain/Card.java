package com.prueba.domain;

import com.prueba.exception.InvalidCardStateException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor


public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "card_id")
    private UUID cardId;

    @Column(name = "card_number", nullable = false, length = 16, unique = true)
    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column (name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", nullable = false, length = 10)
    private CardStatus status;

    @Column (name = "blocked_at")
    private LocalDateTime blockedAt;

    @Column (name = "blocked_reason", length = 255)
    private String blockedReason;

    @Column (nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column (nullable = false, length = 3)
    private String currency;

    public void activate() {
        if (status == CardStatus.ACTIVE) {
            return; // idempotente: ya está activa
        }
        if (status == CardStatus.BLOCKED) {
            throw new InvalidCardStateException("Una tarjeta BLOCKED no puede activarse");
        }
        this.status = CardStatus.ACTIVE;
    }


    public void block(String reason) {
        if (status == CardStatus.BLOCKED) {
            return; // idempotente: ya está bloqueada
        }
        this.status = CardStatus.BLOCKED;
        this.blockedAt = LocalDateTime.now();
        this.blockedReason = reason;
    }

}
