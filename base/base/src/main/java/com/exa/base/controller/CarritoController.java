package com.exa.base.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private InventarioDao inventarioDao;

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
            System.out.println("ID Producto recibido: " + idProducto);
            System.out.println("Cantidad recibida: " + cantidad);

            Producto producto = productoDao.obtenerProductoPorId(idProducto);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/cliente/catalogo";
            }

            carrito.agregarItem(idProducto, cantidad);
            System.out.println("Carrito actualizado: " + carrito.getItems());

            // Mensaje de éxito para el usuario
            redirectAttributes.addFlashAttribute("success", "¡Producto añadido al carrito!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al agregar el producto: " + e.getMessage());
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
            if (producto != null) {  // Verificar que el producto existe
                int cantidad = entry.getValue();
                itemsDetallados.put(producto, cantidad);
                total += producto.getPrecio() * cantidad;
            }
        }
        
        mav.addObject("items", itemsDetallados);
        mav.addObject("total", total);
        return mav;
    }

    @GetMapping("/carrito/cantidad")
    @ResponseBody
    public int obtenerCantidadCarrito(@ModelAttribute("carrito") Carrito carrito) {
        return carrito.getCantidadTotal();
    }
    
    @PostMapping("/carrito/actualizar")
    public String actualizarCarrito(
        @RequestParam("idProducto") int idProducto,
        @RequestParam("cantidad") int cantidad,
        @ModelAttribute("carrito") Carrito carrito,
        RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("Actualizando producto: " + idProducto + " con cantidad: " + cantidad);
            
            // Verificar que el producto existe antes de actualizar
            Producto producto = productoDao.obtenerProductoPorId(idProducto);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/carrito";
            }
            
            if (cantidad > 0) {
                carrito.getItems().put(idProducto, cantidad);
                redirectAttributes.addFlashAttribute("success", "Cantidad actualizada correctamente");
            } else {
                carrito.getItems().remove(idProducto);
                redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar/{idProducto}")
    public String eliminarDelCarrito(
        @PathVariable("idProducto") int idProducto,
        @ModelAttribute("carrito") Carrito carrito,
        RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("Eliminando producto: " + idProducto);
            
            // Verificar que el producto existe antes de eliminar
            Producto producto = productoDao.obtenerProductoPorId(idProducto);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/carrito";
            }
            
            carrito.getItems().remove(idProducto);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(
        @ModelAttribute("carrito") Carrito carrito,
        RedirectAttributes redirectAttributes) {
        
        carrito.limpiarCarrito();
        redirectAttributes.addFlashAttribute("success", "Carrito vaciado correctamente");
        return "redirect:/carrito";
    }
}