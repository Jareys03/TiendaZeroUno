package com.example.mdai.controller;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.DireccionService;
import com.example.mdai.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios/{usuarioId}/direcciones")
public class DireccionController {

    private final UsuarioService usuarioService;
    private final DireccionService direccionService;

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
        Optional<Usuario> opt = usuarioService.findById(usuarioId);
        return opt.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
    }

    /**
     * Lista todas las direcciones del usuario
     * GET /usuarios/{usuarioId}/direcciones
     */
    @GetMapping
    public String listar(@ModelAttribute("usuario") Usuario usuario, Model model) {

        // Opción A: si tienes un método específico en el servicio:
        // List<Direccion> direcciones = direccionService.listarPorUsuario(usuario.getId());

        // Opción B (segura): si no lo tienes, puedes de momento usar el propio usuario
        // suponiendo que tenga getDirecciones() mapeado.
        List<Direccion> direcciones = usuario.getDirecciones();

        model.addAttribute("direcciones", direcciones);
        return "direcciones/lista"; // templates/direcciones/lista.html
    }

    /**
     * Muestra formulario de nueva dirección
     * GET /usuarios/{usuarioId}/direcciones/nueva
     */
    @GetMapping("/nueva")
    public String nueva(@ModelAttribute("usuario") Usuario usuario, Model model) {
        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario); // establecemos la relación
        model.addAttribute("direccion", direccion);
        return "direcciones/form"; // templates/direcciones/form.html
    }

    /**
     * Crea una nueva dirección para el usuario
     * POST /usuarios/{usuarioId}/direcciones
     */
    @PostMapping
    public String crear(@ModelAttribute("usuario") Usuario usuario,
                        @ModelAttribute("direccion") Direccion direccion,
                        BindingResult br) {

        if (br.hasErrors()) {
            return "direcciones/form";
        }

        direccion.setUsuario(usuario);

        // Sin tocar services: usamos el CRUD típico del DireccionService
        direccionService.save(direccion);

        // Otra opción (cuando queramos usarla): usuarioService.agregarDireccion(usuario.getId(), direccion);

        return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
    }

    /**
     * Muestra formulario de edición
     * GET /usuarios/{usuarioId}/direcciones/{direccionId}/editar
     */
    @GetMapping("/{direccionId}/editar")
    public String editar(@PathVariable Long direccionId,
                         @ModelAttribute("usuario") Usuario usuario,
                         Model model) {

        Optional<Direccion> dOpt = direccionService.findById(direccionId);
        if (dOpt.isEmpty()) {
            return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
        }

        Direccion direccion = dOpt.get();

        // (Opcional) asegurar que pertenece al usuario
        if (direccion.getUsuario() == null ||
                !direccion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
        }

        model.addAttribute("direccion", direccion);
        return "direcciones/form";
    }

    /**
     * Actualiza una dirección existente
     * POST /usuarios/{usuarioId}/direcciones/{direccionId}/actualizar
     */
    @PostMapping("/{direccionId}/actualizar")
    public String actualizar(@PathVariable Long direccionId,
                             @ModelAttribute("usuario") Usuario usuario,
                             @ModelAttribute("direccion") Direccion direccion,
                             BindingResult br) {

        if (br.hasErrors()) {
            return "direcciones/form";
        }

        direccion.setId(direccionId);
        direccion.setUsuario(usuario);

        // Para no tocar services, usamos save() que en JPA hace update si el id no es null
        direccionService.save(direccion);

        return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
    }

    /**
     * Elimina una dirección del usuario
     * GET /usuarios/{usuarioId}/direcciones/{direccionId}/eliminar
     */
    @GetMapping("/{direccionId}/eliminar")
    public String eliminar(@PathVariable Long direccionId,
                           @ModelAttribute("usuario") Usuario usuario) {

        direccionService.deleteById(direccionId);
        return "redirect:/usuarios/" + usuario.getId() + "/direcciones";
    }
}
