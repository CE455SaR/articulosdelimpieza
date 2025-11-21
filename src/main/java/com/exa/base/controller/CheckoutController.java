package com.exa.base.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.exa.base.dao.ProductoDao;
import com.exa.base.model.Carrito;
import com.exa.base.model.Producto;

@Controller
@SessionAttributes("carrito")  // Añadido para acceder a la sesión del carrito
public class CheckoutController {

    @Autowired
    private ProductoDao productoDao;

    @GetMapping("/checkout")
    public ModelAndView mostrarCheckout(@ModelAttribute("carrito") Carrito carrito) {
        ModelAndView mav = new ModelAndView("cliente/checkout");
        
        // Procesar items del carrito para mostrar detalles
        Map<Producto, Integer> itemsDetallados = new HashMap<>();
        double total = 0.0;
        
        for (Map.Entry<Integer, Integer> entry : carrito.getItems().entrySet()) {
            Producto producto = productoDao.obtenerProductoPorId(entry.getKey());
            if (producto != null) {
                int cantidad = entry.getValue();
                itemsDetallados.put(producto, cantidad);
                total += producto.getPrecio() * cantidad;
            }
        }
        
        mav.addObject("items", itemsDetallados);
        mav.addObject("total", total);
        
        return mav;
    }
    
    
    
}