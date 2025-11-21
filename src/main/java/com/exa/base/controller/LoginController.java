package com.exa.base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.exa.base.dao.RoleDao;
import com.exa.base.dao.UsuarioDao;
import com.exa.base.model.Usuario;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private UsuarioDao usuarioDao;
    
    @Autowired
    private RoleDao roleDao;

    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        String mensaje = request.getParameter("mensaje") == null ? "0" : request.getParameter("mensaje");

        String mensajeAlert = "";
        boolean showMensaje = false;

        switch(mensaje) {
            case "1":
                mensajeAlert = "Usuario registrado correctamente";
                showMensaje = true;
                break;
            case "2":
                mensajeAlert = "Error al registrar el usuario";
                showMensaje = true;
                break;
            case "3":
                mensajeAlert = "El email ya está registrado";
                showMensaje = true;
                break;
            case "4":
                mensajeAlert = "Las contraseñas no coinciden";
                showMensaje = true;
                break;
        }

        modelAndView.addObject("mensajeAlert", mensajeAlert);
        modelAndView.addObject("showMensaje", showMensaje);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/registrar")
    public ModelAndView registrar() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("usuario", new Usuario());
        modelAndView.addObject("roles", roleDao.listaRoles());
        modelAndView.setViewName("registrar");
        return modelAndView;
    }

    @PostMapping("/registrar")
    public ModelAndView registrarUsuario(Usuario usuario, HttpServletRequest request) {
        String mensaje = "0";
        String confirmacion = request.getParameter("confirmacion");

        if (!usuario.getContrasena().equals(confirmacion)) {
            mensaje = "4";
            return new ModelAndView("redirect:/login?mensaje=" + mensaje);
        }

        try {
            String tipoUsuarioStr = request.getParameter("tipoUsuario");
            if (tipoUsuarioStr == null || tipoUsuarioStr.isEmpty()) {
                mensaje = "2";
                return new ModelAndView("redirect:/login?mensaje=" + mensaje);
            }

            int tipoUsuario = Integer.parseInt(tipoUsuarioStr);

            if (usuarioDao.buscaEmail(usuario.getEmail())) {
                mensaje = "3";
            } else {
                Integer[] roles = new Integer[]{tipoUsuario};
                if (usuarioDao.grabaUsuario(usuario, roles)) {
                    switch(tipoUsuario) {
                        case 1:
                            usuarioDao.crearCliente(usuario.getId());
                            break;
                        case 2:
                            usuarioDao.crearProveedor(usuario.getId());
                            break;
                        case 3:
                            usuarioDao.crearEmpleado(usuario.getId());
                            break;
                        default:
                            mensaje = "2";
                            return new ModelAndView("redirect:/login?mensaje=" + mensaje);
                    }
                    mensaje = "1";
                } else {
                    mensaje = "2";
                }
            }
        } catch (Exception e) {
            mensaje = "2";
            e.printStackTrace();
        }

        return new ModelAndView("redirect:/inicio?mensaje=" + mensaje);
    }
}