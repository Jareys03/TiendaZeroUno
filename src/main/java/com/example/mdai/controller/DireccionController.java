package com.example.mdai.controller;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.DireccionService;
import com.example.mdai.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios/{usuarioId}/direcciones")
public class DireccionController {

    private final UsuarioService usuarioService;
    private final DireccionService direccionService;
    private static final Logger logger = LoggerFactory.getLogger(DireccionController.class);

    public DireccionController(UsuarioService usuarioService,
                               DireccionService direccionService) {
        this.usuarioService = usuarioService;
        this.direccionService = direccionService;
    }

    /**
     * Carga el usuario en todos los métodos del controlador
     */
    @ModelAttribute("usuario")
    public Usuario cargarUsuario(@PathVariable("usuarioId") Long usuarioId) {
        try {
            Optional<Usuario> opt = usuarioService.findById(usuarioId);
            return opt.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        } catch (Exception e) {
            logger.error("Error al cargar usuario en DireccionController id=" + usuarioId, e);
            throw e;
        }
    }

    /**
     * Lista todas las direcciones del usuario
     * GET /usuarios/{usuarioId}/direcciones
     */
    @GetMapping
    public String listar(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            List<Direccion> direcciones = usuario.getDirecciones();
            model.addAttribute("direcciones", direcciones);
            return "direcciones/lista"; // templates/direcciones/lista.html
        } catch (Exception e) {
            logger.error("Error al listar direcciones para usuario id=" + (usuario != null ? usuario.getId() : "null"), e);
            model.addAttribute("direcciones", Collections.emptyList());
            model.addAttribute("error", "Ocurrió un error al listar las direcciones.");
            return "direcciones/lista";
        }
    }

    /**
     * Muestra formulario de nueva dirección
     * GET /usuarios/{usuarioId}/direcciones/nueva
     */
    @GetMapping("/nueva")
    public String nueva(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            Direccion direccion = new Direccion();
            direccion.setUsuario(usuario); // establecemos la relación
            model.addAttribute("direccion", direccion);
            return "direcciones/form"; // templates/direcciones/form.html
        } catch (Exception e) {
            logger.error("Error al preparar nueva direccion para usuario id=" + (usuario != null ? usuario.getId() : "null"), e);
            model.addAttribute("direccion", new Direccion());
            model.addAttribute("error", "Ocurrió un error al preparar el formulario.");
            return "direcciones/form";
        }
    }

    /**
     * Crea una nueva dirección para el usuario
     * POST /usuarios/{usuarioId}/direcciones
     */
    @PostMapping
    public String crear(@ModelAttribute("usuario") Usuario usuario,
                        @ModelAttribute("direccion") Direccion direccion,
                        BindingResult br,
                        Model model) {

        try {
            if (br.hasErrors()) {
                return "direcciones/form";
            }

            direccion.setUsuario(usuario);

            direccionService.save(direccion);

            return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
        } catch (Exception e) {
            logger.error("Error al crear direccion para usuario id=" + (usuario != null ? usuario.getId() : "null"), e);
            model.addAttribute("error", "Ocurrió un error al crear la dirección.");
            model.addAttribute("direccion", direccion != null ? direccion : new Direccion());
            return "direcciones/form";
        }
    }

    /**
     * Muestra formulario de edición
     * GET /usuarios/{usuarioId}/direcciones/{direccionId}/editar
     */
    @GetMapping("/{direccionId}/editar")
    public String editar(@PathVariable Long direccionId,
                         @ModelAttribute("usuario") Usuario usuario,
                         Model model) {
        try {
            Optional<Direccion> dOpt = direccionService.findById(direccionId);
            if (dOpt.isEmpty()) {
                return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
            }

            Direccion direccion = dOpt.get();

            if (direccion.getUsuario() == null ||
                    !direccion.getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
            }

            model.addAttribute("direccion", direccion);
            return "direcciones/form";
        } catch (Exception e) {
            logger.error("Error al editar direccion id=" + direccionId + " usuarioId=" + (usuario != null ? usuario.getId() : "null"), e);
            return "redirect:/usuarios/" + (usuario != null ? usuario.getId() : "") + "/direcciones";
        }
    }

    /**
     * Actualiza una dirección existente
     * POST /usuarios/{usuarioId}/direcciones/{direccionId}/actualizar
     */
    @PostMapping("/{direccionId}/actualizar")
    public String actualizar(@PathVariable Long direccionId,
                             @ModelAttribute("usuario") Usuario usuario,
                             @ModelAttribute("direccion") Direccion direccion,
                             BindingResult br,
                             Model model) {
        try {
            if (br.hasErrors()) {
                return "direcciones/form";
            }

            direccion.setId(direccionId);
            direccion.setUsuario(usuario);

            direccionService.save(direccion);

            return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
        } catch (Exception e) {
            logger.error("Error al actualizar direccion id=" + direccionId + " usuarioId=" + (usuario != null ? usuario.getId() : "null"), e);
            model.addAttribute("error", "Ocurrió un error al actualizar la dirección.");
            model.addAttribute("direccion", direccion != null ? direccion : new Direccion());
            return "direcciones/form";
        }
    }

    /**
     * Elimina una dirección del usuario
     * GET /usuarios/{usuarioId}/direcciones/{direccionId}/eliminar
     */
    @GetMapping("/{direccionId}/eliminar")
    public String eliminar(@PathVariable Long direccionId,
                           @ModelAttribute("usuario") Usuario usuario) {
        try {
            direccionService.deleteById(direccionId);
            return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
        } catch (Exception e) {
            logger.error("Error al eliminar direccion id=" + direccionId + " usuarioId=" + (usuario != null ? usuario.getId() : "null"), e);
            return "redirect:/usuarios/" + (usuario != null ? usuario.getId() : "") + "/direcciones";
        }
    }
}
