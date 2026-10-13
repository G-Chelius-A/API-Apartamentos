package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blue.apartamentos.models.OrganizacionModel;
import com.blue.apartamentos.models.PersonaModel;
import com.blue.apartamentos.models.UsuarioModel;
import com.blue.apartamentos.services.OrganizacionService;
import com.blue.apartamentos.services.PersonaService;
import com.blue.apartamentos.services.UsuarioService;

// Controlador Thymeleaf que agrupa las secciones de Usuarios, Personas y Organizaciones en la vista web
@Controller
@RequestMapping("/vista")
public class UsuarioPersonaOrgVistaController {

    private final UsuarioService usuarioService;
    private final PersonaService personaService;
    private final OrganizacionService organizacionService;

    public UsuarioPersonaOrgVistaController(UsuarioService usuarioService,
                                            PersonaService personaService,
                                            OrganizacionService organizacionService) {
        this.usuarioService = usuarioService;
        this.personaService = personaService;
        this.organizacionService = organizacionService;
    }

    // --- USUARIOS ---

    // Muestra el listado de todos los usuarios del sistema
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        return "usuarios/lista";
    }

    // Abre el formulario en blanco para registrar un nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String formularioUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioModel());
        model.addAttribute("estados", UsuarioModel.EstadoUsuario.values()); // activo | inactivo | suspendido
        return "usuarios/formulario";
    }

    // Carga el formulario de edición con los datos actuales del usuario indicado
    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return usuarioService.obtenerPorId(id)
                .map(u -> {
                    model.addAttribute("usuario", u);
                    model.addAttribute("estados", UsuarioModel.EstadoUsuario.values());
                    return "usuarios/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("msgError", "Usuario no encontrado");
                    return "redirect:/vista/usuarios";
                });
    }

    // Guarda o actualiza un usuario; asigna contraseña temporal si se omite en creación
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute UsuarioModel usuario, RedirectAttributes flash) {
        try {
            if (usuario.getIdUsuario() != null) {
                usuarioService.actualizarUsuario(usuario, usuario.getIdUsuario());
                flash.addFlashAttribute("msgExito", "Usuario actualizado");
            } else {
                if (usuario.getContrasenaHash() == null || usuario.getContrasenaHash().isBlank()) {
                    usuario.setContrasenaHash("default_pass_hash"); // hash temporal; debe cambiarse
                }
                usuarioService.guardarUsuario(usuario);
                flash.addFlashAttribute("msgExito", "Usuario creado exitosamente");
            }
            return "redirect:/vista/usuarios";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al procesar usuario: " + e.getMessage());
            return "redirect:/vista/usuarios/nuevo";
        }
    }

    // Elimina un usuario por ID; muestra error si tiene propiedades o reservaciones asociadas
    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes flash) {
        if (usuarioService.eliminarUsuario(id)) {
            flash.addFlashAttribute("msgExito", "Usuario eliminado");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar el usuario (posiblemente tenga propiedades o reservaciones)");
        }
        return "redirect:/vista/usuarios";
    }

    // --- PERSONAS ---

    // Muestra el listado de todos los perfiles de persona física
    @GetMapping("/personas")
    public String listarPersonas(Model model) {
        model.addAttribute("personas", personaService.obtenerPersonas());
        return "personas/lista";
    }

    // Abre el formulario para registrar un nuevo perfil de persona vinculado a un usuario
    @GetMapping("/personas/nueva")
    public String formularioPersona(Model model) {
        model.addAttribute("persona", new PersonaModel());
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        return "personas/formulario";
    }

    // Guarda un perfil de persona; el servicio valida mayoría de edad y campos obligatorios
    @PostMapping("/personas/guardar")
    public String guardarPersona(@ModelAttribute PersonaModel persona, RedirectAttributes flash) {
        try {
            personaService.guardarPersona(persona);
            flash.addFlashAttribute("msgExito", "Datos de persona guardados correctamente");
            return "redirect:/vista/personas";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar persona: " + e.getMessage());
            return "redirect:/vista/personas/nueva";
        }
    }

    // Elimina un perfil de persona por ID
    @PostMapping("/personas/{id}/eliminar")
    public String eliminarPersona(@PathVariable Long id, RedirectAttributes flash) {
        if (personaService.eliminarPersona(id)) {
            flash.addFlashAttribute("msgExito", "Perfil de persona eliminado");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la persona");
        }
        return "redirect:/vista/personas";
    }

    // --- ORGANIZACIONES ---

    // Muestra el listado de todas las organizaciones (personas morales)
    @GetMapping("/organizaciones")
    public String listarOrganizaciones(Model model) {
        model.addAttribute("organizaciones", organizacionService.obtenerOrganizaciones());
        return "organizaciones/lista";
    }

    // Abre el formulario para registrar una nueva organización vinculada a un usuario
    @GetMapping("/organizaciones/nueva")
    public String formularioOrganizacion(Model model) {
        model.addAttribute("organizacion", new OrganizacionModel());
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        return "organizaciones/formulario";
    }

    // Guarda una nueva organización; el servicio valida RFC, razón social y fecha de constitución
    @PostMapping("/organizaciones/guardar")
    public String guardarOrganizacion(@ModelAttribute OrganizacionModel org, RedirectAttributes flash) {
        try {
            organizacionService.guardarOrganizacion(org);
            flash.addFlashAttribute("msgExito", "Organización guardada correctamente");
            return "redirect:/vista/organizaciones";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar organización: " + e.getMessage());
            return "redirect:/vista/organizaciones/nueva";
        }
    }

    // Elimina una organización por ID
    @PostMapping("/organizaciones/{id}/eliminar")
    public String eliminarOrganizacion(@PathVariable Long id, RedirectAttributes flash) {
        if (organizacionService.eliminarOrganizacion(id)) {
            flash.addFlashAttribute("msgExito", "Organización eliminada");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la organización");
        }
        return "redirect:/vista/organizaciones";
    }
}
