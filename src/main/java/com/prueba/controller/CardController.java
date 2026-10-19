package com.prueba.controller;

import com.prueba.domain.Card;
import com.prueba.dto.BlockCardRequest;
import com.prueba.dto.CardDetailResponse;
import com.prueba.dto.CardStatusResponse;
import com.prueba.dto.IssueCardRequest;
import com.prueba.dto.IssueCardResponse;
import com.prueba.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Emitir tarjeta -> 201 Created
    @PostMapping
    public ResponseEntity<IssueCardResponse> issue(@Valid @RequestBody IssueCardRequest request) {
        Card card = cardService.issue(request);
        URI location = URI.create("/cards/" + card.getCardId());
        return ResponseEntity.created(location).body(IssueCardResponse.from(card));
    }

    // Activar tarjeta -> 200 OK (idempotente si ya está ACTIVE, 409 si está BLOCKED)
    @PostMapping("/{cardId}/activate")
    public ResponseEntity<CardStatusResponse> activate(@PathVariable("cardId") UUID cardId) {
        Card card = cardService.activate(cardId);
        return ResponseEntity.ok(CardStatusResponse.from(card));
    }

    // Bloquear tarjeta -> 200 OK (idempotente si ya está BLOCKED)
    @PostMapping("/{cardId}/block")
    public ResponseEntity<CardStatusResponse> block(@PathVariable("cardId") UUID cardId,
                                                    @Valid @RequestBody BlockCardRequest request) {
        Card card = cardService.block(cardId, request.reason());
        return ResponseEntity.ok(CardStatusResponse.from(card));
    }

    // Consultar tarjeta -> 200 OK / 404 Not Found
    @GetMapping("/{cardId}")
    public ResponseEntity<CardDetailResponse> getById(@PathVariable("cardId") UUID cardId) {
        Card card = cardService.getById(cardId);
        return ResponseEntity.ok(CardDetailResponse.from(card));
    }
}
