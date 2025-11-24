package com.exa.base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.exa.base.dao.PedidoDao;
import com.exa.base.dao.ProductoDao;
import com.exa.base.dao.UsuarioDao;
import com.exa.base.model.Usuario;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private UsuarioDao usuarioDao; // Inyección de UsuarioDao

    @Autowired
    private PedidoDao pedidoDao; // Inyección de PedidoDao

    @GetMapping("/catalogo")
    public ModelAndView mostrarCatalogo() {
    ModelAndView mav = new ModelAndView("cliente/catalogo");
    mav.addObject("productos", productoDao.obtenerTodosProductos());
    return mav;
    }

    @GetMapping("/mis-pedidos")
    public ModelAndView mostrarPedidos(Authentication auth) {
        // Validar autenticación
        if (auth == null || !auth.isAuthenticated()) {
            return new ModelAndView("redirect:/login");
        }

        // Obtener usuario
        Usuario usuario = usuarioDao.buscarPorEmail(auth.getName());
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        // Obtener ID del cliente
        Integer idCliente = usuarioDao.obtenerIdCliente(usuario.getId());
        if (idCliente == null) {
            return new ModelAndView("redirect:/error");
        }

        // Cargar pedidos
        ModelAndView mav = new ModelAndView("cliente/mis-pedidos"); // Corregido de "cliente/pedidos" a "cliente/mis-pedidos"
        mav.addObject("pedidos", pedidoDao.obtenerPedidosPorCliente(idCliente));
        return mav;
    }
}