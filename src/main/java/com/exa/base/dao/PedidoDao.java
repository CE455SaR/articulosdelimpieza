package com.exa.base.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exa.base.model.DetallePedido;
import com.exa.base.model.Pedido;

@Repository
public class PedidoDao {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Pedido> obtenerPedidosPorCliente(Integer idCliente) {
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, id_cliente, fecha, total, estado FROM pedidos WHERE id_cliente = ? ORDER BY fecha DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Pedido pedido = new Pedido();
                        pedido.setId(rs.getInt("id"));
                        pedido.setIdCliente(rs.getInt("id_cliente"));
                        pedido.setFecha(rs.getTimestamp("fecha"));
                        pedido.setTotal(rs.getDouble("total"));
                        pedido.setEstado(rs.getString("estado"));
                        
                        pedidos.add(pedido);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return pedidos;
    }
    
    public Pedido obtenerPedidoPorId(Integer id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, id_cliente, fecha, total, estado FROM pedidos WHERE id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Pedido pedido = new Pedido();
                        pedido.setId(rs.getInt("id"));
                        pedido.setIdCliente(rs.getInt("id_cliente"));
                        pedido.setFecha(rs.getTimestamp("fecha"));
                        pedido.setTotal(rs.getDouble("total"));
                        pedido.setEstado(rs.getString("estado"));
                        
                        return pedido;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<DetallePedido> obtenerDetallesPedido(Integer idPedido) {
        List<DetallePedido> detalles = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id_detalle, id_pedido, id_producto, cantidad, precio_unitario FROM detalles_pedido WHERE id_pedido = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPedido);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DetallePedido detalle = new DetallePedido();
                        detalle.setIdDetalle(rs.getInt("id_detalle"));
                        detalle.setIdPedido(rs.getInt("id_pedido"));
                        detalle.setIdProducto(rs.getInt("id_producto"));
                        detalle.setCantidad(rs.getInt("cantidad"));
                        detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                        
                        detalles.add(detalle);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    public Integer guardarPedido(Pedido pedido) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO pedidos (id_cliente, fecha, total, estado) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pedido.getIdCliente());
                ps.setTimestamp(2, new Timestamp(pedido.getFecha().getTime()));
                ps.setDouble(3, pedido.getTotal());
                ps.setString(4, pedido.getEstado());
                
                ps.executeUpdate();
                
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean guardarDetallePedido(DetallePedido detalle) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, detalle.getIdPedido());
                ps.setInt(2, detalle.getIdProducto());
                ps.setInt(3, detalle.getCantidad());
                ps.setDouble(4, detalle.getPrecioUnitario());
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean actualizarEstadoPedido(Integer idPedido, String nuevoEstado) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idPedido);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene todos los pedidos con información del cliente
     */
    public List<Pedido> obtenerTodosPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT p.id, p.id_cliente, u.nombre as nombre_cliente, p.fecha, p.total, p.estado " +
                         "FROM pedidos p " +
                         "JOIN usuarios u ON p.id_cliente = u.id " +
                         "ORDER BY p.fecha DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Pedido pedido = new Pedido();
                        pedido.setId(rs.getInt("id"));
                        pedido.setIdCliente(rs.getInt("id_cliente"));
                        pedido.setFecha(rs.getTimestamp("fecha"));
                        pedido.setTotal(rs.getDouble("total"));
                        pedido.setEstado(rs.getString("estado"));
                        
                        pedidos.add(pedido);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por estado con información del cliente
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT p.id, p.id_cliente, u.nombre as nombre_cliente, p.fecha, p.total, p.estado " +
                         "FROM pedidos p " +
                         "JOIN usuarios u ON p.id_cliente = u.id " +
                         "WHERE p.estado = ? " +
                         "ORDER BY p.fecha DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, estado);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Pedido pedido = new Pedido();
                        pedido.setId(rs.getInt("id"));
                        pedido.setIdCliente(rs.getInt("id_cliente"));
                        pedido.setFecha(rs.getTimestamp("fecha"));
                        pedido.setTotal(rs.getDouble("total"));
                        pedido.setEstado(rs.getString("estado"));
                        
                        pedidos.add(pedido);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return pedidos;
    }
}