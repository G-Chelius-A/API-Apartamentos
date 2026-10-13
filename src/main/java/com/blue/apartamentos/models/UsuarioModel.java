package com.blue.apartamentos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un usuario del sistema de alquiler de apartamentos.
 *
 * <p>Un {@code UsuarioModel} es la cuenta base de autenticación y autorización dentro de la
 * plataforma. Puede estar asociado a un perfil de {@link PersonaModel} (persona física) o a
 * un perfil de {@link OrganizacionModel} (persona jurídica). También puede ser propietario de
 * una o más {@link UnidadRentableModel unidades rentables}.</p>
 *
 * <p>La tabla subyacente en la base de datos es {@code Usuarios}.</p>
 *
 * @see PersonaModel
 * @see OrganizacionModel
 * @see UnidadRentableModel
 */
@Entity
@Table(name = "Usuarios")
public class UsuarioModel {

    /**
     * Identificador único del usuario, generado automáticamente por la base de datos
     * mediante estrategia {@code IDENTITY} (auto-increment).
     * Columna: {@code id_usuario}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    /**
     * Nombre de usuario único para iniciar sesión en la plataforma.
     * No puede estar en blanco, debe ser único y no exceder 50 caracteres.
     * Columna: {@code username}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El nombre de usuario es obligatorio")
    @jakarta.validation.constraints.Size(max = 50, message = "El nombre de usuario no puede exceder 50 caracteres")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Dirección de correo electrónico del usuario.
     * Debe tener formato válido de e-mail, ser único en el sistema y no exceder 254 caracteres.
     * Columna: {@code email}.
     */
    @jakarta.validation.constraints.NotBlank(message = "El correo electrónico es obligatorio")
    @jakarta.validation.constraints.Email(message = "El formato del correo electrónico no es válido")
    @jakarta.validation.constraints.Size(max = 254, message = "El correo electrónico no puede exceder 254 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    /**
     * Número de teléfono de contacto del usuario (opcional).
     * Acepta formatos internacionales con prefijo {@code +}, dígitos, guiones y espacios,
     * con una longitud de 7 a 20 caracteres. También acepta cadena vacía.
     * Columna: {@code telefono}.
     */
    @jakarta.validation.constraints.Pattern(regexp = "^$|^[+0-9\\-\\s]{7,20}$", message = "El número de teléfono no tiene un formato válido")
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Estado actual de la cuenta del usuario.
     * Almacenado como cadena de texto ({@code EnumType.STRING}) en la base de datos.
     * Los valores posibles se definen en {@link EstadoUsuario}.
     * Columna: {@code estado}.
     */
    @jakarta.validation.constraints.NotNull(message = "El estado del usuario es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estado;

    /**
     * Hash de la contraseña del usuario almacenado de forma segura.
     * En producción debe contener el resultado de un algoritmo de hashing (p. ej. BCrypt).
     * No se serializa en las respuestas JSON cuando se excluye mediante {@code @JsonIgnoreProperties}.
     * Columna: {@code contrasena_hash}.
     */
    @jakarta.validation.constraints.NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    /**
     * Fecha y hora en que el usuario fue registrado por primera vez en el sistema.
     * Se asigna automáticamente en el evento {@link #asignarFechaRegistro()} {@code @PrePersist}
     * si no se proporciona un valor.
     * Columna: {@code fecha_registro}.
     */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Fecha y hora del último inicio de sesión del usuario.
     * Se inicializa con la fecha actual en {@code @PrePersist} si no se proporcionó.
     * Columna: {@code ultimo_login}.
     */
    @Column(name = "ultimo_login", nullable = false)
    private LocalDateTime ultimoLogin;

    /**
     * Lista de unidades rentables cuyo propietario es este usuario.
     * Relación {@code @OneToMany} mapeada por el atributo {@code propietario}
     * en {@link UnidadRentableModel}.
     */
    @OneToMany(mappedBy = "propietario")
    private List<UnidadRentableModel> unidades = new ArrayList<>();

    /**
     * Enumeración que representa los posibles estados de una cuenta de usuario.
     * <ul>
     *   <li>{@code activo} – La cuenta está habilitada y el usuario puede operar normalmente.</li>
     *   <li>{@code inactivo} – La cuenta fue desactivada voluntariamente o por inactividad.</li>
     *   <li>{@code suspendido} – La cuenta fue suspendida por incumplimiento de políticas.</li>
     * </ul>
     */
    public enum EstadoUsuario { activo, inactivo, suspendido }

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate para la instanciación de entidades.
     */
    public UsuarioModel() { }

    /**
     * Método de ciclo de vida JPA que se ejecuta antes de que la entidad sea persistida
     * por primera vez ({@code @PrePersist}).
     * Asigna la fecha y hora actuales a {@code fechaRegistro} y {@code ultimoLogin}
     * si estos campos no han sido establecidos previamente.
     */
    @PrePersist
    private void asignarFechaRegistro() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }

        if(ultimoLogin==null){
            ultimoLogin = LocalDateTime.now();
        }
    }

    /**
     * Retorna el identificador único del usuario.
     * @return identificador numérico del usuario
     */
    public Long getIdUsuario() { return idUsuario; }

    /**
     * Establece el identificador único del usuario.
     * @param idUsuario identificador a asignar
     */
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    /**
     * Retorna el nombre de usuario (login).
     * @return nombre de usuario
     */
    public String getUsername() { return username; }

    /**
     * Establece el nombre de usuario (login).
     * @param username nombre de usuario a asignar
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Retorna la dirección de correo electrónico del usuario.
     * @return correo electrónico
     */
    public String getEmail() { return email; }

    /**
     * Establece la dirección de correo electrónico del usuario.
     * @param email correo electrónico a asignar
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Retorna el número de teléfono del usuario.
     * @return número de teléfono o {@code null} si no fue proporcionado
     */
    public String getTelefono() { return telefono; }

    /**
     * Establece el número de teléfono del usuario.
     * @param telefono número de teléfono a asignar
     */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Retorna el estado actual de la cuenta del usuario.
     * @return estado de la cuenta ({@link EstadoUsuario})
     */
    public EstadoUsuario getEstado() { return estado; }

    /**
     * Establece el estado de la cuenta del usuario.
     * @param estado nuevo estado a asignar
     */
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    /**
     * Retorna el hash de la contraseña almacenado.
     * @return hash de la contraseña
     */
    public String getContrasenaHash() { return contrasenaHash; }

    /**
     * Establece el hash de la contraseña del usuario.
     * @param contrasenaHash hash de la contraseña a asignar
     */
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    /**
     * Retorna la fecha y hora de registro del usuario.
     * @return fecha de registro
     */
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    /**
     * Establece la fecha y hora de registro del usuario.
     * @param fechaRegistro fecha de registro a asignar
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    /**
     * Retorna la fecha y hora del último inicio de sesión.
     * @return fecha del último login
     */
    public LocalDateTime getUltimoLogin() { return ultimoLogin; }

    /**
     * Establece la fecha y hora del último inicio de sesión.
     * @param ultimoLogin fecha de último login a asignar
     */
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }

    /**
     * Retorna la lista de unidades rentables asociadas al usuario como propietario.
     * @return lista de {@link UnidadRentableModel}
     */
    public List<UnidadRentableModel> getUnidades() { return unidades; }

    /**
     * Establece la lista de unidades rentables del usuario.
     * @param unidades lista de unidades a asignar
     */
    public void setUnidades(List<UnidadRentableModel> unidades) { this.unidades = unidades; }
}
