package com.exa.base.model;

public class DetallePedido {
    private Integer idDetalle;
    private Integer idPedido;
    private Integer idProducto;
    private Integer cantidad;
    private Double precioUnitario;

    // Constructores
    public DetallePedido() {
    }

    public DetallePedido(Integer idDetalle, Integer idPedido, Integer idProducto, Integer cantidad, Double precioUnitario) {
        this.idDetalle = idDetalle;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y setters
    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    @Override
    public String toString() {
        return "DetallePedido [idDetalle=" + idDetalle + ", idPedido=" + idPedido + ", idProducto=" + idProducto
                + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + "]";
    }
}