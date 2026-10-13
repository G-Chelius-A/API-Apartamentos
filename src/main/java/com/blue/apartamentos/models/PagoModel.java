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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un pago asociado a una {@link ReservacionModel reservación}.
 *
 * <p>Un {@code PagoModel} registra cada transacción económica realizada por el cliente
 * para cubrir el costo de una reservación. Un mismo pago puede cubrir el monto total o
 * parcial; múltiples pagos pueden existir por reservación. El estado del pago determina
 * si la reservación puede confirmarse y si aplica un reembolso posterior.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code pagos}.</p>
 *
 * @see ReservacionModel
 * @see ReembolsoModel
 * @see com.blue.apartamentos.services.PagoService
 */
@Entity
@Table(name = "pagos")
public class PagoModel {

    /**
     * Identificador único del pago, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_pago}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    /**
     * Reservación a la que corresponde este pago.
     * Relación {@code @ManyToOne} de carga diferida ({@code LAZY}), obligatoria.
     * Se ignoran propiedades para evitar recursividad en la serialización JSON.
     * Columna de unión: {@code id_reservacion}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reservacion", nullable = false)
    @JsonIgnoreProperties({"propiedad", "cliente"})
    private ReservacionModel reservacion;

    /**
     * Monto de este pago en la moneda configurada.
     * Obligatorio y debe ser mayor que cero. Precisión: 12 dígitos, 2 decimales.
     * No puede exceder el precio total de la reservación.
     * Columna: {@code monto}.
     */
    @jakarta.validation.constraints.NotNull(message = "El monto del pago es obligatorio")
    @jakarta.validation.constraints.DecimalMin(value = "0.01", message = "El monto del pago debe ser mayor que cero")
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /**
     * Fecha y hora en que se realizó el pago.
     * Se asigna automáticamente en {@link #asignarFechaPago()} si no se proporciona.
     * Columna: {@code fecha_pago}.
     */
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    /**
     * Referencia externa o número de transacción único proporcionado por la pasarela de pago.
     * Obligatoria, única en el sistema y con un máximo de 100 caracteres.
     * Columna: {@code referencia_pago}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La referencia del pago es obligatoria")
    @jakarta.validation.constraints.Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    @Column(name = "referencia_pago", nullable = false, unique = true, length = 100)
    private String referenciaPago;

    /**
     * Datos adicionales del pago en formato libre (p. ej. detalles de la transacción,
     * últimos 4 dígitos de tarjeta, etc.).
     * Obligatorios, almacenados como texto largo ({@code text}).
     * Columna: {@code datos_pago}.
     */
    @jakarta.validation.constraints.NotBlank(message = "Los datos del pago son obligatorios")
    @Column(name = "datos_pago", nullable = false, columnDefinition = "text")
    private String datosPago;

    /**
     * Método de pago utilizado en la transacción.
     * Almacenado como cadena de texto ({@code EnumType.STRING}).
     * Los valores posibles se definen en {@link Metodo}.
     * Columna: {@code metodo}.
     */
    @jakarta.validation.constraints.NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", length = 20)
    private Metodo metodo;

    /**
     * Estado actual del pago en el proceso de la transacción.
     * Almacenado como cadena de texto ({@code EnumType.STRING}).
     * El valor por defecto es {@link Estado#pendiente}.
     * Los valores posibles se definen en {@link Estado}.
     * Columna: {@code estado}.
     */
    @jakarta.validation.constraints.NotNull(message = "El estado del pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.pendiente;

    /**
     * Enumeración que representa los métodos de pago aceptados por la plataforma.
     * <ul>
     *   <li>{@code credito} – Pago con tarjeta de crédito.</li>
     *   <li>{@code debito} – Pago con tarjeta de débito.</li>
     *   <li>{@code paypal} – Pago a través de PayPal.</li>
     *   <li>{@code transferencia} – Pago por transferencia bancaria.</li>
     * </ul>
     */
    public enum Metodo { credito, debito, paypal, transferencia }

    /**
     * Enumeración que representa los posibles estados de un pago.
     * <ul>
     *   <li>{@code pendiente} – El pago fue registrado pero aún no procesado.</li>
     *   <li>{@code aceptado} – El pago fue procesado y aprobado exitosamente.</li>
     *   <li>{@code rechazado} – El pago fue procesado pero rechazado por la pasarela.</li>
     *   <li>{@code cancelado} – El pago fue cancelado antes de ser procesado.</li>
     *   <li>{@code reembolso} – El pago fue reembolsado total o parcialmente.</li>
     * </ul>
     */
    public enum Estado { pendiente, aceptado, rechazado, cancelado, reembolso }

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public PagoModel() { }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaPago} si no fue establecida previamente.
     */
    @PrePersist
    private void asignarFechaPago() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
    }

    /**
     * Retorna el identificador único del pago.
     * @return identificador numérico del pago
     */
    public Long getIdPago() { return idPago; }

    /**
     * Establece el identificador único del pago.
     * @param idPago identificador a asignar
     */
    public void setIdPago(Long idPago) { this.idPago = idPago; }

    /**
     * Retorna la reservación asociada a este pago.
     * @return reservación a la que corresponde el pago
     */
    public ReservacionModel getReservacion() { return reservacion; }

    /**
     * Establece la reservación asociada a este pago.
     * @param reservacion reservación a asignar
     */
    public void setReservacion(ReservacionModel reservacion) { this.reservacion = reservacion; }

    /**
     * Retorna el monto del pago.
     * @return monto de la transacción
     */
    public BigDecimal getMonto() { return monto; }

    /**
     * Establece el monto del pago.
     * @param monto monto a asignar
     */
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    /**
     * Retorna la fecha y hora en que se realizó el pago.
     * @return fecha y hora del pago
     */
    public LocalDateTime getFechaPago() { return fechaPago; }

    /**
     * Establece la fecha y hora del pago.
     * @param fechaPago fecha y hora a asignar
     */
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    /**
     * Retorna la referencia única de la transacción de pago.
     * @return referencia del pago
     */
    public String getReferenciaPago() { return referenciaPago; }

    /**
     * Establece la referencia única de la transacción de pago.
     * @param referenciaPago referencia a asignar
     */
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }

    /**
     * Retorna el método de pago utilizado.
     * @return método de pago ({@link Metodo})
     */
    public Metodo getMetodo() { return metodo; }

    /**
     * Establece el método de pago utilizado.
     * @param metodo método de pago a asignar
     */
    public void setMetodo(Metodo metodo) { this.metodo = metodo; }

    /**
     * Retorna el estado actual del pago.
     * @return estado del pago ({@link Estado})
     */
    public Estado getEstado() { return estado; }

    /**
     * Establece el estado del pago.
     * @param estado estado a asignar
     */
    public void setEstado(Estado estado) { this.estado = estado; }

    /**
     * Retorna los datos adicionales del pago.
     * @return datos de la transacción
     */
    public String getDatosPago() { return datosPago; }

    /**
     * Establece los datos adicionales del pago.
     * @param datosPago datos a asignar
     */
    public void setDatosPago(String datosPago) { this.datosPago = datosPago; }
}