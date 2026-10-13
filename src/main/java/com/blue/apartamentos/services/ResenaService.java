package com.blue.apartamentos.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.models.ResenaModel;
import com.blue.apartamentos.repositories.IReservacionRepository;
import com.blue.apartamentos.repositories.IResenaRepository;

// Lógica de negocio para reseñas de reservaciones completadas
// Cada reservación completada puede tener como máximo una reseña
@Service
public class ResenaService {
    @Autowired
    private IResenaRepository resenaRepository;
    @Autowired
    private IReservacionRepository reservacionRepository; // para validar que la reservación esté completada

    // Retorna todas las reseñas registradas
    public ArrayList<ResenaModel> obtenerResenas() { return (ArrayList<ResenaModel>) resenaRepository.findAll(); }

    // Guarda una nueva reseña; valida que la reservación esté completada y no tenga reseña previa
    @Transactional
    public ResenaModel guardarResena(ResenaModel resena) {
        ReservacionModel reservacion = validarResena(resena, null);
        if (!resenaRepository.findByReservacion_IdReservacion(reservacion.getIdReservacion()).isEmpty()) {
            throw new IllegalStateException("La reservación ya tiene una reseña");
        }
        resena.setReservacion(reservacion);
        return resenaRepository.save(resena);
    }

    // Busca una reseña por ID; retorna Optional vacío si no existe
    public Optional<ResenaModel> obtenerPorId(Long id) { return resenaRepository.findById(id); }

    // Actualiza calificaciones (limpieza, ubicación, general, comunicación), comentario y respuesta del propietario
    @Transactional
    public ResenaModel actualizarResena(ResenaModel request, Long id) {
        ResenaModel resena = resenaRepository.findById(id).orElseThrow(() -> new RuntimeException("Resena no encontrada con el ID: " + id));
        validarResena(resena, id);
        resena.setCalificacionLimpieza(request.getCalificacionLimpieza());
        resena.setCalificacionUbicacion(request.getCalificacionUbicacion());
        resena.setCalificacionGeneral(request.getCalificacionGeneral());
        resena.setCalificacionComunicacion(request.getCalificacionComunicacion());
        validarCalificaciones(resena);
        resena.setComentario(request.getComentario());
        resena.setRespuestaPropietario(request.getRespuestaPropietario());
        resena.setFechaRespuesta(request.getFechaRespuesta());
        return resenaRepository.save(resena);
    }

    // Valida que la reservación exista, tenga cliente, esté completada y no tenga otra reseña
    private ReservacionModel validarResena(ResenaModel resena, Long idResena) {
        if (resena == null || resena.getReservacion() == null
                || resena.getReservacion().getIdReservacion() == null) {
            throw new IllegalArgumentException("La reservación de la reseña es obligatoria");
        }
        ReservacionModel reservacion = reservacionRepository.findById(resena.getReservacion().getIdReservacion())
                .orElseThrow(() -> new IllegalArgumentException("La reservación no existe"));
        if (reservacion.getCliente() == null || reservacion.getCliente().getIdUsuario() == null) {
            throw new IllegalStateException("La reservación no tiene un cliente válido");
        }
        if (reservacion.getEstado() != ReservacionModel.Estado.completada) {
            throw new IllegalStateException("Solo se puede reseñar una reservación completada");
        }
        if (idResena != null) {
            boolean existeOtra = resenaRepository.findByReservacion_IdReservacion(reservacion.getIdReservacion()).stream()
                    .anyMatch(item -> !item.getIdResena().equals(idResena));
            if (existeOtra) {
                throw new IllegalStateException("La reservación ya tiene otra reseña");
            }
        }
        validarCalificaciones(resena);
        return reservacion;
    }

    // Valida que todas las calificaciones estén en el rango permitido (1-10)
    private void validarCalificaciones(ResenaModel resena) {
        validarCalificacion(resena.getCalificacionLimpieza(), "La calificación de limpieza");
        validarCalificacion(resena.getCalificacionUbicacion(), "La calificación de ubicación");
        validarCalificacion(resena.getCalificacionGeneral(), "La calificación general");
        validarCalificacion(resena.getCalificacionComunicacion(), "La calificación de comunicación");
    }

    // Valida que una calificación individual esté entre 1 y 10
    private void validarCalificacion(Integer calificacion, String campo) {
        if (calificacion == null || calificacion < 1 || calificacion > 10) {
            throw new IllegalArgumentException(campo + " debe estar entre 1 y 10");
        }
    }

    // Elimina una reseña por ID; retorna false si falla
    public boolean eliminarResena(Long id) {
        try { resenaRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra reseñas por reservación
    public ArrayList<ResenaModel> obtenerPorReservacion(Long idReservacion) { return resenaRepository.findByReservacion_IdReservacion(idReservacion); }
    // Filtra reseñas por unidad rentable (a través de la reservación)
    public ArrayList<ResenaModel> obtenerPorUnidad(Long idUnidad) { return resenaRepository.findByReservacion_Propiedad_IdUnidad(idUnidad); }
    // Filtra reseñas por cliente
    public ArrayList<ResenaModel> obtenerPorCliente(Long idUsuario) { return resenaRepository.findByReservacion_Cliente_IdUsuario(idUsuario); }
    // Filtra reseñas por calificación general exacta
    public ArrayList<ResenaModel> obtenerPorCalificacion(Integer calificacion) { return resenaRepository.findByCalificacionGeneral(calificacion); }
    // Filtra reseñas por fecha/hora exacta
    public ArrayList<ResenaModel> obtenerPorFecha(LocalDateTime fecha) { return resenaRepository.findByFechaResena(fecha); }
}