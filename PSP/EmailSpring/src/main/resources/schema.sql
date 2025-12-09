CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    nombre VARCHAR(100),
    rol VARCHAR(20),
    activo BOOLEAN DEFAULT FALSE,
    codigoActivacion VARCHAR(255),
    expiracionCodigo TIMESTAMP
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
