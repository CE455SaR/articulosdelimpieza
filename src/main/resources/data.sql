-- Insertar roles básicos
INSERT INTO rol (nombre) VALUES 
('PROVEEDOR'),
('CLIENTE'), 
('EMPLEADO')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar usuario de prueba (contraseña: 123456)
INSERT INTO usuario (email, contrasena, activo) VALUES 
('proveedor@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true)
ON CONFLICT (email) DO NOTHING;

-- Asignar rol
INSERT INTO rol_usuario (id_usuario, id_rol) 
SELECT u.id_usuario, r.id_rol 
FROM usuario u, rol r 
WHERE u.email = 'proveedor@test.com' AND r.nombre = 'PROVEEDOR'
ON CONFLICT DO NOTHING;

-- Insertar más usuarios de prueba
INSERT INTO usuario (email, contrasena, activo) VALUES 
('cliente@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true),
('empleado@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true)
ON CONFLICT (email) DO NOTHING;

-- Asignar roles a los otros usuarios
INSERT INTO rol_usuario (id_usuario, id_rol) 
SELECT u.id_usuario, r.id_rol 
FROM usuario u, rol r 
WHERE u.email = 'cliente@test.com' AND r.nombre = 'CLIENTE'
ON CONFLICT DO NOTHING;

INSERT INTO rol_usuario (id_usuario, id_rol) 
SELECT u.id_usuario, r.id_rol 
FROM usuario u, rol r 
WHERE u.email = 'empleado@test.com' AND r.nombre = 'EMPLEADO'
ON CONFLICT DO NOTHING;