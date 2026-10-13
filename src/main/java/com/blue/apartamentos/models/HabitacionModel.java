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
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Entidad JPA que representa una habitación individual dentro de una {@link CasaModel casa}.
 *
 * <p>Una {@code HabitacionModel} es una subdivisión de una casa y contiene información
 * específica sobre su número, piso, superficie y tipo. Permite llevar un catálogo detallado
 * de cada habitación disponible dentro de una propiedad de tipo casa.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Habitaciones}.</p>
 *
 * @see CasaModel
 */
@Entity
@Table(name = "Habitaciones")
public class HabitacionModel {

    /**
     * Identificador único de la habitación, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_habitacion}.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_habitacion") private Long idHabitacion;

    /**
     * Casa a la que pertenece esta habitación.
     * Relación {@code @ManyToOne}, obligatoria. Se ignoran las propiedades de
     * {@code unidad} al serializar para evitar referencias circulares.
     * Columna de unión: {@code id_casa}.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_casa", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"unidad"})
    private CasaModel casa;

    /**
     * Número de identificación de la habitación dentro de la casa (p. ej. 1, 2, 3…).
     * Obligatorio y debe ser mayor que cero.
     * Columna: {@code numero}.
     */
    @jakarta.validation.constraints.NotNull(message = "El número de habitación es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "El número de habitación debe ser mayor que cero")
    @Column(nullable = false)
    private Integer numero;

    /**
     * Número de piso en que se encuentra la habitación dentro de la casa.
     * Obligatorio y debe ser mayor que cero.
     * Columna: {@code piso}.
     */
    @jakarta.validation.constraints.NotNull(message = "El piso es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "El piso debe ser mayor que cero")
    @Column(nullable = false)
    private Integer piso;

    /**
     * Superficie de la habitación expresada en metros cuadrados.
     * Obligatoria y debe ser mayor que cero. Precisión: 12 dígitos, 2 decimales.
     * Columna: {@code metros_cuadrados}.
     */
    @jakarta.validation.constraints.NotNull(message = "Los metros cuadrados son obligatorios")
    @jakarta.validation.constraints.DecimalMin(value = "1.00", message = "Los metros cuadrados deben ser mayores que cero")
    @Column(name = "metros_cuadrados", nullable = false, precision = 12, scale = 2)
    private BigDecimal metrosCuadrados;

    /**
     * Tipo de habitación según su configuración y capacidad.
     * Almacenado como cadena de texto ({@code EnumType.STRING}).
     * Los valores posibles se definen en {@link TipoHabitacion}.
     * Columna: {@code tipo_habitacion}.
     */
    @jakarta.validation.constraints.NotNull(message = "El tipo de habitación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habitacion", nullable = false, length = 20)
    private TipoHabitacion tipoHabitacion;

    /**
     * Enumeración que clasifica el tipo de habitación según su configuración de camas.
     * <ul>
     *   <li>{@code matrimonial} – Habitación con cama de matrimonio o doble.</li>
     *   <li>{@code individual} – Habitación con una cama individual.</li>
     *   <li>{@code familiar} – Habitación con capacidad para familia (múltiples camas).</li>
     * </ul>
     */
    public enum TipoHabitacion { matrimonial, individual, familiar }

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public HabitacionModel() { }

    /**
     * Retorna el identificador único de la habitación.
     * @return identificador numérico de la habitación
     */
    public Long getIdHabitacion() { return idHabitacion; }

    /**
     * Establece el identificador único de la habitación.
     * @param value identificador a asignar
     */
    public void setIdHabitacion(Long value) { idHabitacion = value; }

    /**
     * Retorna la casa a la que pertenece esta habitación.
     * @return casa contenedora
     */
    public CasaModel getCasa() { return casa; }

    /**
     * Establece la casa a la que pertenece esta habitación.
     * @param value casa a asignar
     */
    public void setCasa(CasaModel value) { casa = value; }

    /**
     * Retorna el número de la habitación dentro de la casa.
     * @return número de habitación
     */
    public Integer getNumero() { return numero; }

    /**
     * Establece el número de la habitación dentro de la casa.
     * @param value número a asignar
     */
    public void setNumero(Integer value) { numero = value; }

    /**
     * Retorna el número de piso en que se encuentra la habitación.
     * @return número de piso
     */
    public Integer getPiso() { return piso; }

    /**
     * Establece el número de piso de la habitación.
     * @param value piso a asignar
     */
    public void setPiso(Integer value) { piso = value; }

    /**
     * Retorna la superficie de la habitación en metros cuadrados.
     * @return metros cuadrados
     */
    public BigDecimal getMetrosCuadrados() { return metrosCuadrados; }

    /**
     * Establece la superficie de la habitación en metros cuadrados.
     * @param value metros cuadrados a asignar
     */
    public void setMetrosCuadrados(BigDecimal value) { metrosCuadrados = value; }

    /**
     * Retorna el tipo de habitación.
     * @return tipo de habitación ({@link TipoHabitacion})
     */
    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }

    /**
     * Establece el tipo de habitación.
     * @param value tipo de habitación a asignar
     */
    public void setTipoHabitacion(TipoHabitacion value) { tipoHabitacion = value; }
}
