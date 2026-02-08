CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    nombre VARCHAR(100),
    rol VARCHAR(20)
);

CREATE TABLE entrenamiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuarioId INT NOT NULL,
    nombre VARCHAR(100),
    descripcion VARCHAR(255)
);

CREATE TABLE ejercicio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entrenamientoId INT NOT NULL,
    nombre VARCHAR(100),
    repeticiones INT,
    series INT
);

-- Tables for Cryptographic Secret Management System
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    clave_publica BLOB NOT NULL,
    clave_privada_cifrada BLOB NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE secretos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    autor_id BIGINT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    contenido_cifrado BLOB NOT NULL,
    clave_simetrica_cifrada BLOB NOT NULL,
    firma BLOB NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE secretos_compartidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    secreto_id BIGINT NOT NULL,
    destinatario_id BIGINT NOT NULL,
    clave_simetrica_cifrada_destinatario BLOB NOT NULL,
    fecha_compartido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (secreto_id) REFERENCES secretos(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT unique_compartido UNIQUE (secreto_id, destinatario_id)
);
