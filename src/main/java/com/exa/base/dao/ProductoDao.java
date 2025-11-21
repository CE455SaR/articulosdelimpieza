package com.exa.base.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.exa.base.model.Pedido;
import com.exa.base.model.Producto;
import com.exa.base.model.ProductoVendido;


@Repository
public class ProductoDao {

    @Autowired
    private JdbcTemplate postgresTemplate;

    // Método corregido para obtener un producto por ID
    public Producto obtenerProductoPorId(int idProducto) {
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        return postgresTemplate.queryForObject(
            sql,
            new BeanPropertyRowMapper<>(Producto.class), // Mapeo automático
            idProducto
        );
    }

    
    @Transactional
    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO productos(nombre, descripcion, precio, id_proveedor) VALUES (?, ?, ?, ?)";
        return postgresTemplate.update(sql, producto.getNombre(), producto.getDescripcion(), 
                producto.getPrecio(), producto.getId_proveedor()) > 0;
    }

    public List<Producto> obtenerProductosPorProveedor(int idProveedor) {
        String sql = "SELECT * FROM productos WHERE id_proveedor = ?";
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setPrecio(rs.getDouble("precio"));
            p.setId_proveedor(rs.getInt("id_proveedor"));
            return p;
        }, idProveedor);
    }

    public List<Producto> obtenerTodosProductos() {
        String sql = "SELECT * FROM productos";
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setPrecio(rs.getDouble("precio"));
            p.setId_proveedor(rs.getInt("id_proveedor"));
            return p;
        });
    }

   

    @Transactional
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ? WHERE id_producto = ?";
        return postgresTemplate.update(sql, producto.getNombre(), producto.getDescripcion(), 
                producto.getPrecio(), producto.getId_producto()) > 0;
    }

    @Transactional
    public boolean eliminarProducto(int idProducto) {
        return postgresTemplate.update("DELETE FROM productos WHERE id_producto = ?", idProducto) > 0;
    }
    
    /**
     * Obtiene los productos más vendidos en un rango de fechas
     */
    public List<ProductoVendido> obtenerProductosMasVendidos(Date fechaInicio, Date fechaFin) {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, " +
                     "SUM(dp.cantidad) as cantidad_vendida, " +
                     "SUM(dp.cantidad * dp.precio_unitario) as monto_total " +
                     "FROM productos p " +
                     "JOIN detalles_pedido dp ON p.id_producto = dp.id_producto " +
                     "JOIN pedidos pe ON dp.id_pedido = pe.id " +
                     "WHERE pe.fecha BETWEEN ? AND ? " +
                     "AND pe.estado != 'CANCELADO' " +
                     "GROUP BY p.id_producto, p.nombre, p.descripcion " +
                     "ORDER BY cantidad_vendida DESC";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return postgresTemplate.query(sql, new ProductoVendidoRowMapper(), fechaInicio, fechaFin);
    }
    
    /**
     * Método para obtener todos los pedidos (para implementar la función en PedidoDao)
     */
    public List<Pedido> obtenerTodosPedidos() {
        String sql = "SELECT p.id, p.id_cliente, u.nombre, p.fecha, p.total, p.estado " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.id_cliente = u.id " +
                     "ORDER BY p.fecha DESC";
        
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Pedido p = new Pedido();
            p.setId(rs.getInt("id"));
            p.setIdCliente(rs.getInt("id_cliente"));
            p.setFecha(rs.getTimestamp("fecha"));
            p.setTotal(rs.getDouble("total"));
            p.setEstado(rs.getString("estado"));
            return p;
        });
    }
    
    /**
     * Método para obtener pedidos por estado (para implementar la función en PedidoDao)
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        String sql = "SELECT p.id, p.id_cliente, u.nombre, p.fecha, p.total, p.estado " +
                     "FROM pedidos p " +
                     "JOIN usuarios u ON p.id_cliente = u.id " +
                     "WHERE p.estado = ? " +
                     "ORDER BY p.fecha DESC";
        
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Pedido p = new Pedido();
            p.setId(rs.getInt("id"));
            p.setIdCliente(rs.getInt("id_cliente"));
            p.setFecha(rs.getTimestamp("fecha"));
            p.setTotal(rs.getDouble("total"));
            p.setEstado(rs.getString("estado"));
            return p;
        }, estado);
    }
    
    private class ProductoVendidoRowMapper implements RowMapper<ProductoVendido> {
        @Override
        public ProductoVendido mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ProductoVendido(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getInt("cantidad_vendida"),
                rs.getDouble("monto_total")
            );
        }
    }
}