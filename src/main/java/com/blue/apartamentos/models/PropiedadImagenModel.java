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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "Unidad_Imagen")
public class PropiedadImagenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_unidad", nullable = false)
    @JsonIgnoreProperties({"disponibilidades"})
    private UnidadRentableModel unidad;

    @jakarta.validation.constraints.NotBlank(message = "La URL de la imagen es obligatoria")
    @jakarta.validation.constraints.Size(max = 2048, message = "La URL no puede exceder 2048 caracteres")
    @Column(name = "url_imagen", nullable = false, length = 2048)
    private String urlImagen;

    @jakarta.validation.constraints.NotNull(message = "El orden de la imagen es obligatorio")
    @jakarta.validation.constraints.Min(value = 1, message = "El orden debe ser mayor que cero")
    @Column(nullable = false)
    private Integer orden;

    @jakarta.validation.constraints.NotNull(message = "Debe indicar si es la imagen principal")
    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal;

    @Column(name = "fecha_subida", nullable = false)
    private java.time.LocalDateTime fechaSubida;

    public PropiedadImagenModel() {
    }

    @PrePersist
    private void asignarFechaSubida() {
        if (fechaSubida == null) {
            fechaSubida = java.time.LocalDateTime.now();
        }
    }

    public Long getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(Long idImagen) {
        this.idImagen = idImagen;
    }

    public UnidadRentableModel getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadRentableModel unidad) {
        this.unidad = unidad;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public java.time.LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(java.time.LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}