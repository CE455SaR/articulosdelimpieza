package com.exa.base.model;

public class Producto {
    private int id_producto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int id_proveedor;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(int id_producto, String nombre, String descripcion, double precio, int id_proveedor) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.id_proveedor = id_proveedor;
    }

    // Getters y setters
    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    @Override
    public String toString() {
        return "Producto [id_producto=" + id_producto + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", precio=" + precio + ", id_proveedor=" + id_proveedor + "]";
    }
}