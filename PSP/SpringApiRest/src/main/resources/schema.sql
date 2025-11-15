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
INSERT INTO entrenamiento (usuarioId, nombre, descripcion) VALUES
(1, 'Entrenamiento admin 1', 'Rutina para admin'),
(2, 'Entrenamiento user 1', 'Rutina para usuario');

INSERT INTO ejercicio (entrenamientoId, nombre, repeticiones, series) VALUES
(1, 'Flexiones', 15, 3),
(1, 'Sentadillas', 20, 3),
(2, 'Abdominales', 25, 2),
(2, 'Lagartijas', 10, 2);