INSERT INTO usuarios (username, password, email, nombre, activo, rol, two_factor_enabled, two_factor_secret) VALUES
('admin', '$2a$12$tGuiIhlUgGscg/DcmVF6ceWxrh8u4is8BQuSIJup91CinxD1broqq', 'admin@gmail.com', 'Administrador', true, 'ADMIN', false, NULL),
('user', '$2a$12$ZxKzvqN72HVtpClh0pTqcOB.On0UU8l0bVb713kvUnK5cp/iCBq.q', 'usuario@gmail.com', 'Usuario', true, 'USER', false, NULL);

INSERT INTO entrenamientos (usuario_id, nombre, descripcion) VALUES
(1, 'Entrenamiento Pecho, Hombro, Triceps', 'Rutina tren superior'),
(2, 'Entrenamiento Pierna', 'Rutina pierna'),
(1, 'Entrenamiento Espalda, Biceps', 'Rutina tren superior');

INSERT INTO ejercicios (nombre, tipo_entrenamiento, imagen_url, descripcion)
VALUES ('Press de Banca', 'Pecho', '/images/ejercicios/PressBanca.gif', 'Ejercicio básico para pectoral'),
       ('Pecho Cerrado', 'Pecho', '/images/ejercicios/PechoCerrado.gif', 'Ejercicio básico para pectoral'),
       ('Sentadilla', 'Piernas', '/images/ejercicios/Sentadilla.gif', 'Ejercicio fundamental para tren inferior'),
       ('Hacka', 'Piernas', '/images/ejercicios/Hacka.gif', 'Ejercicio fundamental para tren inferior'),
       ('Peso Muerto', 'Piernas', '/images/ejercicios/PesoMuerto.gif', 'Ejercicio compuesto'),
       ('Press Militar', 'Hombros', '/images/ejercicios/PressMilitar.gif', 'Desarrollo de hombros'),
       ('Jalon Al Pecho', 'Espalda', '/images/ejercicios/JalonAlPecho.gif', 'Desarrollo de escapulas'),
       ('Remo', 'Espalda', '/images/ejercicios/Remo.gif', 'Desarrollo de escapulas'),
       ('Fondos', 'Triceps', '/images/ejercicios/Fondos.gif', 'Ejercicio de triceps'),
       ('Triceps', 'Triceps', '/images/ejercicios/Triceps.gif', 'Ejercicio de triceps'),
       ('Vuelos Laterales', 'Hombros', '/images/ejercicios/VuelosLaterales.gif', 'Desarrollo de hombros'),
       ('Press Frances', 'Triceps', '/images/ejercicios/PressFrances.gif', 'Ejercicio de triceps'),
       ('Curl de Bíceps', 'Brazos', '/images/ejercicios/BicepsBarraZ.gif', 'Aislamiento de bíceps'),
        ('Bíceps', 'Brazos', '/images/ejercicios/BicepsMancuerna.gif', 'Aislamiento de bíceps');

INSERT INTO entrenamiento_ejercicios (entrenamiento_id, ejercicio_id, series, repeticiones)
VALUES
(1, 1,  3, 8),
(1, 2,  3, 6),
(1, 6,  4, 8),
(1, 10,  3, 8),
(1, 9,  3, 6),
(1, 11,  3, 8),
(1, 12,  3, 8),

(2, 3, 3,  8),
(2, 4, 3,  6),
(2, 5, 3,  8),

(3, 7, 3,  8),
(3, 8, 3,  6),
(3, 13, 3,  8),
(3, 14, 3,  8);

