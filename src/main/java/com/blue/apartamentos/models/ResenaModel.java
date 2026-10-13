package com.blue.apartamentos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la reseña que un cliente deja sobre una estancia completada.
 *
 * <p>Una {@code ResenaModel} está vinculada de forma única a una {@link ReservacionModel reservación}
 * completada, garantizando que solo exista una reseña por estancia (restricción de unicidad en
 * la columna {@code id_reservacion}). Contiene calificaciones numéricas en diferentes categorías
 * (limpieza, ubicación, comunicación y calificación general) en una escala del 1 al 10,
 * además de un comentario de texto libre y la posibilidad de que el propietario responda.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code resenas}.</p>
 *
 * @see ReservacionModel
 * @see com.blue.apartamentos.services.ResenaService
 */
@Entity
@Table(name = "resenas", uniqueConstraints = {
    @UniqueConstraint(name = "uk_resena_reservacion", columnNames = "id_reservacion")
})
public class ResenaModel {

    /**
     * Identificador único de la reseña, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_resena}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long idResena;

    /**
     * Reservación completada a la que corresponde esta reseña.
     * Relación {@code @ManyToOne} de carga diferida ({@code LAZY}), obligatoria y única
     * (solo una reseña por reservación).
     * Se ignoran propiedades para evitar referencias circulares en la serialización JSON.
     * Columna de unión: {@code id_reservacion}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reservacion", nullable = false)
    @JsonIgnoreProperties({"propiedad", "cliente"})
    private ReservacionModel reservacion;

    /**
     * Calificación de la limpieza de la unidad rentable durante la estancia.
     * Obligatoria, en escala del 1 al 10.
     * Columna: {@code calificacion_limpieza}.
     */
    @jakarta.validation.constraints.NotNull(message = "La calificación de limpieza es obligatoria")
    @jakarta.validation.constraints.Min(value = 1, message = "La calificación de limpieza debe ser al menos 1")
    @jakarta.validation.constraints.Max(value = 10, message = "La calificación de limpieza no puede exceder 10")
    @Column(name = "calificacion_limpieza", nullable = false)
    private Integer calificacionLimpieza;

    /**
     * Calificación de la ubicación de la unidad rentable.
     * Obligatoria, en escala del 1 al 10.
     * Columna: {@code calificacion_ubicacion}.
     */
    @jakarta.validation.constraints.NotNull(message = "La calificación de ubicación es obligatoria")
    @jakarta.validation.constraints.Min(value = 1, message = "La calificación de ubicación debe ser al menos 1")
    @jakarta.validation.constraints.Max(value = 10, message = "La calificación de ubicación no puede exceder 10")
    @Column(name = "calificacion_ubicacion", nullable = false)
    private Integer calificacionUbicacion;

    /**
     * Calificación general de la experiencia de hospedaje.
     * Obligatoria, en escala del 1 al 10.
     * Columna: {@code calificacion_general}.
     */
    @jakarta.validation.constraints.NotNull(message = "La calificación general es obligatoria")
    @jakarta.validation.constraints.Min(value = 1, message = "La calificación general debe ser al menos 1")
    @jakarta.validation.constraints.Max(value = 10, message = "La calificación general no puede exceder 10")
    @Column(name = "calificacion_general", nullable = false)
    private Integer calificacionGeneral;

    /**
     * Calificación de la comunicación con el propietario durante la estancia.
     * Obligatoria, en escala del 1 al 10.
     * Columna: {@code calificacion_comunicacion}.
     */
    @jakarta.validation.constraints.NotNull(message = "La calificación de comunicación es obligatoria")
    @jakarta.validation.constraints.Min(value = 1, message = "La calificación de comunicación debe ser al menos 1")
    @jakarta.validation.constraints.Max(value = 10, message = "La calificación de comunicación no puede exceder 10")
    @Column(name = "calificacion_comunicacion", nullable = false)
    private Integer calificacionComunicacion;

    /**
     * Comentario de texto libre escrito por el cliente sobre su experiencia (opcional).
     * Máximo 1000 caracteres.
     * Columna: {@code comentario}.
     */
    @jakarta.validation.constraints.Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    @Column(name = "comentario", length = 1000)
    private String comentario;

    /**
     * Fecha y hora en que se publicó la reseña.
     * Se asigna automáticamente en {@link #asignarFechaResena()} si no fue establecida.
     * Columna: {@code fecha_resena}.
     */
    @Column(name = "fecha_resena", nullable = false)
    private LocalDateTime fechaResena;

    /**
     * Respuesta del propietario a la reseña del cliente (opcional).
     * Almacenada como texto largo ({@code text}).
     * Columna: {@code respuesta_propietario}.
     */
    @Column(name = "respuesta_propietario", columnDefinition = "text")
    private String respuestaPropietario;

    /**
     * Fecha y hora en que el propietario publicó su respuesta a la reseña (opcional).
     * Columna: {@code fecha_respuesta}.
     */
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public ResenaModel() { }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaResena} si no fue establecida previamente.
     */
    @PrePersist
    private void asignarFechaResena() {
        if (fechaResena == null) {
            fechaResena = LocalDateTime.now();
        }
    }

    /**
     * Retorna el identificador único de la reseña.
     * @return identificador numérico de la reseña
     */
    public Long getIdResena() { return idResena; }

    /**
     * Establece el identificador único de la reseña.
     * @param idResena identificador a asignar
     */
    public void setIdResena(Long idResena) { this.idResena = idResena; }

    /**
     * Retorna la reservación asociada a esta reseña.
     * @return reservación evaluada
     */
    public ReservacionModel getReservacion() { return reservacion; }

    /**
     * Establece la reservación asociada a esta reseña.
     * @param reservacion reservación a asignar
     */
    public void setReservacion(ReservacionModel reservacion) { this.reservacion = reservacion; }

    /**
     * Retorna la calificación de limpieza.
     * @return calificación de limpieza (1-10)
     */
    public Integer getCalificacionLimpieza() { return calificacionLimpieza; }

    /**
     * Establece la calificación de limpieza.
     * @param value calificación de limpieza a asignar (1-10)
     */
    public void setCalificacionLimpieza(Integer value) { calificacionLimpieza = value; }

    /**
     * Retorna la calificación de ubicación.
     * @return calificación de ubicación (1-10)
     */
    public Integer getCalificacionUbicacion() { return calificacionUbicacion; }

    /**
     * Establece la calificación de ubicación.
     * @param value calificación de ubicación a asignar (1-10)
     */
    public void setCalificacionUbicacion(Integer value) { calificacionUbicacion = value; }

    /**
     * Retorna la calificación general de la estancia.
     * @return calificación general (1-10)
     */
    public Integer getCalificacionGeneral() { return calificacionGeneral; }

    /**
     * Establece la calificación general de la estancia.
     * @param calificacionGeneral calificación general a asignar (1-10)
     */
    public void setCalificacionGeneral(Integer calificacionGeneral) { this.calificacionGeneral = calificacionGeneral; }

    /**
     * Retorna la calificación de comunicación con el propietario.
     * @return calificación de comunicación (1-10)
     */
    public Integer getCalificacionComunicacion() { return calificacionComunicacion; }

    /**
     * Establece la calificación de comunicación con el propietario.
     * @param calificacionComunicacion calificación de comunicación a asignar (1-10)
     */
    public void setCalificacionComunicacion(Integer calificacionComunicacion) { this.calificacionComunicacion = calificacionComunicacion; }

    /**
     * Retorna el comentario de texto libre del cliente.
     * @return comentario o {@code null} si no fue proporcionado
     */
    public String getComentario() { return comentario; }

    /**
     * Establece el comentario de texto libre del cliente.
     * @param comentario comentario a asignar
     */
    public void setComentario(String comentario) { this.comentario = comentario; }

    /**
     * Retorna la fecha y hora de publicación de la reseña.
     * @return fecha de la reseña
     */
    public LocalDateTime getFechaResena() { return fechaResena; }

    /**
     * Establece la fecha y hora de publicación de la reseña.
     * @param fechaResena fecha a asignar
     */
    public void setFechaResena(LocalDateTime fechaResena) { this.fechaResena = fechaResena; }

    /**
     * Retorna la respuesta del propietario a esta reseña.
     * @return respuesta del propietario o {@code null} si no ha respondido
     */
    public String getRespuestaPropietario() { return respuestaPropietario; }

    /**
     * Establece la respuesta del propietario a esta reseña.
     * @param value respuesta a asignar
     */
    public void setRespuestaPropietario(String value) { respuestaPropietario = value; }

    /**
     * Retorna la fecha y hora en que el propietario publicó su respuesta.
     * @return fecha de respuesta o {@code null} si no ha respondido
     */
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }

    /**
     * Establece la fecha y hora de la respuesta del propietario.
     * @param value fecha de respuesta a asignar
     */
    public void setFechaRespuesta(LocalDateTime value) { fechaRespuesta = value; }
}