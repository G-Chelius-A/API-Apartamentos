package com.blue.apartamentos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entidad JPA que representa un reembolso asociado a un {@link PagoModel pago}.
 *
 * <p>Un {@code ReembolsoModel} se genera cuando se requiere devolver parcial o totalmente
 * el monto de un pago previamente realizado. El estado del reembolso sigue su propio ciclo
 * de vida hasta ser procesado o rechazado.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Reembolsos}.</p>
 *
 * @see PagoModel
 * @see com.blue.apartamentos.services.ReembolsoService
 */
@Entity
@Table(name = "Reembolsos")
public class ReembolsoModel {

    /**
     * Identificador único del reembolso, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_reembolso}.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_reembolso") private Long idReembolso;

    /**
     * Pago al que corresponde este reembolso.
     * Relación {@code @ManyToOne}, obligatoria.
     * Columna de unión: {@code id_pago}.
     */
    @ManyToOne(optional = false) @JoinColumn(name = "id_pago", nullable = false) private PagoModel pago;

    /**
     * Fecha y hora en que se registró o procesó el reembolso.
     * Se asigna automáticamente en {@link #asignarFechaReembolso()} si no se proporciona.
     * Columna: {@code fecha_reembolso}.
     */
    @Column(name = "fecha_reembolso") private LocalDateTime fechaReembolso;

    /**
     * Monto a reembolsar al cliente.
     * Obligatorio, debe ser mayor que cero y no puede exceder el monto del pago original.
     * Precisión: 12 dígitos, 2 decimales.
     * Columna: {@code monto_reembolso}.
     */
    @jakarta.validation.constraints.NotNull(message = "El monto del reembolso es obligatorio")
    @jakarta.validation.constraints.DecimalMin(value = "0.01", message = "El monto del reembolso debe ser mayor que cero")
    @Column(name = "monto_reembolso", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoReembolso;

    /**
     * Estado actual del proceso de reembolso.
     * Almacenado como cadena de texto ({@code EnumType.STRING}).
     * El valor por defecto es {@link Estado#pendiente}.
     * Los valores posibles se definen en {@link Estado}.
     * Columna: {@code estado}.
     */
    @jakarta.validation.constraints.NotNull(message = "El estado del reembolso es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.pendiente;

    /**
     * Enumeración que representa los posibles estados de un reembolso.
     * <ul>
     *   <li>{@code pendiente} – El reembolso fue solicitado y está en espera de revisión.</li>
     *   <li>{@code aprobado} – El reembolso fue aprobado y está pendiente de procesamiento.</li>
     *   <li>{@code rechazado} – El reembolso fue rechazado por el administrador.</li>
     *   <li>{@code procesado} – El reembolso fue ejecutado y el dinero devuelto al cliente.</li>
     * </ul>
     */
    public enum Estado { pendiente, aprobado, rechazado, procesado }

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public ReembolsoModel() { }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaReembolso} si no fue establecida.
     */
    @PrePersist
    private void asignarFechaReembolso() {
        if (fechaReembolso == null) {
            fechaReembolso = LocalDateTime.now();
        }
    }

    /**
     * Retorna el identificador único del reembolso.
     * @return identificador numérico del reembolso
     */
    public Long getIdReembolso() { return idReembolso; }

    /**
     * Establece el identificador único del reembolso.
     * @param value identificador a asignar
     */
    public void setIdReembolso(Long value) { idReembolso = value; }

    /**
     * Retorna el pago asociado a este reembolso.
     * @return pago original
     */
    public PagoModel getPago() { return pago; }

    /**
     * Establece el pago asociado a este reembolso.
     * @param value pago a asignar
     */
    public void setPago(PagoModel value) { pago = value; }

    /**
     * Retorna la fecha y hora del reembolso.
     * @return fecha del reembolso
     */
    public LocalDateTime getFechaReembolso() { return fechaReembolso; }

    /**
     * Establece la fecha y hora del reembolso.
     * @param value fecha del reembolso a asignar
     */
    public void setFechaReembolso(LocalDateTime value) { fechaReembolso = value; }

    /**
     * Retorna el monto a reembolsar.
     * @return monto del reembolso
     */
    public BigDecimal getMontoReembolso() { return montoReembolso; }

    /**
     * Establece el monto a reembolsar.
     * @param value monto del reembolso a asignar
     */
    public void setMontoReembolso(BigDecimal value) { montoReembolso = value; }

    /**
     * Retorna el estado actual del reembolso.
     * @return estado del reembolso ({@link Estado})
     */
    public Estado getEstado() { return estado; }

    /**
     * Establece el estado del reembolso.
     * @param value estado a asignar
     */
    public void setEstado(Estado value) { estado = value; }
}
