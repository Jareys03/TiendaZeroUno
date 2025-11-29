package com.example.mdai.controller;

import com.example.mdai.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class GlobalModelAttributes {

    private static final Logger logger = LoggerFactory.getLogger(GlobalModelAttributes.class);

    @ModelAttribute("usuarioLogeado")
    public Usuario usuarioLogeado(HttpSession session) {
        try {
            Object u = session.getAttribute("usuarioLogeado");
            if (u instanceof Usuario) return (Usuario) u;
            return null;
        } catch (Exception e) {
            logger.error("Error al obtener usuarioLogeado desde la sesión", e);
            return null;
        }
    }

    @ModelAttribute("carritoCount")
    public Integer carritoCount(HttpSession session) {
        try {
            Object c = session.getAttribute("carrito");
            if (c instanceof Map) {
                Map<?,?> m = (Map<?,?>) c;
                int total = 0;
                for (Object v : m.values()) {
                    if (v instanceof Integer) total += (Integer) v;
                    else {
                        try {
                            total += Integer.parseInt(String.valueOf(v));
                        } catch (NumberFormatException ignored) {
                            // si no se puede parsear, ignoramos
                        }
                    }
                }
                return total;
            }
            return 0;
        } catch (ClassCastException e) {
            logger.warn("El atributo 'carrito' en sesión no es un Map, se ignora", e);
            return 0;
        } catch (Exception e) {
            logger.error("Error al calcular carritoCount", e);
            return 0;
        }
    }
}
