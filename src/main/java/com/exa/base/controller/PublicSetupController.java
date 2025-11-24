package com.exa.base.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PublicSetupController {
    
    private final JdbcTemplate jdbcTemplate;
    
    public PublicSetupController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @GetMapping("/public-setup")
    @ResponseBody
    public String publicSetup() {
        try {
            System.out.println("=== EJECUTANDO PUBLIC SETUP ===");
            
            // 1. Insertar roles
            jdbcTemplate.execute("INSERT INTO rol (nombre) VALUES ('PROVEEDOR') ON CONFLICT (nombre) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO rol (nombre) VALUES ('CLIENTE') ON CONFLICT (nombre) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO rol (nombre) VALUES ('EMPLEADO') ON CONFLICT (nombre) DO NOTHING");
            System.out.println("Roles insertados");
            
            // 2. Insertar usuarios
            jdbcTemplate.execute("INSERT INTO usuario (email, contrasena, activo) VALUES " +
                "('proveedor@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true) " +
                "ON CONFLICT (email) DO NOTHING");
            
            jdbcTemplate.execute("INSERT INTO usuario (email, contrasena, activo) VALUES " +
                "('cliente@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true) " +
                "ON CONFLICT (email) DO NOTHING");
            
            jdbcTemplate.execute("INSERT INTO usuario (email, contrasena, activo) VALUES " +
                "('empleado@test.com', '$2a$10$D61fHS9f4hn0i2H6QoFH3uqcBw4NiLCRyXUxGhzI.NprILOlBzxSu', true) " +
                "ON CONFLICT (email) DO NOTHING");
            System.out.println("Usuarios insertados");
            
            // 3. Asignar roles
            jdbcTemplate.execute("INSERT INTO rol_usuario (id_usuario, id_rol) " +
                "SELECT u.id_usuario, r.id_rol FROM usuario u, rol r " +
                "WHERE u.email = 'proveedor@test.com' AND r.nombre = 'PROVEEDOR' " +
                "ON CONFLICT DO NOTHING");
            
            jdbcTemplate.execute("INSERT INTO rol_usuario (id_usuario, id_rol) " +
                "SELECT u.id_usuario, r.id_rol FROM usuario u, rol r " +
                "WHERE u.email = 'cliente@test.com' AND r.nombre = 'CLIENTE' " +
                "ON CONFLICT DO NOTHING");
            
            jdbcTemplate.execute("INSERT INTO rol_usuario (id_usuario, id_rol) " +
                "SELECT u.id_usuario, r.id_rol FROM usuario u, rol r " +
                "WHERE u.email = 'empleado@test.com' AND r.nombre = 'EMPLEADO' " +
                "ON CONFLICT DO NOTHING");
            System.out.println("Roles asignados");
            
            System.out.println("=== PUBLIC SETUP COMPLETADO ===");
            
            return "✅ <h2>Datos de prueba insertados correctamente!</h2>" +
                   "<p><strong>Ahora puedes iniciar sesión con:</strong></p>" +
                   "<ul>" +
                   "<li><strong>proveedor@test.com</strong> / <strong>123456</strong> (Rol: PROVEEDOR)</li>" +
                   "<li><strong>cliente@test.com</strong> / <strong>123456</strong> (Rol: CLIENTE)</li>" +
                   "<li><strong>empleado@test.com</strong> / <strong>123456</strong> (Rol: EMPLEADO)</li>" +
                   "</ul>" +
                   "<p><a href='/login'>Ir al login</a></p>";
                   
        } catch (Exception e) {
            System.out.println("Error en public setup: " + e.getMessage());
            return "❌ <h2>Error insertando datos</h2>" +
                   "<p>" + e.getMessage() + "</p>" +
                   "<p><a href='/login'>Ir al login</a></p>";
        }
    }
}