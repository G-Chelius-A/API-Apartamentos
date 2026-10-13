package com.blue.apartamentos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Entidad JPA que representa los datos extendidos de una casa dentro del sistema de alquiler.
 *
 * <p>Una {@code CasaModel} complementa a la {@link UnidadRentableModel} cuando la unidad
 * rentable es de tipo casa. Comparte el mismo identificador que la unidad mediante la
 * anotación {@code @MapsId}, estableciendo una relación de herencia compartida de clave primaria
 * ({@code @OneToOne}).</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Casas}.</p>
 *
 * @see UnidadRentableModel
 * @see HabitacionModel
 */
@Entity
@Table(name = "Casas")
public class CasaModel {

    /**
     * Identificador único de la casa. Coincide con el identificador de la
     * {@link UnidadRentableModel} a la que pertenece gracias a la anotación {@code @MapsId}.
     * Columna: {@code id_casa}.
     */
    @Id @Column(name = "id_casa") private Long idCasa;

    /**
     * Referencia a la unidad rentable base de esta casa.
     * Relación {@code @OneToOne} con clave compartida ({@code @MapsId}).
     * La columna de unión es {@code id_casa}.
     */
    @OneToOne @MapsId @JoinColumn(name = "id_casa") private UnidadRentableModel unidad;

    /**
     * Número total de habitaciones de la casa.
     * Obligatorio y debe ser al menos 1.
     * Columna: {@code numero_habitaciones}.
     */
    @jakarta.validation.constraints.NotNull(message = "El número de habitaciones es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "La casa debe tener al menos 1 habitación")
    @Column(name = "numero_habitaciones", nullable = false)
    private Integer numeroHabitaciones;

    /**
     * Número de pisos o niveles de la casa.
     * Obligatorio y debe ser al menos 1.
     * Columna: {@code numero_pisos}.
     */
    @jakarta.validation.constraints.NotNull(message = "El número de pisos es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "La casa debe tener al menos 1 piso")
    @Column(name = "numero_pisos", nullable = false)
    private Integer numeroPisos;

    /**
     * Número de baños disponibles en la casa.
     * Obligatorio y debe ser al menos 1.
     * Columna: {@code numero_banos}.
     */
    @jakarta.validation.constraints.NotNull(message = "El número de baños es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "La casa debe tener al menos 1 baño")
    @Column(name = "numero_banos", nullable = false)
    private Integer numeroBanos;

    /**
     * Superficie total de la casa expresada en metros cuadrados.
     * Obligatoria y debe ser mayor que cero. Precisión: 12 dígitos, 2 decimales.
     * Columna: {@code metros_cuadrados}.
     */
    @jakarta.validation.constraints.NotNull(message = "Los metros cuadrados son obligatorios")
    @jakarta.validation.constraints.DecimalMin(value = "1.00", message = "Los metros cuadrados deben ser mayores que cero")
    @Column(name = "metros_cuadrados", nullable = false, precision = 12, scale = 2)
    private BigDecimal metrosCuadrados;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public CasaModel() { }

    /**
     * Retorna el identificador único de la casa.
     * @return identificador numérico de la casa
     */
    public Long getIdCasa() { return idCasa; }

    /**
     * Establece el identificador único de la casa.
     * @param idCasa identificador a asignar
     */
    public void setIdCasa(Long idCasa) { this.idCasa = idCasa; }

    /**
     * Retorna la unidad rentable base asociada a esta casa.
     * @return unidad rentable
     */
    public UnidadRentableModel getUnidad() { return unidad; }

    /**
     * Establece la unidad rentable base asociada a esta casa.
     * @param unidad unidad rentable a asignar
     */
    public void setUnidad(UnidadRentableModel unidad) { this.unidad = unidad; }

    /**
     * Retorna el número de habitaciones de la casa.
     * @return número de habitaciones
     */
    public Integer getNumeroHabitaciones() { return numeroHabitaciones; }

    /**
     * Establece el número de habitaciones de la casa.
     * @param value número de habitaciones a asignar
     */
    public void setNumeroHabitaciones(Integer value) { numeroHabitaciones = value; }

    /**
     * Retorna el número de pisos de la casa.
     * @return número de pisos
     */
    public Integer getNumeroPisos() { return numeroPisos; }

    /**
     * Establece el número de pisos de la casa.
     * @param value número de pisos a asignar
     */
    public void setNumeroPisos(Integer value) { numeroPisos = value; }

    /**
     * Retorna el número de baños de la casa.
     * @return número de baños
     */
    public Integer getNumeroBanos() { return numeroBanos; }

    /**
     * Establece el número de baños de la casa.
     * @param value número de baños a asignar
     */
    public void setNumeroBanos(Integer value) { numeroBanos = value; }

    /**
     * Retorna la superficie total de la casa en metros cuadrados.
     * @return metros cuadrados
     */
    public BigDecimal getMetrosCuadrados() { return metrosCuadrados; }

    /**
     * Establece la superficie total de la casa en metros cuadrados.
     * @param value metros cuadrados a asignar
     */
    public void setMetrosCuadrados(BigDecimal value) { metrosCuadrados = value; }
}
