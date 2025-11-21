package com.exa.base.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    // Añadir método para procesar el formulario de checkout
    @PostMapping("/cliente/checkout")
    public String procesarCheckout(
            @ModelAttribute("carrito") Carrito carrito,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Aquí iría la lógica para guardar el pedido en la base de datos
            // Por ejemplo: crear un pedido, sus detalles, etc.
            
            // Limpiar el carrito después de procesar el pedido
            carrito.limpiarCarrito();
            
            redirectAttributes.addFlashAttribute("success", "¡Pedido realizado con éxito!");
            return "redirect:/cliente/mis-pedidos";  // Redirigir a la página de pedidos del cliente
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pedido");
            return "redirect:/checkout";
        }
    }
}