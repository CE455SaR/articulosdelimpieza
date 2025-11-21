package com.exa.base.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.exa.base.model.Usuario;

@Repository
public class UsuarioDao {

    @Autowired
    private JdbcTemplate postgresTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean buscaEmail(String email) {
        String query = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        try {
            Integer count = postgresTemplate.queryForObject(query, Integer.class, email);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("ERROR en buscaEmail: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean grabaUsuario(Usuario usuario, Integer[] roles) {
        if (buscaEmail(usuario.getEmail())) {
            return false;
        }

        // Usamos RETURNING para obtener solo el ID
        String insertUsuarioSql = "INSERT INTO usuario(nombre, email, contrasena, telefono, direccion, activo, user_name) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_usuario";

        try {
            // Obtenemos el ID del usuario insertado
            Integer idUsuario = postgresTemplate.queryForObject(
                insertUsuarioSql,
                (rs, rowNum) -> rs.getInt("id_usuario"),
                usuario.getNombre(),
                usuario.getEmail(),
                bCryptPasswordEncoder.encode(usuario.getContrasena()),
                usuario.getTelefono(),
                usuario.getDireccion(),
                true,
                usuario.getUserName()
            );

            if (idUsuario != null && roles != null && roles.length > 0) {
                // Insertar roles
                String insertRolSql = "INSERT INTO rol_usuario(id_usuario, id_rol) VALUES (?, ?)";
                for (Integer idRol : roles) {
                    postgresTemplate.update(insertRolSql, idUsuario, idRol);
                }
                
                // Asignar el ID al objeto usuario
                usuario.setId(idUsuario);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("ERROR en grabaUsuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void crearCliente(int idUsuario) {
        try {
            String sql = "INSERT INTO clientes(id_usuario) VALUES (?)";
            postgresTemplate.update(sql, idUsuario);
        } catch (Exception e) {
            System.err.println("ERROR en crearCliente: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void crearEmpleado(int idUsuario) {
        try {
            String sql = "INSERT INTO empleados(id_usuario, puesto, salario, fecha_contratacion) " +
                         "VALUES (?, 'Nuevo empleado', 0, CURRENT_DATE)";
            postgresTemplate.update(sql, idUsuario);
        } catch (Exception e) {
            System.err.println("ERROR en crearEmpleado: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void crearProveedor(int idUsuario) {
        try {
            String sql = "INSERT INTO proveedores(id_usuario, nombre, direccion, telefono) " +
                         "VALUES (?, 'Nuevo proveedor', '', '')";
            postgresTemplate.update(sql, idUsuario);
        } catch (Exception e) {
            System.err.println("ERROR en crearProveedor: " + e.getMessage());
            throw e;
        }
    }

    public Usuario buscarPorEmail(String email) {
        String query = "SELECT id_usuario as id, nombre, email, contrasena, telefono, " +
                      "direccion, activo, user_name as userName " +
                      "FROM usuario WHERE email = ?";
        try {
            return postgresTemplate.queryForObject(query, (rs, rowNum) -> {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setDireccion(rs.getString("direccion"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setUserName(rs.getString("userName"));
                return usuario;
            }, email);
        } catch (Exception e) {
            System.err.println("ERROR en buscarPorEmail: " + e.getMessage());
            return null;
        }
    }

    public List<Integer> obtenerRolesUsuario(int idUsuario) {
        String query = "SELECT id_rol FROM rol_usuario WHERE id_usuario = ?";
        try {
            return postgresTemplate.queryForList(query, Integer.class, idUsuario);
        } catch (Exception e) {
            System.err.println("ERROR en obtenerRolesUsuario: " + e.getMessage());
            return null;
        }
    }


    public Integer obtenerIdProveedor(int idUsuario) {
        String query = "SELECT id_proveedor FROM proveedores WHERE id_usuario = ?";
        try {
            return postgresTemplate.queryForObject(query, Integer.class, idUsuario);
        } catch (Exception e) {
            System.err.println("ERROR en obtenerIdProveedor: " + e.getMessage());
            return null;
        }
    }

    // En UsuarioDao.java
    public Integer obtenerIdCliente(int idUsuario) {
    String sql = "SELECT id_cliente FROM clientes WHERE id_usuario = ?";
    try {
        return postgresTemplate.queryForObject(sql, Integer.class, idUsuario);
    } catch (Exception e) {
        return null;
    }
}

/**
 * Método para obtener el nombre del cliente por su ID de cliente
 */
public String obtenerNombreCliente(Integer idCliente) {
    String sql = "SELECT u.nombre FROM usuario u " +
                 "JOIN clientes c ON u.id_usuario = c.id_usuario " +
                 "WHERE c.id_cliente = ?";
    try {
        return postgresTemplate.queryForObject(sql, String.class, idCliente);
    } catch (Exception e) {
        System.err.println("ERROR en obtenerNombreCliente: " + e.getMessage());
        return "Cliente desconocido";
    }
}

/**
 * Método para obtener la dirección del cliente por su ID
 */
public String obtenerDireccionCliente(Integer idCliente) {
    try {
        String sql = "SELECT u.direccion FROM usuario u " +
                     "JOIN clientes c ON u.id_usuario = c.id_usuario " +
                     "WHERE c.id_cliente = ?";
        return postgresTemplate.queryForObject(sql, String.class, idCliente);
    } catch (Exception e) {
        System.err.println("ERROR al obtener dirección del cliente: " + e.getMessage());
        return "Dirección no disponible";
    }
}
}