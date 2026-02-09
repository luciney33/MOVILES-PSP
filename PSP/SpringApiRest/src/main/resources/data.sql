INSERT INTO entrenamiento (usuarioId, nombre, descripcion) VALUES
(1, 'Entrenamiento admin 1', 'Rutina para admin'),
(2, 'Entrenamiento user 1', 'Rutina para usuario');

INSERT INTO ejercicio (entrenamientoId, nombre, repeticiones, series) VALUES
(1, 'Flexiones', 15, 3),
(1, 'Sentadillas', 20, 3),
(2, 'Abdominales', 25, 2),
(2, 'Lagartijas', 10, 2);