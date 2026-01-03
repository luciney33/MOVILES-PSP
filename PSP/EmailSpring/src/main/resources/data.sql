INSERT INTO usuarios (username, password, email, nombre, activo, rol, two_factor_enabled, two_factor_secret) VALUES
('admin', '$2a$10$JLwB82ikhv1.1BQfISy0QuT7D1.cIP9DaXBeVtoDT9vItR/DGMxoC', 'admin@gmail.com', 'Administrador', true, 'ADMIN', false, NULL),
('user', '$2a$10$e635KEsmmDkMTcqjFjucMe5JjnGI.Xbq3KxapficixxxY0St9g3EG', 'usuario@gmail.com', 'Usuario', true, 'USER', false, NULL);

INSERT INTO entrenamientos (usuario_id, nombre, descripcion) VALUES
(1, 'Entrenamiento admin 1', 'Rutina para admin'),
(2, 'Entrenamiento user 1', 'Rutina para usuario');

