package com.exa.base.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.exa.base.model.Usuario;

public class UsuarioMapper implements RowMapper<Usuario> {
    @Override
    public Usuario mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();
        
        // Mapeo de campos básicos
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setActivo(rs.getBoolean("activo"));
        
        // Mapeo de campos adicionales
        if (columnExists(rs, "telefono")) {
            usuario.setTelefono(rs.getString("telefono"));
        }
        if (columnExists(rs, "direccion")) {
            usuario.setDireccion(rs.getString("direccion"));
        }
        if (columnExists(rs, "user_name")) {
            usuario.setUserName(rs.getString("user_name"));
        }
        
        return usuario;
    }
    
    // Método auxiliar para verificar si existe una columna en el ResultSet
    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}