package com.exa.base.dao;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.exa.base.model.DetallePedido;
import com.exa.base.model.Pedido;

@Repository
public class PedidoDao {

    @Autowired
    private JdbcTemplate postgresTemplate;
    
    /**
     * Obtiene todos los pedidos de la base de datos
     */
    public List<Pedido> obtenerTodosPedidos() {
    String sql = "SELECT id, id_cliente, fecha, total, estado " +
                 "FROM pedidos " +
                 "ORDER BY fecha DESC";
    
    try {
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Pedido pedido = new Pedido();
            pedido.setId(rs.getInt("id"));
            pedido.setIdCliente(rs.getInt("id_cliente"));
            pedido.setFecha(rs.getTimestamp("fecha"));
            pedido.setTotal(rs.getDouble("total"));
            pedido.setEstado(rs.getString("estado"));
            return pedido;
        });
    } catch (Exception e) {
        System.err.println("ERROR en obtenerTodosPedidos: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>(); // Devolver una lista vacía en caso de error
    }
}
    
    /**
     * Obtiene los pedidos filtrados por estado
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
    String sql = "SELECT id, id_cliente, fecha, total, estado " +
                 "FROM pedidos " +
                 "WHERE UPPER(estado) = UPPER(?) " +
                 "ORDER BY fecha DESC";
    
    try {
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Pedido pedido = new Pedido();
            pedido.setId(rs.getInt("id"));
            pedido.setIdCliente(rs.getInt("id_cliente"));
            pedido.setFecha(rs.getTimestamp("fecha"));
            pedido.setTotal(rs.getDouble("total"));
            pedido.setEstado(rs.getString("estado"));
            return pedido;
        }, estado);
    } catch (Exception e) {
        System.err.println("ERROR en obtenerPedidosPorEstado: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}
    
    /**
     * Obtiene un pedido por su ID
     */
    public Pedido obtenerPedidoPorId(Integer idPedido) {
    String sql = "SELECT id, id_cliente, fecha, total, estado " +
                 "FROM pedidos " +
                 "WHERE id = ?";
    
    try {
        return postgresTemplate.queryForObject(sql, (rs, rowNum) -> {
            Pedido pedido = new Pedido();
            pedido.setId(rs.getInt("id"));
            pedido.setIdCliente(rs.getInt("id_cliente"));
            pedido.setFecha(rs.getTimestamp("fecha"));
            pedido.setTotal(rs.getDouble("total"));
            pedido.setEstado(rs.getString("estado"));
            return pedido;
        }, idPedido);
    } catch (Exception e) {
        System.err.println("ERROR en obtenerPedidoPorId: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
    
    /**
     * Obtiene los detalles de un pedido
     */
    public List<DetallePedido> obtenerDetallesPedido(Integer idPedido) {
        String sql = "SELECT id_detalle, id_pedido, id_producto, cantidad, precio_unitario " +
                     "FROM detalles_pedido " +
                     "WHERE id_pedido = ?";
        
        try {
            return postgresTemplate.query(sql, (rs, rowNum) -> {
                DetallePedido detalle = new DetallePedido();
                detalle.setIdDetalle(rs.getInt("id_detalle"));
                detalle.setIdPedido(rs.getInt("id_pedido"));
                detalle.setIdProducto(rs.getInt("id_producto"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                return detalle;
            }, idPedido);
        } catch (Exception e) {
            System.err.println("ERROR en obtenerDetallesPedido: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public boolean actualizarEstadoPedido(Integer idPedido, String nuevoEstado) {
    String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
    
    try {
        int filasActualizadas = postgresTemplate.update(sql, nuevoEstado, idPedido);
        return filasActualizadas > 0;
    } catch (Exception e) {
        System.err.println("ERROR en actualizarEstadoPedido: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    
    /**
     * Obtiene los pedidos de un cliente específico
     */
    public List<Pedido> obtenerPedidosPorCliente(Integer idCliente) {
    String sql = "SELECT id, id_cliente, fecha, total, estado " +
                 "FROM pedidos " +
                 "WHERE id_cliente = ? " +
                 "ORDER BY fecha DESC";
    
    try {
        return postgresTemplate.query(sql, (rs, rowNum) -> {
            Pedido pedido = new Pedido();
            pedido.setId(rs.getInt("id"));
            pedido.setIdCliente(rs.getInt("id_cliente"));
            pedido.setFecha(rs.getTimestamp("fecha"));
            pedido.setTotal(rs.getDouble("total"));
            pedido.setEstado(rs.getString("estado"));
            return pedido;
        }, idCliente);
    } catch (Exception e) {
        System.err.println("ERROR en obtenerPedidosPorCliente: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}

    /**
     * Guarda un nuevo pedido y devuelve el ID generado
     */
   @Transactional
public Integer guardarPedido(Pedido pedido) {
    String sql = "INSERT INTO pedidos(id_cliente, fecha, total, estado) " +
                 "VALUES (?, ?, ?, ?)";
    
    KeyHolder keyHolder = new GeneratedKeyHolder();
    
    try {
        postgresTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"}); 
            ps.setInt(1, pedido.getIdCliente());
            ps.setTimestamp(2, new Timestamp(pedido.getFecha().getTime()));
            ps.setDouble(3, pedido.getTotal());
            ps.setString(4, pedido.getEstado());
            return ps;
        }, keyHolder);
        
        // Extraer el valor de la clave "id" de forma segura
        Number key = keyHolder.getKey();
        if (key != null) {
            return key.intValue();
        } else {
            throw new RuntimeException("No se pudo obtener el ID del pedido insertado");
        }
    } catch (Exception e) {
        System.err.println("ERROR en guardarPedido: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error al guardar el pedido: " + e.getMessage());
    }
}
    
    /**
     * Guarda un detalle de pedido
     */
    @Transactional
    public boolean guardarDetallePedido(DetallePedido detalle) {
        String sql = "INSERT INTO detalles_pedido(id_pedido, id_producto, cantidad, precio_unitario) " +
                     "VALUES (?, ?, ?, ?)";
        
        try {
            int filasInsertadas = postgresTemplate.update(sql, 
                    detalle.getIdPedido(), 
                    detalle.getIdProducto(), 
                    detalle.getCantidad(), 
                    detalle.getPrecioUnitario());
            
            return filasInsertadas > 0;
        } catch (Exception e) {
            System.err.println("ERROR en guardarDetallePedido: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}