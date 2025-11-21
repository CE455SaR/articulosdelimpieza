package com.exa.base.model;

public class ProductoVendido {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Integer cantidadVendida;
    private Double montoTotal;
    private Double precioPromedio;
    
    public ProductoVendido() {
    }
    
    public ProductoVendido(Integer idProducto, String nombre, String descripcion, 
                         Integer cantidadVendida, Double montoTotal) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidadVendida = cantidadVendida;
        this.montoTotal = montoTotal;
        this.calcularPrecioPromedio();
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
        calcularPrecioPromedio();
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
        calcularPrecioPromedio();
    }

    public Double getPrecioPromedio() {
        return precioPromedio;
    }
    
    private void calcularPrecioPromedio() {
        if (cantidadVendida != null && cantidadVendida > 0 && montoTotal != null) {
            this.precioPromedio = montoTotal / cantidadVendida;
        } else {
            this.precioPromedio = 0.0;
        }
    }
}