package com.exa.base.model;

import java.util.Date;
import java.util.List;

public class Pedido {
    private Integer id;
    private Integer idCliente;
    private Date fecha;
    private Double total;
    private String estado;
    private List<DetallePedido> detalles;
    private String nombreCliente; // Nombre del cliente para mostrar en la vista
    private String direccionCliente; // Dirección del cliente para mostrar en la vista

    // Constructores
    public Pedido() {
    }

    public Pedido(Integer id, Integer idCliente, Date fecha, Double total, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    @Override
    public String toString() {
        return "Pedido [id=" + id + ", idCliente=" + idCliente + ", fecha=" + fecha + ", total=" + total + ", estado="
                + estado + "]";
    }
}