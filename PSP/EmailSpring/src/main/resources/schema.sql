DROP TABLE IF EXISTS entrenamientos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

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

CREATE TABLE entrenamientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
CREATE TABLE ejercicios (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(255) NOT NULL,
                            tipo_entrenamiento VARCHAR(100) NOT NULL,
                            imagen_url VARCHAR(500),
                            descripcion TEXT
);

CREATE TABLE entrenamiento_ejercicios (
                                          entrenamiento_id BIGINT NOT NULL,
                                          ejercicio_id BIGINT NOT NULL,
                                          orden INT DEFAULT 0,
                                          series INT,
                                          repeticiones INT,
                                          PRIMARY KEY (entrenamiento_id, ejercicio_id),
                                          FOREIGN KEY (entrenamiento_id) REFERENCES entrenamientos(id) ON DELETE CASCADE,
                                          FOREIGN KEY (ejercicio_id) REFERENCES ejercicios(id) ON DELETE CASCADE
);
