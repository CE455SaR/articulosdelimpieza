package com.exa.base.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class InventarioDao {

    @Autowired
    private JdbcTemplate postgresTemplate;

    public int obtenerStock(int idProducto) {
        String sql = "SELECT cantidad FROM inventario WHERE id_producto = ?";
        try {
            return postgresTemplate.queryForObject(sql, Integer.class, idProducto);
        } catch (Exception e) {
            // Si no hay registro, retornamos 0
            return 0;
        }
    }

    @Transactional
    public void actualizarCantidad(int idProducto, int cambio) {
        // Si cambio es positivo, incrementa; si es negativo, decrementa
        String sql = "UPDATE inventario SET cantidad = cantidad + ? WHERE id_producto = ?";
        int filasAfectadas = postgresTemplate.update(sql, cambio, idProducto);
        
        // Si no existe el registro, lo creamos
        if (filasAfectadas == 0 && cambio > 0) {
            sql = "INSERT INTO inventario (id_producto, cantidad) VALUES (?, ?)";
            postgresTemplate.update(sql, idProducto, cambio);
        }
        
        // Asegurarnos que no quede negativo
        sql = "UPDATE inventario SET cantidad = 0 WHERE id_producto = ? AND cantidad < 0";
        postgresTemplate.update(sql, idProducto);
    }

    @Transactional
    public void establecerCantidad(int idProducto, int nuevaCantidad) {
        String sql = "UPDATE inventario SET cantidad = ? WHERE id_producto = ?";
        int filasAfectadas = postgresTemplate.update(sql, nuevaCantidad, idProducto);
        
        // Si no existe el registro, lo creamos
        if (filasAfectadas == 0) {
            sql = "INSERT INTO inventario (id_producto, cantidad) VALUES (?, ?)";
            postgresTemplate.update(sql, idProducto, nuevaCantidad);
        }
    }
}