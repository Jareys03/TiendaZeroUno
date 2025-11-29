package com.example.mdai.controller;

import com.example.mdai.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("usuarioLogeado")
    public Usuario usuarioLogeado(HttpSession session) {
        Object u = session.getAttribute("usuarioLogeado");
        if (u instanceof Usuario) return (Usuario) u;
        return null;
    }

    @ModelAttribute("carritoCount")
    public Integer carritoCount(HttpSession session) {
        Object c = session.getAttribute("carrito");
        if (c instanceof Map<?,?> m) {
            int total = 0;
            for (Object v : m.values()) {
                if (v instanceof Integer) total += (Integer) v;
            }
            return total;
        }
        return 0;
    }

    @ModelAttribute("modoAdmin")
    public boolean modoAdmin(HttpSession session) {
        Object m = session.getAttribute("modoAdmin");
        if (m instanceof Boolean) return (Boolean) m;
        return false;
    }
}
