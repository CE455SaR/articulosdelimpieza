package com.exa.base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller; // Import correcto
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.exa.base.dao.ProductoDao;
import com.exa.base.dao.UsuarioDao;
import com.exa.base.model.Producto;

@Controller
@RequestMapping("/proveedor")
public class ProductoController {

    @Autowired
    private ProductoDao productoDao;
    
    @Autowired
    private UsuarioDao usuarioDao;

    @GetMapping("/mis-productos")
    public ModelAndView listarProductos(Authentication auth) {
        ModelAndView mav = new ModelAndView("proveedor/mis-productos");
        int idProveedor = usuarioDao.obtenerIdProveedor(
            usuarioDao.buscarPorEmail(auth.getName()).getId()
        );
        mav.addObject("productos", productoDao.obtenerProductosPorProveedor(idProveedor));
        return mav;
    }

    @GetMapping("/nuevo-producto")
    public ModelAndView mostrarFormularioNuevo() {
        return new ModelAndView("proveedor/nuevo-producto", "producto", new Producto());
    }

    @PostMapping("/guardar-producto")
    public String guardarProducto(@ModelAttribute Producto producto, Authentication auth) {
        producto.setId_proveedor(usuarioDao.obtenerIdProveedor(
            usuarioDao.buscarPorEmail(auth.getName()).getId()
        ));
        productoDao.insertarProducto(producto);
        return "redirect:/proveedor/mis-productos";
    }

    @GetMapping("/editar-producto/{id}")
    public ModelAndView mostrarFormularioEdicion(@PathVariable int id) {
        Producto producto = productoDao.obtenerProductoPorId(id);
        return new ModelAndView("proveedor/editar-producto", "producto", producto);
    }

    @PostMapping("/actualizar-producto")
    public String actualizarProducto(@ModelAttribute Producto producto) {
        productoDao.actualizarProducto(producto);
        return "redirect:/proveedor/mis-productos";
    }

    @PostMapping("/eliminar-producto/{id}")
    public String eliminarProducto(@PathVariable int id) {
        productoDao.eliminarProducto(id);
        return "redirect:/proveedor/mis-productos";
    }
}