package com.prueba.service;

import com.prueba.domain.Card;
import com.prueba.domain.CardStatus;
import com.prueba.domain.Product;
import com.prueba.dto.IssueCardRequest;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.repository.CardRepository;
import com.prueba.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final int RANDOM_DIGITS = 10;
    private static final int MAX_ATTEMPTS = 10;
    private static final int VALIDITY_YEARS = 3;
    private static final String CURRENCY = "USD";

    private final CardRepository cardRepository;
    private final ProductRepository productRepository;
    private final SecureRandom random = new SecureRandom();

    // POST /cards
    @Transactional
    public Card issue(IssueCardRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado: " + request.productId()));

        LocalDate today = LocalDate.now();

        Card card = new Card();
        card.setProduct(product);
        card.setCardNumber(generateCardNumber(product.getProductId()));
        card.setHolderName(request.holderName().trim());
        card.setIssuedAt(today);
        card.setExpiresAt(today.plusYears(VALIDITY_YEARS));
        card.setStatus(CardStatus.ISSUED);
        card.setBalance(new BigDecimal("0.00"));
        card.setCurrency(CURRENCY);

        return cardRepository.save(card);
    }

    // POST /cards/{cardId}/activate
    @Transactional
    public Card activate(UUID cardId) {
        Card card = findCard(cardId);
        card.activate();
        return card;
    }

    // POST /cards/{cardId}/block
    @Transactional
    public Card block(UUID cardId, String reason) {
        Card card = findCard(cardId);
        card.block(reason);
        return card;
    }

    // GET /cards/{cardId}
    @Transactional(readOnly = true)
    public Card getById(UUID cardId) {
        return findCard(cardId);
    }

    private Card findCard(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada: " + cardId));
    }

    private String generateCardNumber(String productId) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            StringBuilder number = new StringBuilder(productId);
            for (int i = 0; i < RANDOM_DIGITS; i++) {
                number.append(random.nextInt(10));
            }
            String candidate = number.toString();
            if (!cardRepository.existsByCardNumber(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("No fue posible generar un número de tarjeta único");
    }
}
