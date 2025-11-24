package com.exa.base.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.exa.base.dao.ProductoDao;
import com.exa.base.dao.UsuarioDao;
import com.exa.base.model.Usuario;

@Controller
public class InicioController {

    @Autowired
    private UsuarioDao usuarioDao;
    
    @Autowired
    private ProductoDao productoDao;

    @GetMapping("/inicio")
    public ModelAndView inicio() {
        ModelAndView mav = new ModelAndView("inicio");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            Usuario usuario = usuarioDao.buscarPorEmail(email);
            List<Integer> roles = usuarioDao.obtenerRolesUsuario(usuario.getId());

            mav.addObject("nombreUsuario", usuario.getNombre());
            mav.addObject("esCliente", roles.contains(1));
            mav.addObject("esProveedor", roles.contains(2));
            mav.addObject("esEmpleado", roles.contains(3));
            
            if(roles.contains(2)){
                Integer idProveedor = usuarioDao.obtenerIdProveedor(usuario.getId());
                mav.addObject("idProveedor", idProveedor);
            }
        }
        return mav;
    }

    @GetMapping("/actualizar-sesion")
    public String actualizarSesion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/inicio";
    }
}