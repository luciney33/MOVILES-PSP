# Secret Management System - Implementation Documentation

## Overview
This system implements a cryptographic secret management solution using hybrid encryption (RSA + AES) with digital signatures to ensure confidentiality, integrity, and authenticity of shared secrets.

## Architecture

### Cryptographic Components

#### Key Generation
- **RSA-2048**: Used for asymmetric encryption of AES keys
- **AES-256**: Used for symmetric encryption of secret content
- **Key Storage**: Public keys stored as-is, private keys encrypted with PBKDF2-derived keys

#### Encryption Scheme
1. **Content Encryption**: AES-256/GCM (Galois/Counter Mode)
   - Provides authenticated encryption
   - Random IV per encryption
   - 128-bit authentication tag

2. **Key Encryption**: RSA/ECB/OAEPWithSHA-256AndMGF1Padding
   - OAEP padding prevents padding oracle attacks
   - SHA-256 for mask generation function

3. **Digital Signatures**: SHA256withRSA
   - Signs encrypted content
   - Verifies authenticity and prevents tampering

4. **Password Protection**: PBKDF2WithHmacSHA256
   - 65,536 iterations
   - 256-bit key length
   - Random 16-byte salt

## Database Schema

### usuarios (Users)
```sql
- id: BIGINT (Primary Key)
- username: VARCHAR(50) UNIQUE
- password: VARCHAR(255) (BCrypt hashed)
- email: VARCHAR(100)
- clave_publica: BLOB (RSA public key)
- clave_privada_cifrada: BLOB (Encrypted RSA private key)
- fecha_registro: TIMESTAMP
```

### secretos (Secrets)
```sql
- id: BIGINT (Primary Key)
- autor_id: BIGINT (Foreign Key → usuarios.id)
- titulo: VARCHAR(255)
- contenido_cifrado: BLOB (AES encrypted content)
- clave_simetrica_cifrada: BLOB (RSA encrypted AES key)
- firma: BLOB (Digital signature)
- fecha_creacion: TIMESTAMP
```

### secretos_compartidos (Shared Secrets)
```sql
- id: BIGINT (Primary Key)
- secreto_id: BIGINT (Foreign Key → secretos.id)
- destinatario_id: BIGINT (Foreign Key → usuarios.id)
- clave_simetrica_cifrada_destinatario: BLOB (AES key encrypted for recipient)
- fecha_compartido: TIMESTAMP
- UNIQUE (secreto_id, destinatario_id)
```

## API Endpoints

### Authentication
**POST /api/auth/registro**
```json
Request:
{
  "username": "user1",
  "password": "securePassword123",
  "email": "user1@example.com",
  "nombre": "User One",
  "rol": "USER"
}

Response:
{
  "id": 1,
  "username": "user1",
  "email": "user1@example.com",
  "nombre": "User One",
  "rol": "USER"
}
```

### Secret Management

**POST /api/secretos** (Create Secret)
```json
Request:
{
  "titulo": "My Secret",
  "contenido": "This is confidential information",
  "password": "userPassword"
}

Response:
{
  "id": 1,
  "titulo": "My Secret",
  "autorUsername": "user1",
  "fechaCreacion": "2026-02-08T01:30:00",
  "esPropio": true
}
```

**GET /api/secretos** (List Secrets)
```json
Response:
[
  {
    "id": 1,
    "titulo": "My Secret",
    "autorUsername": "user1",
    "fechaCreacion": "2026-02-08T01:30:00",
    "esPropio": true
  },
  {
    "id": 2,
    "titulo": "Shared Secret",
    "autorUsername": "user2",
    "fechaCreacion": "2026-02-08T01:25:00",
    "esPropio": false
  }
]
```

**POST /api/secretos/{id}/ver** (View Secret)
```json
Request:
{
  "password": "userPassword"
}

Response:
{
  "id": 1,
  "titulo": "My Secret",
  "contenido": "This is confidential information",
  "autorUsername": "user1"
}
```

**POST /api/secretos/{id}/compartir** (Share Secret)
```json
Request:
{
  "destinatarioId": 2,
  "password": "ownerPassword"
}

Response: "Secreto compartido exitosamente"
```

**DELETE /api/secretos/{id}/compartir/{destinatarioId}** (Unshare Secret)
```
Response: "Secreto dejado de compartir exitosamente"
```

## Security Workflow

### 1. User Registration
```
1. User provides: username, password, email, nombre
2. System generates RSA-2048 key pair
3. Private key encrypted with PBKDF2(password, random_salt)
4. Public key and encrypted private key stored in database
5. Password hashed with BCrypt before storage
```

### 2. Creating a Secret
```
1. User provides: titulo, contenido, password
2. System generates random AES-256 key
3. Content encrypted with AES-256/GCM
4. User's private key decrypted using password
5. Encrypted content signed with private key
6. AES key encrypted with user's public key
7. All stored in database (contenidoCifrado, claveSimétricaCifrada, firma)
```

### 3. Viewing a Secret
```
1. User provides: secreto_id, password
2. System retrieves secret from database
3. Author's public key used to verify signature
4. If signature invalid, operation fails
5. User's private key decrypted using password
6. AES key decrypted using private key
7. Content decrypted using AES key
8. Decrypted content returned to user
```

### 4. Sharing a Secret
```
1. Owner provides: secreto_id, destinatario_id, password
2. System verifies ownership
3. Owner's private key decrypted using password
4. AES key decrypted using owner's private key
5. Recipient's public key retrieved from database
6. AES key re-encrypted with recipient's public key
7. Encrypted key stored in secretos_compartidos
```

## Security Features

### Confidentiality
- **AES-256/GCM**: Military-grade symmetric encryption
- **RSA-2048**: Strong asymmetric encryption for key exchange
- **No plaintext storage**: All sensitive data encrypted at rest

### Integrity
- **Digital Signatures**: SHA256withRSA ensures content hasn't been modified
- **GCM Mode**: Provides authenticated encryption with built-in integrity check
- **Signature Verification**: Performed before decryption

### Authenticity
- **Digital Signatures**: Proves secret author identity
- **Public Key Infrastructure**: RSA keys uniquely identify users
- **Non-repudiation**: Author cannot deny creating a signed secret

### Key Management
- **Secure Key Storage**: Private keys never stored in plaintext
- **Password-Based Encryption**: PBKDF2 with 65,536 iterations
- **Key Separation**: Different AES key for each secret
- **Secure Key Sharing**: RSA public key encryption for sharing

## Best Practices Implemented

1. **Constructor Injection**: Used throughout for better testability
2. **Transaction Management**: Proper @Transactional boundaries
3. **Exception Handling**: Comprehensive error handling with meaningful messages
4. **Separation of Concerns**: Clear layering (Controller → Service → Repository)
5. **Secure Defaults**: OAEP padding, GCM mode, strong key sizes
6. **Random IVs**: New initialization vector for each encryption operation
7. **Password Hashing**: BCrypt for irreversible password storage
8. **Lazy Loading**: Configured to prevent N+1 queries

## Important Notes

### Why Signature in SecretoEntity?
The `firma` field belongs in `SecretoEntity` (not `UsuarioEntity`) because:
- Each secret has its own unique signature
- Signature is computed over the specific encrypted content
- Different secrets have different signatures even from same author
- Enables per-secret integrity verification

### Password Requirements
- User password: Used to encrypt/decrypt private RSA key (never stored plaintext)
- BCrypt hash: Stored in database for authentication
- PBKDF2: Derives AES key from password for private key encryption

### Performance Considerations
- RSA operations are expensive: Used only for AES key encryption
- AES operations are fast: Used for actual content encryption
- Signature verification: Always performed before decryption
- Query optimization: @EntityGraph and JOIN FETCH for related entities

## Error Handling

Common errors and their meanings:
- "Usuario no encontrado": Invalid user ID
- "Secreto no encontrado": Invalid secret ID or no permission
- "Firma digital inválida": Content has been tampered with
- "No tiene permisos para ver este secreto": User not owner or recipient
- "Solo el autor puede compartir el secreto": Only owner can share
- "El secreto ya está compartido con este usuario": Duplicate sharing attempt

## Testing

Run tests with:
```bash
cd PSP/SpringApiRest
mvn clean test
```

All tests should pass, including:
- Spring context loading
- Entity validation
- Repository operations
- Service layer logic
