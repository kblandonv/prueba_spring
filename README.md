# prueba_spring — API de Tarjetas

API REST para la administración de tarjetas asociadas a productos: **emisión, activación, bloqueo y consulta**.

## Tecnologías

- Java 17
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL
- Maven (incluye wrapper `./mvnw`)
- Lombok

## Requisitos

- JDK 17 o superior
- PostgreSQL en ejecución (local, puerto `5432`)
- Git

## Base de datos

1. Crear la base de datos:

   ```sql
   CREATE DATABASE prueba;
   ```

2. Ajustar las credenciales en `src/main/resources/application.properties` si son distintas:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/prueba
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. Las tablas (`products`, `cards`) las crea Hibernate automáticamente al arrancar (`ddl-auto=update`).
   Los productos de prueba se cargan desde `src/main/resources/data.sql`:

   | productId | Nombre                 | Tipo   |
   |-----------|------------------------|--------|
   | `123456`  | Tarjeta Debito Clasica | DEBIT  |
   | `654321`  | Tarjeta Credito Gold   | CREDIT |

### Modelo de datos

**products**

| Columna      | Tipo         | Restricciones                 |
|--------------|--------------|-------------------------------|
| product_id   | VARCHAR(6)   | PK, exactamente 6 dígitos     |
| name         | VARCHAR(100) | NOT NULL                      |
| type         | VARCHAR(10)  | NOT NULL (`DEBIT`, `CREDIT`)  |

**cards**

| Columna        | Tipo          | Restricciones                              |
|----------------|---------------|--------------------------------------------|
| card_id        | UUID          | PK                                         |
| card_number    | VARCHAR(16)   | NOT NULL, **UNIQUE**                       |
| product_id     | VARCHAR(6)    | FK → products, NOT NULL                    |
| holder_name    | VARCHAR(100)  | NOT NULL                                   |
| issued_at      | DATE          | NOT NULL                                   |
| expires_at     | DATE          | NOT NULL (issued_at + 3 años)              |
| status         | VARCHAR(10)   | NOT NULL (`ISSUED`, `ACTIVE`, `BLOCKED`)   |
| blocked_at     | TIMESTAMP     | NULL si no está bloqueada                  |
| blocked_reason | VARCHAR(255)  | NULL si no está bloqueada                  |
| balance        | NUMERIC(15,2) | NOT NULL, inicial `0.00`                   |
| currency       | VARCHAR(3)    | NOT NULL, `USD`                            |

## Ejecutar

```bash
./mvnw clean spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

> Si usas VS Code, ejecuta siempre con `clean` o desactiva `java.autobuild`, porque el compilador del editor
> escribe en `target/classes` y puede dejar clases inválidas.

## Endpoints

| Método | Ruta                        | Descripción              | Respuestas          |
|--------|-----------------------------|--------------------------|---------------------|
| POST   | `/cards`                    | Emitir tarjeta           | 201, 400, 404       |
| POST   | `/cards/{cardId}/activate`  | Activar tarjeta          | 200, 400, 404, 409  |
| POST   | `/cards/{cardId}/block`     | Bloquear tarjeta         | 200, 400, 404       |
| GET    | `/cards/{cardId}`           | Consultar tarjeta        | 200, 400, 404       |

### Reglas de negocio

- `productId` debe tener exactamente 6 dígitos numéricos y existir en la base (si no, 404).
- `cardNumber` tiene 16 dígitos: los primeros 6 son el `productId` y los 10 restantes son aleatorios. Es único en la base.
- Una tarjeta nueva queda en estado `ISSUED`, con saldo `0.00 USD` y vencimiento a 3 años.
- **Activar**: solo desde `ISSUED`. Si ya está `ACTIVE` responde 200 (idempotente). Si está `BLOCKED` responde **409 Conflict**.
- **Bloquear**: cambia el estado a `BLOCKED` y registra `blockedAt` y `blockedReason`. Si ya está `BLOCKED` responde 200 (idempotente).

## Ejemplos de uso (curl)

### 1. Emitir tarjeta

```bash
curl -i -X POST http://localhost:8080/cards \
  -H "Content-Type: application/json" \
  -d '{"productId":"123456","holderName":"Ricardo Perez"}'
```

Respuesta `201 Created`:

```json
{
  "cardId": "3f2b8c1e-9a4d-4e7b-8c21-5d6f7a8b9c0d",
  "cardNumber": "1234568304719265",
  "status": "ISSUED",
  "expiresAt": "2029-10-19",
  "balance": 0.00,
  "currency": "USD"
}
```

Guarda el `cardId` para los siguientes pasos:

```bash
CARD_ID=3f2b8c1e-9a4d-4e7b-8c21-5d6f7a8b9c0d
```

### 2. Activar tarjeta

```bash
curl -i -X POST http://localhost:8080/cards/$CARD_ID/activate
```

Respuesta `200 OK`:

```json
{
  "cardId": "3f2b8c1e-9a4d-4e7b-8c21-5d6f7a8b9c0d",
  "status": "ACTIVE"
}
```

### 3. Consultar tarjeta

```bash
curl -i http://localhost:8080/cards/$CARD_ID
```

Respuesta `200 OK`:

```json
{
  "cardId": "3f2b8c1e-9a4d-4e7b-8c21-5d6f7a8b9c0d",
  "cardNumber": "1234568304719265",
  "holderName": "Ricardo Perez",
  "status": "ACTIVE",
  "blockedAt": "",
  "blockedReason": "",
  "expiresAt": "2029-10-19",
  "balance": 0.00,
  "currency": "USD"
}
```

### 4. Bloquear tarjeta

```bash
curl -i -X POST http://localhost:8080/cards/$CARD_ID/block \
  -H "Content-Type: application/json" \
  -d '{"reason":"SECURITY_INCONSISTENCY"}'
```

Respuesta `200 OK`:

```json
{
  "cardId": "3f2b8c1e-9a4d-4e7b-8c21-5d6f7a8b9c0d",
  "status": "BLOCKED"
}
```

### Casos de error

```bash
# 400 - productId inválido
curl -i -X POST http://localhost:8080/cards \
  -H "Content-Type: application/json" \
  -d '{"productId":"12A45","holderName":"Ricardo Perez"}'

# 404 - producto inexistente
curl -i -X POST http://localhost:8080/cards \
  -H "Content-Type: application/json" \
  -d '{"productId":"999999","holderName":"Ricardo Perez"}'

# 409 - activar una tarjeta bloqueada
curl -i -X POST http://localhost:8080/cards/$CARD_ID/activate

# 404 - tarjeta inexistente
curl -i http://localhost:8080/cards/00000000-0000-0000-0000-000000000000
```

Formato de los errores:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Una tarjeta BLOCKED no puede activarse",
  "timestamp": "2026-10-19T15:45:10.123"
}
```

## Estructura del proyecto

```
src/main/java/com/prueba/
├── controller/   CardController (endpoints REST)
├── service/      CardService (casos de uso, generación de cardNumber)
├── domain/       Product, Card (entidades JPA y reglas de estado), enums
├── repository/   ProductRepository, CardRepository
├── dto/          Requests y responses de la API
└── exception/    Excepciones de negocio y GlobalExceptionHandler
src/main/resources/
├── application.properties
└── data.sql      Productos de prueba
```
