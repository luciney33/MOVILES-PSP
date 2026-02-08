# Testing Guide - Secret Management System

## Manual Testing with cURL

### 1. Start the Application
```bash
cd PSP/SpringApiRest
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 2. Register Users

**Register User 1 (Alice):**
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "alicePass123",
    "email": "alice@example.com",
    "nombre": "Alice Smith",
    "rol": "USER"
  }'
```

**Register User 2 (Bob):**
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "password": "bobPass123",
    "email": "bob@example.com",
    "nombre": "Bob Jones",
    "rol": "USER"
  }'
```

### 3. Login

**Login as Alice:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "alice",
    "password": "alicePass123"
  }'
```

The `-c cookies.txt` flag saves the session cookie for subsequent requests.

### 4. Create a Secret

**Alice creates a secret:**
```bash
curl -X POST http://localhost:8080/api/secretos \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "titulo": "Project Alpha Credentials",
    "contenido": "Username: admin, Password: super_secret_123",
    "password": "alicePass123"
  }'
```

Response will include the secret ID (e.g., id: 1)

### 5. List Secrets

**View all secrets accessible to Alice:**
```bash
curl -X GET http://localhost:8080/api/secretos \
  -b cookies.txt
```

### 6. View a Secret

**Alice views her secret:**
```bash
curl -X POST http://localhost:8080/api/secretos/1/ver \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "password": "alicePass123"
  }'
```

This will decrypt and return the secret content.

### 7. Share a Secret

**Alice shares secret with Bob (assuming Bob's ID is 2):**
```bash
curl -X POST http://localhost:8080/api/secretos/1/compartir \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "destinatarioId": 2,
    "password": "alicePass123"
  }'
```

### 8. View Shared Secret (as Bob)

**Login as Bob:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies_bob.txt \
  -d '{
    "username": "bob",
    "password": "bobPass123"
  }'
```

**Bob lists his secrets (should see Alice's shared secret):**
```bash
curl -X GET http://localhost:8080/api/secretos \
  -b cookies_bob.txt
```

**Bob views the shared secret:**
```bash
curl -X POST http://localhost:8080/api/secretos/1/ver \
  -H "Content-Type: application/json" \
  -b cookies_bob.txt \
  -d '{
    "password": "bobPass123"
  }'
```

### 9. Stop Sharing

**Alice stops sharing with Bob:**
```bash
curl -X DELETE http://localhost:8080/api/secretos/1/compartir/2 \
  -b cookies.txt
```

After this, Bob will no longer be able to view secret ID 1.

## Postman Collection

You can also import this into Postman for easier testing:

```json
{
  "info": {
    "name": "Secret Management API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Register User",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"alice\",\n  \"password\": \"alicePass123\",\n  \"email\": \"alice@example.com\",\n  \"nombre\": \"Alice Smith\",\n  \"rol\": \"USER\"\n}"
        },
        "url": {"raw": "http://localhost:8080/api/auth/registro"}
      }
    },
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"alice\",\n  \"password\": \"alicePass123\"\n}"
        },
        "url": {"raw": "http://localhost:8080/api/auth/login"}
      }
    },
    {
      "name": "Create Secret",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"titulo\": \"My Secret\",\n  \"contenido\": \"Confidential information\",\n  \"password\": \"alicePass123\"\n}"
        },
        "url": {"raw": "http://localhost:8080/api/secretos"}
      }
    },
    {
      "name": "List Secrets",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/secretos"}
      }
    },
    {
      "name": "View Secret",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"password\": \"alicePass123\"\n}"
        },
        "url": {"raw": "http://localhost:8080/api/secretos/1/ver"}
      }
    },
    {
      "name": "Share Secret",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"destinatarioId\": 2,\n  \"password\": \"alicePass123\"\n}"
        },
        "url": {"raw": "http://localhost:8080/api/secretos/1/compartir"}
      }
    },
    {
      "name": "Stop Sharing",
      "request": {
        "method": "DELETE",
        "url": {"raw": "http://localhost:8080/api/secretos/1/compartir/2"}
      }
    }
  ]
}
```

## Security Testing Scenarios

### Test 1: Signature Verification
1. Create a secret as Alice
2. Manually tamper with the `contenido_cifrado` in the database
3. Try to view the secret - should fail with "Firma digital inválida"

### Test 2: Access Control
1. Create a secret as Alice
2. Login as Bob
3. Try to view Alice's secret without sharing - should fail with "No tiene permisos"

### Test 3: Owner-Only Sharing
1. Alice shares secret with Bob
2. Login as Bob
3. Try to share the secret with a third user - should fail (only owner can share)

### Test 4: Password Protection
1. Create a secret as Alice
2. Try to view with wrong password - should fail with decryption error

### Test 5: Duplicate Sharing Prevention
1. Alice shares secret with Bob
2. Try to share same secret with Bob again - should fail

## Expected Cryptographic Behavior

### RSA Key Generation
- Keys should be 2048 bits
- Public key stored as X.509 encoded bytes
- Private key encrypted before storage

### AES Encryption
- Each secret uses unique AES-256 key
- GCM mode provides authentication
- IV is random and prepended to ciphertext

### Digital Signatures
- SHA256withRSA algorithm
- Signature computed over encrypted content
- Verified before any decryption

### PBKDF2 Key Derivation
- 65,536 iterations
- 256-bit output key
- Random 16-byte salt per user

## Performance Testing

Test the system with multiple users and secrets:

```bash
# Create 10 users
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/auth/registro \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"user$i\",\"password\":\"pass$i\",\"email\":\"user$i@example.com\",\"nombre\":\"User $i\",\"rol\":\"USER\"}"
done

# Each user creates 5 secrets
# Each user shares secrets with 2 other users
# Measure response times for viewing shared secrets
```

## Database Inspection

You can access the H2 console to inspect the database:

1. Navigate to: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:gymdb`
3. Username: `sa`
4. Password: (leave empty)

Useful queries:
```sql
-- View all users
SELECT id, username, email, LENGTH(clave_publica) as pub_key_size, 
       LENGTH(clave_privada_cifrada) as priv_key_size 
FROM usuarios;

-- View all secrets
SELECT id, autor_id, titulo, LENGTH(contenido_cifrado) as content_size,
       LENGTH(clave_simetrica_cifrada) as key_size, LENGTH(firma) as sig_size
FROM secretos;

-- View all shared secrets
SELECT * FROM secretos_compartidos;
```

## Troubleshooting

### Issue: "Usuario no autenticado"
**Solution**: Make sure to include the session cookie in requests after login

### Issue: "Firma digital inválida"
**Solution**: The content has been tampered with or there's a mismatch in encryption/decryption

### Issue: "Error al descifrar"
**Solution**: Wrong password provided or corrupted encrypted data

### Issue: Application won't start
**Solution**: Check that port 8080 is not already in use
