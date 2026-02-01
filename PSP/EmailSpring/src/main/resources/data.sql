INSERT INTO usuarios (username, password, email, nombre, activo, rol, two_factor_enabled, two_factor_secret) VALUES
('admin', '$2a$12$tGuiIhlUgGscg/DcmVF6ceWxrh8u4is8BQuSIJup91CinxD1broqq', 'admin@gmail.com', 'Administrador', true, 'ADMIN', false, NULL),
('user', '$2a$12$ZxKzvqN72HVtpClh0pTqcOB.On0UU8l0bVb713kvUnK5cp/iCBq.q', 'usuario@gmail.com', 'Usuario', true, 'USER', false, NULL);

INSERT INTO entrenamientos (usuario_id, nombre, descripcion) VALUES
(1, 'Entrenamiento admin 1', 'Rutina para admin'),
(2, 'Entrenamiento user 1', 'Rutina para usuario');

