package com.exa.base.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L; // Añadido para serialización
    private Map<Integer, Integer> items = new HashMap<>(); // ID Producto -> Cantidad

    // Agrega o actualiza la cantidad de un producto en el carrito
    public void agregarItem(int idProducto, int cantidad) {
        items.merge(idProducto, cantidad, Integer::sum);
    }

    // Elimina un producto del carrito
    public void eliminarItem(int idProducto) {
        items.remove(idProducto);
    }

    // Vacía completamente el carrito
    public void limpiarCarrito() {
        items.clear();
    }

    // Obtiene la cantidad específica de un producto
    public int obtenerCantidad(int idProducto) {
        return items.getOrDefault(idProducto, 0);
    }

    // Getters y Setters
    public Map<Integer, Integer> getItems() {
        return items; // Devolvemos la referencia directa para permitir modificaciones
    }

    public void setItems(Map<Integer, Integer> items) {
        this.items = items != null ? items : new HashMap<>();
    }

    // Método útil para obtener el total de productos distintos en el carrito
    public int getTotalItems() {
        return items.size();
    }

    // Método para obtener la cantidad total de artículos
    public int getCantidadTotal() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    @Override
    public String toString() {
        return "Carrito{items=" + items + "}";
    }
}