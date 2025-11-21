package com.exa.base.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.exa.base.model.UsuarioRole;

public class UsuarioRoleMapper implements RowMapper<UsuarioRole> {
    @Override
    public UsuarioRole mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        UsuarioRole objeto = new UsuarioRole();

        // Ajustado a los nombres de columnas de tu BD
        objeto.setUsuarioId(rs.getInt("id_usuario"));  // Cambiado de "USUARIO_ID" a "id_usuario"
        objeto.setRoleId(rs.getInt("id_rol"));  // Cambiado de "ROLE_ID" a "id_rol"

        return objeto;
    }
}