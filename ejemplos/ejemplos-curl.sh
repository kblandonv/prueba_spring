#!/usr/bin/env bash
# =====================================================================
# Ejemplos de uso de la API de Tarjetas (curl)
# Requisitos: la aplicación corriendo en http://localhost:8080
# Uso:        bash ejemplos/ejemplos-curl.sh
# =====================================================================

BASE=${BASE:-http://localhost:8080}

title() { printf "\n\033[1m=== %s ===\033[0m\n" "$1"; }

title "1. Emitir tarjeta (esperado 201, status ISSUED)"
RESP=$(curl -s -X POST "$BASE/cards" -H "Content-Type: application/json" \
  -d '{"productId":"123456","holderName":"Ricardo Perez"}')
echo "$RESP"
CARD_ID=$(echo "$RESP" | sed -E 's/.*"cardId":"([^"]+)".*/\1/')
echo "cardId: $CARD_ID"

title "2. Activar tarjeta (esperado 200, status ACTIVE)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards/$CARD_ID/activate"

title "3. Activar de nuevo (esperado 200, idempotente)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards/$CARD_ID/activate"

title "4. Consultar tarjeta (esperado 200, status ACTIVE)"
curl -s -w "  [HTTP %{http_code}]\n" "$BASE/cards/$CARD_ID"

title "5. Bloquear tarjeta (esperado 200, status BLOCKED)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards/$CARD_ID/block" \
  -H "Content-Type: application/json" -d '{"reason":"SECURITY_INCONSISTENCY"}'

title "6. Bloquear de nuevo (esperado 200, idempotente)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards/$CARD_ID/block" \
  -H "Content-Type: application/json" -d '{"reason":"SECURITY_INCONSISTENCY"}'

title "7. Consultar tarjeta bloqueada (esperado 200, con blockedAt y blockedReason)"
curl -s -w "  [HTTP %{http_code}]\n" "$BASE/cards/$CARD_ID"

title "8. Activar tarjeta bloqueada (esperado 409 Conflict)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards/$CARD_ID/activate"

title "9. Emitir con productId invalido (esperado 400)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards" -H "Content-Type: application/json" \
  -d '{"productId":"12A45","holderName":"Ricardo Perez"}'

title "10. Emitir con producto inexistente (esperado 404)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards" -H "Content-Type: application/json" \
  -d '{"productId":"999999","holderName":"Ricardo Perez"}'

title "11. Emitir sin holderName (esperado 400)"
curl -s -w "  [HTTP %{http_code}]\n" -X POST "$BASE/cards" -H "Content-Type: application/json" \
  -d '{"productId":"123456"}'

title "12. Consultar tarjeta inexistente (esperado 404)"
curl -s -w "  [HTTP %{http_code}]\n" "$BASE/cards/00000000-0000-0000-0000-000000000000"

title "13. Consultar con cardId invalido (esperado 400)"
curl -s -w "  [HTTP %{http_code}]\n" "$BASE/cards/abc"

title "14. Flujo final: emitir y activar (la tarjeta queda ACTIVE)"
RESP=$(curl -s -X POST "$BASE/cards" -H "Content-Type: application/json" \
  -d '{"productId":"654321","holderName":"Ricardo Perez"}')
NEW_ID=$(echo "$RESP" | sed -E 's/.*"cardId":"([^"]+)".*/\1/')
curl -s -o /dev/null -X POST "$BASE/cards/$NEW_ID/activate"
curl -s -w "  [HTTP %{http_code}]\n" "$BASE/cards/$NEW_ID"
