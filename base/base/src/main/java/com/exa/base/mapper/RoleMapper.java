package com.exa.base.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.exa.base.model.Role;

public class RoleMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Role objeto = new Role();

        // Ajustado a los nombres de columnas de tu BD
        objeto.setId(rs.getInt("id_rol"));  // Cambiado de "ID" a "id_rol"
        objeto.setNombre(rs.getString("nombre"));  // Cambiado de "NOMBRE" a "nombre"

        return objeto;
    }
}