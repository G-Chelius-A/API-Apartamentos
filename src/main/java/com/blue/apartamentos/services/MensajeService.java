package com.blue.apartamentos.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.MensajeModel;
import com.blue.apartamentos.repositories.IMensajeRepository;

// Lógica de negocio para mensajes internos entre usuarios
@Service
public class MensajeService {
    @Autowired
    private IMensajeRepository mensajeRepository;

    // Retorna todos los mensajes del sistema
    public ArrayList<MensajeModel> obtenerMensajes() { return (ArrayList<MensajeModel>) mensajeRepository.findAll(); }

    // Guarda un nuevo mensaje sin validaciones adicionales
    public MensajeModel guardarMensaje(MensajeModel mensaje) { return mensajeRepository.save(mensaje); }

    // Busca un mensaje por ID; retorna Optional vacío si no existe
    public Optional<MensajeModel> obtenerPorId(Long id) { return mensajeRepository.findById(id); }

    // Actualiza asunto, contenido y estado de leído de un mensaje existente
    public MensajeModel actualizarMensaje(MensajeModel request, Long id) {
        MensajeModel mensaje = mensajeRepository.findById(id).orElseThrow(() -> new RuntimeException("Mensaje no encontrado con el ID: " + id));
        mensaje.setAsunto(request.getAsunto());
        mensaje.setContenido(request.getContenido());
        mensaje.setLeido(request.getLeido());
        return mensajeRepository.save(mensaje);
    }

    // Elimina un mensaje por ID; retorna false si falla
    public boolean eliminarMensaje(Long id) {
        try { mensajeRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra mensajes enviados por un usuario (remitente)
    public ArrayList<MensajeModel> obtenerPorRemitente(Long idUsuario) { return mensajeRepository.findByRemitente_IdUsuario(idUsuario); }
    // Filtra mensajes recibidos por un usuario (destinatario)
    public ArrayList<MensajeModel> obtenerPorDestinatario(Long idUsuario) { return mensajeRepository.findByDestinatario_IdUsuario(idUsuario); }
    // Filtra mensajes asociados a una reservación
    public ArrayList<MensajeModel> obtenerPorReservacion(Long idReservacion) { return mensajeRepository.findByReservacion_IdReservacion(idReservacion); }
    // Filtra mensajes por fecha/hora exacta de envío
    public ArrayList<MensajeModel> obtenerPorFechaEnvio(LocalDateTime fecha) { return mensajeRepository.findByFechaEnvio(fecha); }
}