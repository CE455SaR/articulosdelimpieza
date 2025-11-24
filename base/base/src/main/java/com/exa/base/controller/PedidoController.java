package com.exa.base.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exa.base.dao.PedidoDao;
import com.exa.base.dao.ProductoDao;
import com.exa.base.dao.UsuarioDao;
import com.exa.base.model.Carrito;
import com.exa.base.model.DetallePedido;
import com.exa.base.model.Pedido;
import com.exa.base.model.Producto;
import com.exa.base.model.Usuario;

@Controller
@SessionAttributes("carrito")
public class PedidoController {

    @Autowired
    private PedidoDao pedidoDao;
    
    @Autowired
    private ProductoDao productoDao;
    
    @Autowired
    private UsuarioDao usuarioDao;
    
    @PostMapping("/cliente/finalizar-pedido")
    public String procesarCheckout(
            @ModelAttribute("carrito") Carrito carrito,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validar autenticación
            if (auth == null || !auth.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para realizar un pedido");
                return "redirect:/login";
            }
            
            // Obtener usuario y su ID de cliente
            Usuario usuario = usuarioDao.buscarPorEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/login";
            }
            
            Integer idCliente = usuarioDao.obtenerIdCliente(usuario.getId());
            if (idCliente == null) {
                redirectAttributes.addFlashAttribute("error", "Perfil de cliente no encontrado");
                return "redirect:/error";
            }
            
            // Validar que el carrito no esté vacío
            if (carrito.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío");
                return "redirect:/carrito";
            }
            
            // Crear el pedido
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setIdCliente(idCliente);
            nuevoPedido.setFecha(new Date());
            nuevoPedido.setEstado("PENDIENTE");
            
            // Calcular el total y crear los detalles
            double total = 0.0;
            List<DetallePedido> detalles = new ArrayList<>();
            
            for (Map.Entry<Integer, Integer> entry : carrito.getItems().entrySet()) {
                Integer idProducto = entry.getKey();
                Integer cantidad = entry.getValue();
                
                Producto producto = productoDao.obtenerProductoPorId(idProducto);
                if (producto == null) {
                    continue; // Ignorar productos que ya no existen
                }
                
                // Crear detalle
                DetallePedido detalle = new DetallePedido();
                detalle.setIdProducto(idProducto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getPrecio());
                
                // Acumular al total
                total += producto.getPrecio() * cantidad;
                
                detalles.add(detalle);
            }
            
            nuevoPedido.setTotal(total);
            
            // Guardar el pedido y obtener su ID
            Integer idPedido = pedidoDao.guardarPedido(nuevoPedido);
            
            // Guardar los detalles del pedido
            for (DetallePedido detalle : detalles) {
                detalle.setIdPedido(idPedido);
                pedidoDao.guardarDetallePedido(detalle);
            }
            
            // Limpiar el carrito después de procesar el pedido
            carrito.limpiarCarrito();
            
            redirectAttributes.addFlashAttribute("success", "¡Pedido realizado con éxito!");
            return "redirect:/cliente/mis-pedidos";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pedido: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
    
    @GetMapping("/cliente/pedidos/{id}/detalle")
    public ModelAndView mostrarDetallePedido(@PathVariable Integer id, Authentication auth, RedirectAttributes redirectAttributes) {
        // Validar autenticación
        if (auth == null || !auth.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para ver detalles del pedido");
            return new ModelAndView("redirect:/login");
        }
        
        // Obtener usuario y cliente
        Usuario usuario = usuarioDao.buscarPorEmail(auth.getName());
        Integer idCliente = usuarioDao.obtenerIdCliente(usuario.getId());
        
        // Obtener el pedido y verificar que pertenezca al cliente
        Pedido pedido = pedidoDao.obtenerPedidoPorId(id);
        if (pedido == null || !pedido.getIdCliente().equals(idCliente)) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado o no autorizado");
            return new ModelAndView("redirect:/cliente/mis-pedidos");
        }
        
        // Obtener detalles del pedido
        List<DetallePedido> detalles = pedidoDao.obtenerDetallesPedido(id);
        pedido.setDetalles(detalles);
        
        // Obtener información de productos para cada detalle
        List<Map<String, Object>> detallesCompletos = new ArrayList<>();
        
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDao.obtenerProductoPorId(detalle.getIdProducto());
            
            if (producto != null) {
                Map<String, Object> item = Map.of(
                    "detalle", detalle,
                    "producto", producto,
                    "subtotal", detalle.getPrecioUnitario() * detalle.getCantidad()
                );
                
                detallesCompletos.add(item);
            }
        }
        
        ModelAndView mav = new ModelAndView("cliente/detalle-pedido");
        mav.addObject("pedido", pedido);
        mav.addObject("detalles", detallesCompletos);
        
        return mav;
    }
}