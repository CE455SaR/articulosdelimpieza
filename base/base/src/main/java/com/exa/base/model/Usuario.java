package com.exa.base.model;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String contrasena; // Cambiado de password a contrasena
    private String userName;
    private boolean activo;
    private String telefono;
    private String direccion;
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getContrasena() { // Cambiado de getPassword
        return contrasena;
    }
    public void setContrasena(String contrasena) { // Cambiado de setPassword
        this.contrasena = contrasena;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}