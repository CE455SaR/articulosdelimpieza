package com.exa.base.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Nuevo import
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exa.base.dao.InventarioDao;
import com.exa.base.dao.ProductoDao;
import com.exa.base.model.Carrito;
import com.exa.base.model.Producto;

@Controller
@SessionAttributes("carrito")
public class CarritoController {

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private InventarioDao inventarioDao; // Nueva dependencia

    @ModelAttribute("carrito")
    public Carrito inicializarCarrito() {
        return new Carrito();
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(
        @RequestParam int idProducto,
        @RequestParam int cantidad,
        @ModelAttribute("carrito") Carrito carrito,
        RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("ID Producto recibido: " + idProducto); // Log para depuración
            System.out.println("Cantidad recibida: " + cantidad);

            Producto producto = productoDao.obtenerProductoPorId(idProducto);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/cliente/catalogo";
            }

            carrito.agregarItem(idProducto, cantidad);
            System.out.println("Carrito actualizado: " + carrito.getItems()); // Log del carrito

            redirectAttributes.addFlashAttribute("success", "Producto añadido al carrito");
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza del error
            redirectAttributes.addFlashAttribute("error", "Error al agregar el producto");
        }
        return "redirect:/cliente/catalogo";
    }

    @GetMapping("/carrito")
    public ModelAndView verCarrito(@ModelAttribute("carrito") Carrito carrito) {
        ModelAndView mav = new ModelAndView("cliente/carrito");
        
        Map<Producto, Integer> itemsDetallados = new HashMap<>();
        double total = 0.0;

        for (Map.Entry<Integer, Integer> entry : carrito.getItems().entrySet()) {
            Producto producto = productoDao.obtenerProductoPorId(entry.getKey());
            int cantidad = entry.getValue();
            itemsDetallados.put(producto, cantidad);
            total += producto.getPrecio() * cantidad;
        }
        
        mav.addObject("items", itemsDetallados);
        mav.addObject("total", total);
        return mav;
    }

    @GetMapping("/carrito/cantidad")
    @ResponseBody // Retorna datos, no una vista
    public int obtenerCantidadCarrito(@ModelAttribute("carrito") Carrito carrito) {
        return carrito.getCantidadTotal();
    }
    
    @PostMapping("/carrito/actualizar")
    public String actualizarCarrito(
        @RequestParam int idProducto,
        @RequestParam int cantidad,
        @ModelAttribute("carrito") Carrito carrito,
        RedirectAttributes redirectAttributes) { // Añadir RedirectAttributes
        
        try {
            if (cantidad > 0) {
                carrito.getItems().put(idProducto, cantidad);
            } else {
                carrito.getItems().remove(idProducto);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar/{idProducto}")
    public String eliminarDelCarrito(
        @PathVariable int idProducto,
        @ModelAttribute("carrito") Carrito carrito) {
        
        carrito.getItems().remove(idProducto);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(@ModelAttribute("carrito") Carrito carrito) {
        carrito.limpiarCarrito();
        return "redirect:/carrito";
    }

    // El método mostrarCheckout ha sido eliminado para evitar la ambigüedad con CheckoutController
}