package com.blue.apartamentos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una reservación de una unidad rentable en el sistema de alquiler.
 *
 * <p>Una {@code ReservacionModel} registra el acuerdo entre un cliente ({@link UsuarioModel})
 * y una propiedad ({@link UnidadRentableModel}) para un periodo de estancia determinado.
 * Incluye datos de fechas, huéspedes, precio calculado, estado del proceso y un código único
 * de identificación de la reserva.</p>
 *
 * <p>El ciclo de vida de una reservación sigue una máquina de estados:
 * {@code pendiente → confirmada → en_curso → completada}, con las salidas alternativas
 * {@code cancelada} y {@code no_presento}.
 * La lógica de validación de estado está en
 * {@link com.blue.apartamentos.services.ReservacionService}.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code reservaciones}.</p>
 *
 * @see UnidadRentableModel
 * @see UsuarioModel
 * @see PagoModel
 * @see com.blue.apartamentos.services.ReservacionService
 */
@Entity
@Table(name = "reservaciones")
public class ReservacionModel {

    /**
     * Identificador único de la reservación, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_reservacion}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservacion")
    private Long idReservacion;

    /**
     * Unidad rentable objeto de la reservación.
     * Relación {@code @ManyToOne} de carga diferida ({@code LAZY}), obligatoria.
     * Se ignoran las propiedades {@code disponibilidades} al serializar.
     * Columna de unión: {@code id_propiedad}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_propiedad", nullable = false)
    @JsonIgnoreProperties("disponibilidades")
    private UnidadRentableModel propiedad;

    /**
     * Usuario cliente que realizó la reservación.
     * Relación {@code @ManyToOne} de carga diferida ({@code LAZY}), obligatoria.
     * Se ignoran las propiedades sensibles al serializar.
     * Columna de unión: {@code id_cliente}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"contrasenaHash", "unidades"})
    private UsuarioModel cliente;

    /**
     * Fecha de entrada (check-in programado) de la estancia.
     * Obligatoria. Debe ser anterior a {@link #fechaSalida}.
     * Columna: {@code fecha_entrada}.
     */
    @jakarta.validation.constraints.NotNull(message = "La fecha de entrada es obligatoria")
    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    /**
     * Fecha de salida (check-out programado) de la estancia.
     * Obligatoria. Debe ser posterior a {@link #fechaEntrada}.
     * Columna: {@code fecha_salida}.
     */
    @jakarta.validation.constraints.NotNull(message = "La fecha de salida es obligatoria")
    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    /**
     * Fecha y hora real en que el huésped realizó el check-in (opcional hasta que ocurra).
     * Se requiere para cambiar el estado a {@code en_curso}.
     * Columna: {@code fecha_checkin}.
     */
    @Column(name = "fecha_checkin")
    private LocalDateTime fechaCheckin;

    /**
     * Fecha y hora real en que el huésped realizó el check-out (opcional hasta que ocurra).
     * Se requiere para cambiar el estado a {@code completada}.
     * Columna: {@code fecha_checkout}.
     */
    @Column(name = "fecha_checkout")
    private LocalDateTime fechaCheckout;

    /**
     * Número de huéspedes que se alojarán en la unidad durante la estancia.
     * Obligatorio, debe ser al menos 1 y no puede exceder la capacidad de la unidad.
     * Columna: {@code numero_huespedes}.
     */
    @jakarta.validation.constraints.NotNull(message = "El número de huéspedes es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Column(name = "numero_huespedes", nullable = false)
    private Integer numeroHuespedes;

    /**
     * Precio total calculado de la estancia (noches × precio por noche).
     * Se calcula automáticamente en {@link com.blue.apartamentos.services.ReservacionService}
     * al crear o actualizar la reservación. Precisión: 12 dígitos, 2 decimales.
     * Columna: {@code precio_total}.
     */
    @Column(name = "precio_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioTotal;

    /**
     * Importe total de la reservación, sincronizado con {@link #precioTotal}
     * en los eventos {@code @PrePersist} y {@code @PreUpdate}.
     * Se mantiene como campo adicional para auditoría o ajustes futuros.
     * Columna: {@code importe_total}.
     */
    @Column(name = "importe_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal importeTotal;

    /**
     * Fecha y hora en que se registró la reservación en el sistema.
     * Se asigna automáticamente en {@link #asignarFechaReservacion()} si no se proporciona.
     * Columna: {@code fecha_reservacion}.
     */
    @Column(name = "fecha_reservacion", nullable = false)
    private java.time.LocalDateTime fechaReservacion;

    /**
     * Notas adicionales del huésped relacionadas con la reservación (opcional).
     * Almacenado como texto largo ({@code text}).
     * Columna: {@code notas}.
     */
    @Column(name = "notas", columnDefinition = "text")
    private String notas;

    /**
     * Código único alfanumérico que identifica la reservación externamente.
     * Se genera automáticamente si no se proporciona (prefijo {@code COD-} + timestamp).
     * Máximo 30 caracteres.
     * Columna: {@code codigo_reserva}.
     */
    @Column(name = "codigo_reserva", nullable = false, length = 30)
    private String codigoReserva;

    /**
     * Estado actual de la reservación en la máquina de estados.
     * Almacenado como cadena de texto ({@code EnumType.STRING}).
     * El valor por defecto es {@link Estado#pendiente}.
     * Los valores posibles se definen en {@link Estado}.
     * Columna: {@code estado}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.pendiente;

    /**
     * Comentario opcional del propietario o del sistema sobre la reservación.
     * Máximo 500 caracteres.
     * Columna: {@code comentario}.
     */
    @Column(name = "comentario", length = 500)
    private String comentario;

    /**
     * Enumeración que representa los posibles estados de una reservación.
     * <ul>
     *   <li>{@code pendiente} – La reservación fue creada pero aún no ha sido confirmada.</li>
     *   <li>{@code confirmada} – El propietario o el pago han confirmado la reservación.</li>
     *   <li>{@code en_curso} – El huésped realizó el check-in y está hospedado actualmente.</li>
     *   <li>{@code completada} – El huésped realizó el check-out y la estancia finalizó.</li>
     *   <li>{@code cancelada} – La reservación fue cancelada antes de la estancia.</li>
     *   <li>{@code no_presento} – El huésped confirmado no se presentó a la propiedad.</li>
     * </ul>
     */
    public enum Estado {
        pendiente, confirmada, en_curso, completada, cancelada, no_presento
    }

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public ReservacionModel() {
    }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaReservacion} si no fue establecida,
     * y sincroniza {@code importeTotal} con {@code precioTotal}.
     */
    @PrePersist
    private void asignarFechaReservacion() {
        if (fechaReservacion == null) {
            fechaReservacion = LocalDateTime.now();
        }
        importeTotal = precioTotal;
    }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de actualizar la entidad ({@code @PreUpdate}).
     * Sincroniza {@code importeTotal} con el valor actual de {@code precioTotal}.
     */
    @PreUpdate
    private void sincronizarImporteTotal() {
        importeTotal = precioTotal;
    }

    /**
     * Retorna el identificador único de la reservación.
     * @return identificador numérico de la reservación
     */
    public Long getIdReservacion() {
        return idReservacion;
    }

    /**
     * Establece el identificador único de la reservación.
     * @param idReservacion identificador a asignar
     */
    public void setIdReservacion(Long idReservacion) {
        this.idReservacion = idReservacion;
    }

    /**
     * Retorna la unidad rentable objeto de la reservación.
     * @return unidad rentable reservada
     */
    public UnidadRentableModel getPropiedad() {
        return propiedad;
    }

    /**
     * Establece la unidad rentable objeto de la reservación.
     * @param propiedad unidad rentable a asignar
     */
    public void setPropiedad(UnidadRentableModel propiedad) {
        this.propiedad = propiedad;
    }

    /**
     * Retorna el usuario cliente que realizó la reservación.
     * @return cliente de la reservación
     */
    public UsuarioModel getCliente() {
        return cliente;
    }

    /**
     * Establece el usuario cliente de la reservación.
     * @param cliente usuario cliente a asignar
     */
    public void setCliente(UsuarioModel cliente) {
        this.cliente = cliente;
    }

    /**
     * Retorna la fecha de entrada programada de la estancia.
     * @return fecha de entrada
     */
    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * Establece la fecha de entrada programada de la estancia.
     * @param fechaEntrada fecha de entrada a asignar
     */
    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * Retorna la fecha de salida programada de la estancia.
     * @return fecha de salida
     */
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    /**
     * Establece la fecha de salida programada de la estancia.
     * @param fechaSalida fecha de salida a asignar
     */
    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /**
     * Retorna la fecha y hora real del check-in del huésped.
     * @return fecha de check-in o {@code null} si aún no ocurre
     */
    public LocalDateTime getFechaCheckin() {
        return fechaCheckin;
    }

    /**
     * Establece la fecha y hora real del check-in del huésped.
     * @param fechaCheckin fecha de check-in a asignar
     */
    public void setFechaCheckin(LocalDateTime fechaCheckin) {
        this.fechaCheckin = fechaCheckin;
    }

    /**
     * Retorna la fecha y hora real del check-out del huésped.
     * @return fecha de check-out o {@code null} si aún no ocurre
     */
    public LocalDateTime getFechaCheckout() {
        return fechaCheckout;
    }

    /**
     * Establece la fecha y hora real del check-out del huésped.
     * @param fechaCheckout fecha de check-out a asignar
     */
    public void setFechaCheckout(LocalDateTime fechaCheckout) {
        this.fechaCheckout = fechaCheckout;
    }

    /**
     * Retorna el número de huéspedes de la reservación.
     * @return número de huéspedes
     */
    public Integer getNumeroHuespedes() {
        return numeroHuespedes;
    }

    /**
     * Establece el número de huéspedes de la reservación.
     * @param numeroHuespedes número de huéspedes a asignar
     */
    public void setNumeroHuespedes(Integer numeroHuespedes) {
        this.numeroHuespedes = numeroHuespedes;
    }

    /**
     * Retorna el precio total calculado de la estancia.
     * @return precio total
     */
    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    /**
     * Establece el precio total de la estancia.
     * @param precioTotal precio total a asignar
     */
    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    /**
     * Retorna la fecha y hora en que se registró la reservación.
     * @return fecha de la reservación
     */
    public java.time.LocalDateTime getFechaReservacion() {
        return fechaReservacion;
    }

    /**
     * Establece la fecha y hora de registro de la reservación.
     * @param fechaReservacion fecha de reservación a asignar
     */
    public void setFechaReservacion(java.time.LocalDateTime fechaReservacion) {
        this.fechaReservacion = fechaReservacion;
    }

    /**
     * Retorna las notas adicionales del huésped para la reservación.
     * @return notas o {@code null} si no se proporcionaron
     */
    public String getNotas() {
        return notas;
    }

    /**
     * Establece las notas adicionales del huésped.
     * @param notas notas a asignar
     */
    public void setNotas(String notas) {
        this.notas = notas;
    }

    /**
     * Retorna el código único de la reservación.
     * @return código de la reserva
     */
    public String getCodigoReserva() {
        return codigoReserva;
    }

    /**
     * Establece el código único de la reservación.
     * @param codigoReserva código de reserva a asignar
     */
    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    /**
     * Retorna el estado actual de la reservación.
     * @return estado de la reservación ({@link Estado})
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la reservación.
     * @param estado estado a asignar
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Retorna el comentario asociado a la reservación.
     * @return comentario o {@code null} si no hay uno
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Establece el comentario de la reservación.
     * @param comentario comentario a asignar
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}