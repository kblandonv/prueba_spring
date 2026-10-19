package com.prueba.repository;

import com.prueba.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    boolean existsByCardNumber(String cardNumber);
    
}
