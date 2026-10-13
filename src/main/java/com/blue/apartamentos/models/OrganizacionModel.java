package com.blue.apartamentos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Entidad JPA que representa el perfil de una organización o persona jurídica
 * asociada a un {@link UsuarioModel usuario}.
 *
 * <p>Una {@code OrganizacionModel} extiende la información básica de un usuario con datos
 * fiscales y comerciales de una empresa (razón social, nombre comercial, RFC, dirección fiscal
 * y fecha de constitución). Al igual que {@link PersonaModel}, comparte la misma clave
 * primaria con {@link UsuarioModel} mediante una relación {@code @OneToOne}.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Organizaciones}.</p>
 *
 * @see UsuarioModel
 * @see PersonaModel
 * @see com.blue.apartamentos.services.OrganizacionService
 */
@Entity
@Table(name = "Organizaciones")
public class OrganizacionModel {

    /**
     * Identificador único de la organización. Coincide con el {@code id_usuario} del
     * {@link UsuarioModel} asociado, estableciendo la relación uno a uno.
     * Columna: {@code id_usuario}.
     */
    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    /**
     * Referencia al usuario base asociado a esta organización.
     * Relación {@code @OneToOne} de solo lectura ({@code insertable = false, updatable = false}).
     * Se ignoran propiedades sensibles al serializar.
     * Columna de unión: {@code id_usuario}.
     */
    @OneToOne
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"unidades", "contrasenaHash"})
    private UsuarioModel usuario;

    /**
     * Razón social oficial de la organización (nombre legal completo).
     * Obligatoria, máximo 200 caracteres.
     * Columna: {@code razon_social}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La razón social es obligatoria")
    @jakarta.validation.constraints.Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    /**
     * Nombre comercial o marca bajo la que opera la organización.
     * Obligatorio, máximo 150 caracteres.
     * Columna: {@code nombre_comercial}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El nombre comercial es obligatorio")
    @jakarta.validation.constraints.Size(max = 150, message = "El nombre comercial no puede exceder 150 caracteres")
    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    /**
     * Registro Federal de Contribuyentes (RFC) de la organización.
     * Obligatorio, entre 12 y 13 caracteres según el formato fiscal mexicano.
     * Columna: {@code RFC}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El RFC es obligatorio")
    @jakarta.validation.constraints.Size(min = 12, max = 13, message = "El RFC debe tener entre 12 y 13 caracteres")
    @Column(name = "RFC", nullable = false, length = 13)
    private String rfc;

    /**
     * Dirección fiscal registrada de la organización.
     * Obligatoria, almacenada como texto largo ({@code text}).
     * Columna: {@code direccion_fiscal}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La dirección fiscal es obligatoria")
    @Column(name = "direccion_fiscal", nullable = false, columnDefinition = "text")
    private String direccionFiscal;

    /**
     * Fecha en que la organización fue constituida legalmente.
     * Obligatoria y no puede ser una fecha futura.
     * Columna: {@code fecha_constitucion}.
     */
    @jakarta.validation.constraints.NotNull(message = "La fecha de constitución es obligatoria")
    @jakarta.validation.constraints.PastOrPresent(message = "La fecha de constitución no puede ser futura")
    @Column(name = "fecha_constitucion", nullable = false)
    private LocalDate fechaConstitucion;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     */
    public OrganizacionModel() { }

    /**
     * Retorna el identificador único de la organización (comparte valor con {@code UsuarioModel.idUsuario}).
     * @return identificador numérico de la organización
     */
    public Long getIdUsuario() { return idUsuario; }

    /**
     * Establece el identificador único de la organización.
     * @param idUsuario identificador a asignar
     */
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    /**
     * Retorna el usuario base asociado a esta organización.
     * @return usuario asociado
     */
    public UsuarioModel getUsuario() { return usuario; }

    /**
     * Establece el usuario base asociado a esta organización.
     * @param usuario usuario a asignar
     */
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }

    /**
     * Retorna la razón social oficial de la organización.
     * @return razón social
     */
    public String getRazonSocial() { return razonSocial; }

    /**
     * Establece la razón social oficial de la organización.
     * @param razonSocial razón social a asignar
     */
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    /**
     * Retorna el nombre comercial de la organización.
     * @return nombre comercial
     */
    public String getNombreComercial() { return nombreComercial; }

    /**
     * Establece el nombre comercial de la organización.
     * @param nombreComercial nombre comercial a asignar
     */
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    /**
     * Retorna el RFC de la organización.
     * @return RFC
     */
    public String getRfc() { return rfc; }

    /**
     * Establece el RFC de la organización.
     * @param rfc RFC a asignar
     */
    public void setRfc(String rfc) { this.rfc = rfc; }

    /**
     * Retorna la dirección fiscal de la organización.
     * @return dirección fiscal
     */
    public String getDireccionFiscal() { return direccionFiscal; }

    /**
     * Establece la dirección fiscal de la organización.
     * @param direccionFiscal dirección fiscal a asignar
     */
    public void setDireccionFiscal(String direccionFiscal) { this.direccionFiscal = direccionFiscal; }

    /**
     * Retorna la fecha de constitución de la organización.
     * @return fecha de constitución
     */
    public LocalDate getFechaConstitucion() { return fechaConstitucion; }

    /**
     * Establece la fecha de constitución de la organización.
     * @param fechaConstitucion fecha de constitución a asignar
     */
    public void setFechaConstitucion(LocalDate fechaConstitucion) { this.fechaConstitucion = fechaConstitucion; }
}
