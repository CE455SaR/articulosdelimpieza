package com.exa.base.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exa.base.mapper.RoleMapper;
import com.exa.base.model.Role;

@Repository
public class RoleDao {

    @Autowired
    private JdbcTemplate postgresTemplate;

    public List<Role> listaRoles() {
        List<Role> lista = new ArrayList<>();
        String query = "SELECT id_rol, nombre FROM rol ORDER BY nombre";

        try {
            lista = postgresTemplate.query(query, new RoleMapper());
        } catch (Exception e) {
            System.err.println("ERROR: RoleDao | listaRoles | " + e.getMessage());
        }

        return lista;
    }

    public Map<Integer, Integer> mapaRolesUsuario(int idUsuario) {
        Map<Integer, Integer> mapaRoles = new HashMap<>();
        String query = "SELECT id_rol FROM rol_usuario WHERE id_usuario = ?";

        try {
            List<Integer> roles = postgresTemplate.queryForList(query, Integer.class, idUsuario);
            for (Integer rolId : roles) {
                mapaRoles.put(rolId, rolId);
            }
        } catch (Exception e) {
            System.err.println("ERROR: RoleDao | mapaRolesUsuario | " + e.getMessage());
        }

        return mapaRoles;
    }

    public Role buscarPorId(int idRol) {
        String query = "SELECT id_rol, nombre FROM rol WHERE id_rol = ?";
        try {
            return postgresTemplate.queryForObject(query, new RoleMapper(), idRol);
        } catch (Exception e) {
            System.err.println("ERROR: RoleDao | buscarPorId | " + e.getMessage());
            return null;
        }
    }
}