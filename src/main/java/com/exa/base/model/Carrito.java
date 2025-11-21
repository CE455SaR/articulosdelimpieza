package com.exa.base.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Carrito implements Serializable {

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
        return new HashMap<>(items); // Devuelve una copia para evitar modificaciones externas
    }

    public void setItems(Map<Integer, Integer> items) {
        this.items = new HashMap<>(items);
    }

    // Método útil para obtener el total de productos distintos en el carrito
    public int getTotalItems() {
        return items.size();
    }

    // Método para obtener la cantidad total de artículos
    public int getCantidadTotal() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}