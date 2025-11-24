package com.exa.base.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetupController {
    
    private final JdbcTemplate jdbcTemplate;
    
    public SetupController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @GetMapping("/setup-data")
    public String setupData() {
        try {
            // 1. Verificar tablas
            System.out.println("=== INICIANDO INSERCIÓN DE DATOS ===");
            
            // 2. Insertar roles
            jdbcTemplate.execute("INSERT INTO rol (nombre) VALUES ('PROVEEDOR') ON CONFLICT (nombre) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO rol (nombre) VALUES ('CLIENTE') ON CONFLICT (nombre) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO rol (nombre) VALUES ('EMPLEADO') ON CONFLICT (nombre) DO NOTHING");
            System.out.println("Roles insertados");
            
            // 3. Insertar usuario (contraseña: 123456)
            jdbcTemplate.execute("INSERT INTO usuario (email, contrasena, activo) VALUES " +
                "('proveedor@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true) " +
                "ON CONFLICT (email) DO NOTHING");
            System.out.println("Usuario proveedor insertado");
            
            // 4. Asignar rol
            jdbcTemplate.execute("INSERT INTO rol_usuario (id_usuario, id_rol) " +
                "SELECT u.id_usuario, r.id_rol FROM usuario u, rol r " +
                "WHERE u.email = 'proveedor@test.com' AND r.nombre = 'PROVEEDOR' " +
                "ON CONFLICT DO NOTHING");
            System.out.println("Rol asignado");
            
            // 5. Insertar más usuarios de prueba
            jdbcTemplate.execute("INSERT INTO usuario (email, contrasena, activo) VALUES " +
                "('cliente@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true) " +
                "ON CONFLICT (email) DO NOTHING");
            
            jdbcTemplate.execute("INSERT INTO rol_usuario (id_usuario, id_rol) " +
                "SELECT u.id_usuario, r.id_rol FROM usuario u, rol r " +
                "WHERE u.email = 'cliente@test.com' AND r.nombre = 'CLIENTE' " +
                "ON CONFLICT DO NOTHING");
            
            System.out.println("=== DATOS INSERTADOS CORRECTAMENTE ===");
            
            return "✅ Datos insertados correctamente!<br><br>" +
                   "Ahora puedes iniciar sesión con:<br>" +
                   "• <strong>proveedor@test.com</strong> / <strong>123456</strong> (Rol: PROVEEDOR)<br>" +
                   "• <strong>cliente@test.com</strong> / <strong>123456</strong> (Rol: CLIENTE)";
                   
        } catch (Exception e) {
            System.out.println("Error insertando datos: " + e.getMessage());
            return "❌ Error insertando datos: " + e.getMessage();
        }
    }
}