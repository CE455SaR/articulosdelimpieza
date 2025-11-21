package com.exa.base.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.exa.base.dao.PedidoDao;
import com.exa.base.dao.ProductoDao;
import com.exa.base.dao.UsuarioDao;
import com.exa.base.model.DetallePedido;
import com.exa.base.model.Pedido;
import com.exa.base.model.ProductoVendido;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    @Autowired
    private PedidoDao pedidoDao;
    
    @Autowired
    private ProductoDao productoDao;
    
    @Autowired
    private UsuarioDao usuarioDao;
    
    @GetMapping("/pedidos")
    public ModelAndView verPedidos(
            @RequestParam(required = false) String estado) {
        
        ModelAndView mav = new ModelAndView("empleado/pedidos");
        List<Pedido> pedidos;
        
        // Obtener todos los pedidos o filtrar por estado
        if (estado != null && !estado.isEmpty()) {
            pedidos = pedidoDao.obtenerPedidosPorEstado(estado);
            mav.addObject("estadoSeleccionado", estado);
        } else {
            pedidos = pedidoDao.obtenerTodosPedidos();
        }
        
        // Obtener información adicional para cada pedido
        for (Pedido pedido : pedidos) {
            // Obtener el nombre del cliente para cada pedido
            String nombreCliente = usuarioDao.obtenerNombreCliente(pedido.getIdCliente());
            pedido.setNombreCliente(nombreCliente);
            
            // Obtener la dirección del cliente para cada pedido
            String direccionCliente = usuarioDao.obtenerDireccionCliente(pedido.getIdCliente());
            pedido.setDireccionCliente(direccionCliente);
        }
        
        mav.addObject("pedidos", pedidos);
        return mav;
    }
    
    @GetMapping("/pedido/{id}")
    public ModelAndView verDetallePedido(@PathVariable("id") Integer idPedido) {
        ModelAndView mav = new ModelAndView("empleado/detalle-pedido");
        
        Pedido pedido = pedidoDao.obtenerPedidoPorId(idPedido);
        
        // Obtener el nombre y dirección del cliente
        String nombreCliente = usuarioDao.obtenerNombreCliente(pedido.getIdCliente());
        pedido.setNombreCliente(nombreCliente);
        
        String direccionCliente = usuarioDao.obtenerDireccionCliente(pedido.getIdCliente());
        pedido.setDireccionCliente(direccionCliente);
        
        // Obtener detalles del pedido
        List<DetallePedido> detalles = pedidoDao.obtenerDetallesPedido(idPedido);
        pedido.setDetalles(detalles);
        
        mav.addObject("pedido", pedido);
        return mav;
    }
    
    @PostMapping("/pedido/actualizar-estado")
    public String actualizarEstadoPedido(
            @RequestParam Integer idPedido,
            @RequestParam String nuevoEstado) {
        
        pedidoDao.actualizarEstadoPedido(idPedido, nuevoEstado);
        return "redirect:/empleado/pedido/" + idPedido;
    }
    
    @GetMapping("/productos-mas-vendidos")
    public ModelAndView productosMasVendidos(
            @RequestParam(required = false, defaultValue = "SEMANA") String periodo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        
        ModelAndView mav = new ModelAndView("empleado/productos-mas-vendidos");
        
        // Si no se proporcionan fechas, calcular fechas según periodo
        if (fechaInicio == null || fechaFin == null) {
            Calendar calendar = Calendar.getInstance();
            fechaFin = calendar.getTime(); // Hoy
            
            calendar = Calendar.getInstance();
            switch (periodo) {
                case "SEMANA":
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    break;
                case "MES":
                    calendar.add(Calendar.MONTH, -1);
                    break;
                case "TRIMESTRE":
                    calendar.add(Calendar.MONTH, -3);
                    break;
                default:
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
            }
            fechaInicio = calendar.getTime();
        }
        
        List<ProductoVendido> productosMasVendidos = 
                productoDao.obtenerProductosMasVendidos(fechaInicio, fechaFin);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        mav.addObject("periodo", periodo);
        mav.addObject("fechaInicio", sdf.format(fechaInicio));
        mav.addObject("fechaFin", sdf.format(fechaFin));
        mav.addObject("productos", productosMasVendidos);
        
        return mav;
    }
}