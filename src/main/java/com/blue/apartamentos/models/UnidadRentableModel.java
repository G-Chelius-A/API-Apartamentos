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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una unidad rentable dentro del sistema de alquiler.
 *
 * <p>Una {@code UnidadRentableModel} es la entidad central del dominio: puede ser un
 * apartamento, una casa o cualquier propiedad ofrecida en alquiler. Contiene información
 * de ubicación, capacidad, precio, comodidades y normas internas. Se relaciona con:
 * <ul>
 *   <li>Un {@link UsuarioModel propietario} que la administra.</li>
 *   <li>Múltiples {@link DisponibilidadModel periodos de disponibilidad}.</li>
 *   <li>Opcionalmente, un perfil extendido de {@link CasaModel} si se trata de una casa.</li>
 * </ul>
 *
 * <p>La tabla subyacente en la base de datos es {@code Unidad_Rentable}.</p>
 *
 * @see UsuarioModel
 * @see DisponibilidadModel
 * @see CasaModel
 */
@Entity
@Table(name = "Unidad_Rentable")
public class UnidadRentableModel {

    /**
     * Identificador único de la unidad rentable, generado automáticamente
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_unidad}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Long idUnidad;

    /**
     * Usuario propietario de la unidad rentable.
     * Relación {@code @ManyToOne} de carga diferida ({@code LAZY}). El propietario es obligatorio.
     * Se ignoran las propiedades {@code unidades} al serializar para evitar recursividad.
     * Columna de unión: {@code id_propietario}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_propietario", nullable = false)
    @JsonIgnoreProperties({"unidades"})
    private UsuarioModel propietario;

    /**
     * Título descriptivo de la unidad rentable (p. ej. "Apartamento con vista al mar").
     * Obligatorio, con un máximo de 150 caracteres.
     * Columna: {@code titulo}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El título de la unidad es obligatorio")
    @jakarta.validation.constraints.Size(max = 150, message = "El título no puede exceder 150 caracteres")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    /**
     * Descripción detallada de la unidad rentable, incluyendo características, servicios
     * y todo aquello que el arrendatario deba conocer.
     * Obligatoria, almacenada como texto largo ({@code text}).
     * Columna: {@code descripcion}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion", nullable = false, columnDefinition = "text")
    private String descripcion;

    /**
     * Dirección física de la unidad rentable (calle, número, colonia, etc.).
     * Obligatoria, con un máximo de 255 caracteres.
     * Columna: {@code direccion}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La dirección es obligatoria")
    @jakarta.validation.constraints.Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    /**
     * Ciudad donde se encuentra la unidad rentable.
     * Obligatoria, con un máximo de 100 caracteres.
     * Columna: {@code ciudad}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La ciudad es obligatoria")
    @jakarta.validation.constraints.Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    /**
     * Estado, provincia o entidad federativa donde se ubica la unidad rentable.
     * Obligatorio, con un máximo de 100 caracteres.
     * Columna: {@code entidad}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El estado o entidad es obligatorio")
    @jakarta.validation.constraints.Size(max = 100, message = "La entidad no puede exceder 100 caracteres")
    @Column(name = "entidad", nullable = false, length = 100)
    private String entidad;

    /**
     * Código postal de la ubicación de la unidad rentable.
     * Obligatorio, con un máximo de 10 caracteres.
     * Columna: {@code codigo_postal}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El código postal es obligatorio")
    @jakarta.validation.constraints.Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
    @Column(name = "codigo_postal", nullable = false, length = 10)
    private String codigoPostal;

    /**
     * País donde se encuentra la unidad rentable.
     * Obligatorio, con un máximo de 100 caracteres.
     * Columna: {@code pais}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El país es obligatorio")
    @jakarta.validation.constraints.Size(max = 100, message = "El país no puede exceder 100 caracteres")
    @Column(name = "pais", nullable = false, length = 100)
    private String pais;

    /**
     * Capacidad máxima de personas que pueden hospedarse en la unidad rentable.
     * Obligatoria y debe ser al menos 1.
     * Columna: {@code capacidad_personas}.
     */
    @jakarta.validation.constraints.NotNull(message = "La capacidad de personas es obligatoria")
    @jakarta.validation.constraints.Min(value = 1, message = "La capacidad debe ser de al menos 1 persona")
    @Column(name = "capacidad_personas", nullable = false)
    private Integer capacidadPersonas;

    /**
     * Lista de comodidades ofrecidas en la unidad (p. ej. "WiFi, cocina equipada, piscina").
     * Obligatoria, almacenada como texto largo ({@code text}).
     * Columna: {@code comodidades}.
     */
    @jakarta.validation.constraints.NotBlank(message = "Las comodidades son obligatorias")
    @Column(nullable = false, columnDefinition = "text")
    private String comodidades;

    /**
     * Normas de convivencia e internas que deben respetar los huéspedes durante su estancia.
     * Obligatorias, almacenadas como texto largo ({@code text}).
     * Columna: {@code normas_unidad}.
     */
    @jakarta.validation.constraints.NotBlank(message = "Las normas de la unidad son obligatorias")
    @Column(name = "normas_unidad", nullable = false, columnDefinition = "text")
    private String normasUnidad;

    /**
     * Fecha y hora en que la unidad rentable fue registrada en el sistema por primera vez.
     * Se asigna automáticamente en {@link #asignarFechasCreacion()} si no se proporciona.
     * Columna: {@code fecha_creacion}.
     */
    @Column(name = "fecha_creacion", nullable = false)
    private java.time.LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación de la unidad rentable.
     * Se actualiza automáticamente en {@link #actualizarFechaModificacion()} con cada cambio.
     * Columna: {@code fecha_actualizacion}.
     */
    @Column(name = "fecha_actualizacion", nullable = false)
    private java.time.LocalDateTime fechaActualizacion;

    /**
     * Indica si la unidad admite mascotas ({@code true}) o no ({@code false}).
     * Obligatorio.
     * Columna: {@code pet_friendly}.
     */
    @jakarta.validation.constraints.NotNull(message = "Debe indicar si la unidad es pet friendly")
    @Column(name = "pet_friendly", nullable = false)
    private Boolean petFriendly;

    /**
     * Indica si la unidad cuenta con características de accesibilidad para personas
     * con movilidad reducida u otras necesidades especiales.
     * Obligatorio.
     * Columna: {@code accesibilidad}.
     */
    @jakarta.validation.constraints.NotNull(message = "Debe indicar si la unidad cuenta con accesibilidad")
    @Column(nullable = false)
    private Boolean accesibilidad;

    /**
     * Precio base por noche de la unidad rentable en la moneda configurada.
     * Obligatorio y debe ser mayor que cero. Precisión: 12 dígitos, 2 decimales.
     * Los periodos de disponibilidad pueden tener un precio especial que sobreescribe este valor.
     * Columna: {@code precio_noche}.
     */
    @jakarta.validation.constraints.NotNull(message = "El precio por noche es obligatorio")
    @jakarta.validation.constraints.DecimalMin(value = "0.01", message = "El precio por noche debe ser mayor que cero")
    @Column(name = "precio_noche", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioNoche;

    /**
     * Lista de periodos de disponibilidad asociados a esta unidad rentable.
     * Relación {@code @OneToMany} mapeada por el atributo {@code unidad} en {@link DisponibilidadModel}.
     */
    @OneToMany(mappedBy = "unidad") private List<DisponibilidadModel> disponibilidades = new ArrayList<>();

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public UnidadRentableModel() { }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaCreacion} y {@code fechaActualizacion}
     * si no fueron establecidas previamente.
     */
    @PrePersist
    private void asignarFechasCreacion() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }
        if (fechaActualizacion == null) {
            fechaActualizacion = ahora;
        }
    }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de actualizar la entidad en la base de datos
     * ({@code @PreUpdate}). Actualiza {@code fechaActualizacion} con la fecha y hora actuales.
     */
    @PreUpdate
    private void actualizarFechaModificacion() {
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Retorna el identificador único de la unidad rentable.
     * @return identificador numérico de la unidad
     */
    public Long getIdUnidad() { return idUnidad; }

    /**
     * Establece el identificador único de la unidad rentable.
     * @param idUnidad identificador a asignar
     */
    public void setIdUnidad(Long idUnidad) { this.idUnidad = idUnidad; }

    /**
     * Retorna el usuario propietario de la unidad rentable.
     * @return propietario de la unidad
     */
    public UsuarioModel getPropietario() { return propietario; }

    /**
     * Establece el usuario propietario de la unidad rentable.
     * @param propietario usuario propietario a asignar
     */
    public void setPropietario(UsuarioModel propietario) { this.propietario = propietario; }

    /**
     * Retorna el título de la unidad rentable.
     * @return título descriptivo
     */
    public String getTitulo() { return titulo; }

    /**
     * Establece el título de la unidad rentable.
     * @param titulo título a asignar
     */
    public void setTitulo(String titulo) { this.titulo = titulo; }

    /**
     * Retorna la descripción detallada de la unidad rentable.
     * @return descripción
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Establece la descripción de la unidad rentable.
     * @param descripcion descripción a asignar
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Retorna la dirección física de la unidad rentable.
     * @return dirección
     */
    public String getDireccion() { return direccion; }

    /**
     * Establece la dirección física de la unidad rentable.
     * @param direccion dirección a asignar
     */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Retorna la ciudad donde se ubica la unidad rentable.
     * @return ciudad
     */
    public String getCiudad() { return ciudad; }

    /**
     * Establece la ciudad donde se ubica la unidad rentable.
     * @param ciudad ciudad a asignar
     */
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    /**
     * Retorna el estado o entidad federativa de la unidad rentable.
     * @return entidad
     */
    public String getEntidad() { return entidad; }

    /**
     * Establece el estado o entidad federativa de la unidad rentable.
     * @param entidad entidad a asignar
     */
    public void setEntidad(String entidad) { this.entidad = entidad; }

    /**
     * Retorna el código postal de la unidad rentable.
     * @return código postal
     */
    public String getCodigoPostal() { return codigoPostal; }

    /**
     * Establece el código postal de la unidad rentable.
     * @param codigoPostal código postal a asignar
     */
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    /**
     * Retorna el país donde se ubica la unidad rentable.
     * @return país
     */
    public String getPais() { return pais; }

    /**
     * Establece el país donde se ubica la unidad rentable.
     * @param pais país a asignar
     */
    public void setPais(String pais) { this.pais = pais; }

    /**
     * Retorna la capacidad máxima de personas de la unidad rentable.
     * @return capacidad máxima de personas
     */
    public Integer getCapacidadPersonas() { return capacidadPersonas; }

    /**
     * Establece la capacidad máxima de personas de la unidad rentable.
     * @param capacidadPersonas capacidad a asignar
     */
    public void setCapacidadPersonas(Integer capacidadPersonas) { this.capacidadPersonas = capacidadPersonas; }

    /**
     * Retorna la lista de comodidades ofrecidas en la unidad rentable.
     * @return texto con las comodidades
     */
    public String getComodidades() { return comodidades; }

    /**
     * Establece las comodidades de la unidad rentable.
     * @param comodidades comodidades a asignar
     */
    public void setComodidades(String comodidades) { this.comodidades = comodidades; }

    /**
     * Retorna las normas internas de la unidad rentable.
     * @return normas de la unidad
     */
    public String getNormasUnidad() { return normasUnidad; }

    /**
     * Establece las normas internas de la unidad rentable.
     * @param normasUnidad normas a asignar
     */
    public void setNormasUnidad(String normasUnidad) { this.normasUnidad = normasUnidad; }

    /**
     * Retorna la fecha y hora de creación del registro de la unidad rentable.
     * @return fecha de creación
     */
    public java.time.LocalDateTime getFechaCreacion() { return fechaCreacion; }

    /**
     * Establece la fecha y hora de creación del registro.
     * @param fechaCreacion fecha de creación a asignar
     */
    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    /**
     * Retorna la fecha y hora de la última actualización del registro.
     * @return fecha de última actualización
     */
    public java.time.LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    /**
     * Establece la fecha y hora de la última actualización del registro.
     * @param fechaActualizacion fecha de actualización a asignar
     */
    public void setFechaActualizacion(java.time.LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    /**
     * Retorna si la unidad rentable admite mascotas.
     * @return {@code true} si es pet-friendly, {@code false} en caso contrario
     */
    public Boolean getPetFriendly() { return petFriendly; }

    /**
     * Establece si la unidad admite mascotas.
     * @param petFriendly valor a asignar
     */
    public void setPetFriendly(Boolean petFriendly) { this.petFriendly = petFriendly; }

    /**
     * Retorna si la unidad cuenta con características de accesibilidad.
     * @return {@code true} si tiene accesibilidad, {@code false} en caso contrario
     */
    public Boolean getAccesibilidad() { return accesibilidad; }

    /**
     * Establece si la unidad cuenta con características de accesibilidad.
     * @param accesibilidad valor a asignar
     */
    public void setAccesibilidad(Boolean accesibilidad) { this.accesibilidad = accesibilidad; }

    /**
     * Retorna el precio base por noche de la unidad rentable.
     * @return precio por noche
     */
    public BigDecimal getPrecioNoche() { return precioNoche; }

    /**
     * Establece el precio base por noche de la unidad rentable.
     * @param precioNoche precio por noche a asignar
     */
    public void setPrecioNoche(BigDecimal precioNoche) { this.precioNoche = precioNoche; }

    /**
     * Retorna la lista de periodos de disponibilidad de la unidad rentable.
     * @return lista de {@link DisponibilidadModel}
     */
    public List<DisponibilidadModel> getDisponibilidades() { return disponibilidades; }

    /**
     * Establece la lista de periodos de disponibilidad de la unidad rentable.
     * @param disponibilidades lista de disponibilidades a asignar
     */
    public void setDisponibilidades(List<DisponibilidadModel> disponibilidades) { this.disponibilidades = disponibilidades; }
}