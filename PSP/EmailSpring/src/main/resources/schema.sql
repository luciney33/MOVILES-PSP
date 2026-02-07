-- Eliminar tablas en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS entrenamiento_ejercicios CASCADE;
DROP TABLE IF EXISTS ejercicios CASCADE;
DROP TABLE IF EXISTS entrenamientos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- Tabla de usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    nombre VARCHAR(100),
    activo BOOLEAN DEFAULT FALSE,
    rol VARCHAR(20),
    codigo_activacion VARCHAR(255),
    expiracion_codigo TIMESTAMP,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_secret VARCHAR(255)
);

-- Tabla de entrenamientos
CREATE TABLE entrenamientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de ejercicios
CREATE TABLE ejercicios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo_entrenamiento VARCHAR(100) NOT NULL,
    imagen_url VARCHAR(500),
    descripcion VARCHAR(200)
);

-- Tabla intermedia para relación muchos a muchos
CREATE TABLE entrenamiento_ejercicios (
    entrenamiento_id BIGINT NOT NULL,
    ejercicio_id BIGINT NOT NULL,
    series INT,
    repeticiones INT,
    PRIMARY KEY (entrenamiento_id, ejercicio_id),
    FOREIGN KEY (entrenamiento_id) REFERENCES entrenamientos(id) ON DELETE CASCADE,
    FOREIGN KEY (ejercicio_id) REFERENCES ejercicios(id) ON DELETE CASCADE
);

