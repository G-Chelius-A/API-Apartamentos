package com.blue.apartamentos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;

import org.springframework.data.domain.Persistable;

/**
 * Entidad JPA que representa el perfil de persona física asociado a un {@link UsuarioModel usuario}.
 *
 * <p>Una {@code PersonaModel} extiende la información básica de un usuario con datos
 * personales como nombre, apellido, fecha de nacimiento, documento de identidad y dirección.
 * Implementa {@link Persistable} para controlar el comportamiento de guardado vs. actualización,
 * ya que comparte la misma clave primaria que {@link UsuarioModel} (relación uno a uno).</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Personas}.</p>
 *
 * @see UsuarioModel
 * @see OrganizacionModel
 * @see com.blue.apartamentos.services.PersonaService
 */
@Entity
@Table(name = "Personas")
public class PersonaModel implements Persistable<Long> {

    /**
     * Campo transitorio (no persistido en la base de datos) que indica si la entidad
     * es nueva y debe ser insertada ({@code true}) o actualizada ({@code false}).
     * Es requerido por la implementación de {@link Persistable}.
     */
    @org.springframework.data.annotation.Transient
    private boolean isNew = true;

    /**
     * Identificador único de la persona. Coincide con el {@code id_usuario} de la
     * {@link UsuarioModel} asociada, estableciendo la relación uno a uno.
     * Columna: {@code id_usuario}.
     */
    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    /**
     * Referencia al usuario base asociado a esta persona.
     * Relación {@code @OneToOne} de solo lectura ({@code insertable = false, updatable = false}).
     * Se ignoran propiedades sensibles al serializar.
     * Columna de unión: {@code id_usuario}.
     */
    @OneToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"unidades", "contrasenaHash"})
    private UsuarioModel usuario;

    /**
     * Nombre(s) de la persona.
     * Obligatorio, máximo 100 caracteres.
     * Columna: {@code nombre}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
    @jakarta.validation.constraints.Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Apellido(s) de la persona.
     * Obligatorio, máximo 100 caracteres.
     * Columna: {@code apellido}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El apellido es obligatorio")
    @jakarta.validation.constraints.Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;

    /**
     * Fecha de nacimiento de la persona.
     * Obligatoria y debe estar en el pasado.
     * Columna: {@code fecha_nacimiento}.
     */
    @jakarta.validation.constraints.NotNull(message = "La fecha de nacimiento es obligatoria")
    @jakarta.validation.constraints.Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Número o clave del documento de identidad oficial de la persona
     * (p. ej. INE, pasaporte, cédula).
     * Obligatorio, máximo 30 caracteres.
     * Columna: {@code documento_identidad}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El documento de identidad es obligatorio")
    @jakarta.validation.constraints.Size(max = 30, message = "El documento de identidad no puede exceder 30 caracteres")
    @Column(name = "documento_identidad", nullable = false, length = 30)
    private String documentoIdentidad;

    /**
     * Dirección de residencia de la persona.
     * Obligatoria, almacenada como texto largo ({@code text}).
     * Columna: {@code direccion}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false, columnDefinition = "text")
    private String direccion;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public PersonaModel() { }

    /**
     * Retorna el identificador único de la persona (comparte valor con {@code UsuarioModel.idUsuario}).
     * @return identificador numérico de la persona
     */
    public Long getIdUsuario() { return idUsuario; }

    /**
     * Establece el identificador único de la persona.
     * @param idUsuario identificador a asignar
     */
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    /**
     * Retorna el usuario base asociado a esta persona.
     * @return usuario asociado
     */
    public UsuarioModel getUsuario() { return usuario; }

    /**
     * Establece el usuario base asociado a esta persona.
     * @param usuario usuario a asignar
     */
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }

    /**
     * Retorna el nombre de la persona.
     * @return nombre
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre de la persona.
     * @param nombre nombre a asignar
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Retorna el apellido de la persona.
     * @return apellido
     */
    public String getApellido() { return apellido; }

    /**
     * Establece el apellido de la persona.
     * @param apellido apellido a asignar
     */
    public void setApellido(String apellido) { this.apellido = apellido; }

    /**
     * Retorna la fecha de nacimiento de la persona.
     * @return fecha de nacimiento
     */
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }

    /**
     * Establece la fecha de nacimiento de la persona.
     * @param fechaNacimiento fecha de nacimiento a asignar
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    /**
     * Retorna el número del documento de identidad de la persona.
     * @return número de documento de identidad
     */
    public String getDocumentoIdentidad() { return documentoIdentidad; }

    /**
     * Establece el número del documento de identidad de la persona.
     * @param documentoIdentidad documento de identidad a asignar
     */
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    /**
     * Retorna la dirección de residencia de la persona.
     * @return dirección
     */
    public String getDireccion() { return direccion; }

    /**
     * Establece la dirección de residencia de la persona.
     * @param direccion dirección a asignar
     */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Implementación de {@link Persistable#getId()} que retorna el identificador
     * de la persona para que Spring Data pueda determinar si debe insertar o actualizar.
     * @return identificador numérico de la persona
     */
    @Override
    public Long getId() {
        return idUsuario;
    }

    /**
     * Implementación de {@link Persistable#isNew()} que indica si la entidad es nueva.
     * Se establece en {@code false} después de persistir o cargar desde la base de datos
     * mediante {@link #markNotNew()}.
     * @return {@code true} si la entidad es nueva, {@code false} si ya existe en base de datos
     */
    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Método de ciclo de vida JPA que marca la entidad como no nueva ({@code isNew = false})
     * después de ser persistida ({@code @PrePersist}) o cargada desde la base de datos
     * ({@code @PostLoad}). Esto evita que Spring Data intente insertar un registro que ya existe.
     */
    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
