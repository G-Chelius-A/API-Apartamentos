package com.blue.apartamentos.models;

import java.time.LocalDate;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entidad JPA que representa un periodo de disponibilidad de una {@link UnidadRentableModel unidad rentable}.
 *
 * <p>Una {@code DisponibilidadModel} define un intervalo de fechas en el que una unidad rentable
 * puede estar disponible o no disponible para ser reservada. Opcionalmente, puede especificarse
 * un precio especial que sobreescribe el precio base de la unidad durante ese periodo.</p>
 *
 * <p>El servicio {@link com.blue.apartamentos.services.ReservacionService} consulta estas
 * disponibilidades para validar que una reservación pueda realizarse en las fechas solicitadas.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code disponibilidades}.</p>
 *
 * @see UnidadRentableModel
 * @see com.blue.apartamentos.services.DisponibilidadService
 */
@Entity
@Table(name = "disponibilidades")
public class DisponibilidadModel {

    /**
     * Identificador único del periodo de disponibilidad, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_disponibilidad}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Long idDisponibilidad;

    /**
     * Unidad rentable a la que pertenece este periodo de disponibilidad.
     * Relación {@code @ManyToOne} de carga diferida ({@code LAZY}), obligatoria.
     * Se ignoran las propiedades {@code disponibilidades} al serializar para evitar recursividad.
     * Columna de unión: {@code id_unidad}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidad", nullable = false)
    @JsonIgnoreProperties("disponibilidades")
    private UnidadRentableModel unidad;

    /**
     * Fecha de inicio del periodo de disponibilidad (inclusive).
     * Obligatoria.
     * Columna: {@code fecha_inicio}.
     */
    @jakarta.validation.constraints.NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha de fin del periodo de disponibilidad (inclusive).
     * Obligatoria.
     * Columna: {@code fecha_fin}.
     */
    @jakarta.validation.constraints.NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    /**
     * Indica si la unidad está disponible ({@code true}) o bloqueada ({@code false})
     * durante este periodo. El valor por defecto es {@code true}.
     * Obligatorio.
     * Columna: {@code disponible}.
     */
    @jakarta.validation.constraints.NotNull(message = "Debe especificar si está disponible")
    @Column(nullable = false)
    private Boolean disponible = true;

    /**
     * Precio especial por noche para este periodo de disponibilidad (opcional).
     * Si se proporciona, sobreescribe el {@link UnidadRentableModel#getPrecioNoche() precio base}
     * de la unidad al calcular el costo de una reservación.
     * Debe ser mayor que cero. Precisión: 12 dígitos, 2 decimales.
     * Columna: {@code precio_especial}.
     */
    @jakarta.validation.constraints.DecimalMin(value = "0.01", message = "El precio especial debe ser mayor que cero")
    @Column(name = "precio_especial", precision = 12, scale = 2)
    private BigDecimal precioEspecial;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public DisponibilidadModel() {
    }

    /**
     * Retorna el identificador único del periodo de disponibilidad.
     * @return identificador numérico de la disponibilidad
     */
    public Long getIdDisponibilidad() {
        return idDisponibilidad;
    }

    /**
     * Establece el identificador único del periodo de disponibilidad.
     * @param idDisponibilidad identificador a asignar
     */
    public void setIdDisponibilidad(Long idDisponibilidad) {
        this.idDisponibilidad = idDisponibilidad;
    }

    /**
     * Retorna la unidad rentable asociada a este periodo de disponibilidad.
     * @return unidad rentable
     */
    public UnidadRentableModel getUnidad() {
        return unidad;
    }

    /**
     * Establece la unidad rentable asociada a este periodo de disponibilidad.
     * @param unidad unidad rentable a asignar
     */
    public void setUnidad(UnidadRentableModel unidad) {
        this.unidad = unidad;
    }

    /**
     * Retorna la fecha de inicio del periodo de disponibilidad.
     * @return fecha de inicio
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio del periodo de disponibilidad.
     * @param fechaInicio fecha de inicio a asignar
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Retorna la fecha de fin del periodo de disponibilidad.
     * @return fecha de fin
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha de fin del periodo de disponibilidad.
     * @param fechaFin fecha de fin a asignar
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Retorna si la unidad está disponible en este periodo.
     * @return {@code true} si está disponible, {@code false} si está bloqueada
     */
    public Boolean getDisponible() {
        return disponible;
    }

    /**
     * Establece si la unidad está disponible en este periodo.
     * @param disponible valor a asignar
     */
    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Retorna el precio especial por noche para este periodo, si existe.
     * @return precio especial o {@code null} si no aplica precio especial
     */
    public BigDecimal getPrecioEspecial() {
        return precioEspecial;
    }

    /**
     * Establece el precio especial por noche para este periodo de disponibilidad.
     * @param precioEspecial precio especial a asignar, puede ser {@code null}
     */
    public void setPrecioEspecial(BigDecimal precioEspecial) {
        this.precioEspecial = precioEspecial;
    }
}
