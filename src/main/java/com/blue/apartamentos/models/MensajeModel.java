package com.blue.apartamentos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un mensaje de comunicación entre usuarios dentro del sistema.
 *
 * <p>Un {@code MensajeModel} facilita la comunicación directa entre el huésped y el propietario
 * en el contexto de una {@link ReservacionModel reservación} específica. Cada mensaje tiene
 * un remitente, un destinatario, un asunto y un contenido, y registra si fue leído.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Mensajes}.</p>
 *
 * @see UsuarioModel
 * @see ReservacionModel
 * @see com.blue.apartamentos.services.MensajeService
 */
@Entity
@Table(name = "Mensajes")
public class MensajeModel {

    /**
     * Identificador único del mensaje, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_mensaje}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    /**
     * Usuario que envía el mensaje.
     * Relación {@code @ManyToOne}, obligatoria.
     * Se ignoran propiedades sensibles al serializar.
     * Columna de unión: {@code id_remitente}.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_remitente", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"unidades", "contrasenaHash"})
    private UsuarioModel remitente;

    /**
     * Usuario que recibe el mensaje.
     * Relación {@code @ManyToOne}, obligatoria.
     * Se ignoran propiedades sensibles al serializar.
     * Columna de unión: {@code id_destinatario}.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_destinatario", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"unidades", "contrasenaHash"})
    private UsuarioModel destinatario;

    /**
     * Reservación en cuyo contexto se intercambia el mensaje.
     * Relación {@code @ManyToOne}, obligatoria.
     * Se ignoran propiedades para evitar referencias circulares.
     * Columna de unión: {@code id_reservacion}.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_reservacion", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"propiedad", "cliente"})
    private ReservacionModel reservacion;

    /**
     * Asunto o título del mensaje.
     * Obligatorio, máximo 200 caracteres.
     * Columna: {@code asunto}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El asunto del mensaje es obligatorio")
    @jakarta.validation.constraints.Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String asunto;

    /**
     * Contenido o cuerpo del mensaje.
     * Obligatorio, almacenado como texto largo ({@code text}).
     * Columna: {@code contenido}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El contenido del mensaje es obligatorio")
    @Column(nullable = false, columnDefinition = "text")
    private String contenido;

    /**
     * Indica si el destinatario ha leído el mensaje ({@code true}) o no ({@code false}).
     * Obligatorio.
     * Columna: {@code leido}.
     */
    @jakarta.validation.constraints.NotNull(message = "Debe indicar el estado de lectura del mensaje")
    @Column(nullable = false)
    private Boolean leido;

    /**
     * Fecha y hora en que fue enviado el mensaje.
     * Se asigna automáticamente en {@link #asignarFechaEnvio()} si no fue establecida.
     * Columna: {@code fecha_envio}.
     */
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public MensajeModel() {
    }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaEnvio} si no fue establecida previamente.
     */
    @PrePersist
    private void asignarFechaEnvio() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
    }

    /**
     * Retorna el identificador único del mensaje.
     * @return identificador numérico del mensaje
     */
    public Long getIdMensaje() {
        return idMensaje;
    }

    /**
     * Establece el identificador único del mensaje.
     * @param value identificador a asignar
     */
    public void setIdMensaje(Long value) {
        idMensaje = value;
    }

    /**
     * Retorna el usuario remitente del mensaje.
     * @return remitente del mensaje
     */
    public UsuarioModel getRemitente() {
        return remitente;
    }

    /**
     * Establece el usuario remitente del mensaje.
     * @param value remitente a asignar
     */
    public void setRemitente(UsuarioModel value) {
        remitente = value;
    }

    /**
     * Retorna el usuario destinatario del mensaje.
     * @return destinatario del mensaje
     */
    public UsuarioModel getDestinatario() {
        return destinatario;
    }

    /**
     * Establece el usuario destinatario del mensaje.
     * @param value destinatario a asignar
     */
    public void setDestinatario(UsuarioModel value) {
        destinatario = value;
    }

    /**
     * Retorna la reservación en cuyo contexto se intercambia el mensaje.
     * @return reservación asociada
     */
    public ReservacionModel getReservacion() {
        return reservacion;
    }

    /**
     * Establece la reservación en cuyo contexto se intercambia el mensaje.
     * @param value reservación a asignar
     */
    public void setReservacion(ReservacionModel value) {
        reservacion = value;
    }

    /**
     * Retorna el asunto del mensaje.
     * @return asunto del mensaje
     */
    public String getAsunto() {
        return asunto;
    }

    /**
     * Establece el asunto del mensaje.
     * @param value asunto a asignar
     */
    public void setAsunto(String value) {
        asunto = value;
    }

    /**
     * Retorna el contenido del mensaje.
     * @return contenido del mensaje
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Establece el contenido del mensaje.
     * @param value contenido a asignar
     */
    public void setContenido(String value) {
        contenido = value;
    }

    /**
     * Retorna si el mensaje ha sido leído por el destinatario.
     * @return {@code true} si fue leído, {@code false} si está sin leer
     */
    public Boolean getLeido() {
        return leido;
    }

    /**
     * Establece si el mensaje fue leído por el destinatario.
     * @param value estado de lectura a asignar
     */
    public void setLeido(Boolean value) {
        leido = value;
    }

    /**
     * Retorna la fecha y hora de envío del mensaje.
     * @return fecha de envío
     */
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * Establece la fecha y hora de envío del mensaje.
     * @param value fecha de envío a asignar
     */
    public void setFechaEnvio(LocalDateTime value) {
        fechaEnvio = value;
    }
}
