package com.kokodev.contactame.Objetos;

/**
 * Created by koko on 13-05-17.
 */

public class Tarjeta {

    private String cargo,pagina,descripcion,direccion,localidad,organizacion,telefono,imagenTarjeta;
    private Boolean publico;

    public Tarjeta() {
    }

    public Tarjeta(String cargo, String pagina, String descripcion, String direccion, String localidad, String organizacion, String telefono, String imagenTarjeta, Boolean publico) {
        this.cargo = cargo;
        this.pagina = pagina;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.localidad = localidad;
        this.organizacion = organizacion;
        this.telefono = telefono;
        this.imagenTarjeta = imagenTarjeta;
        this.publico = publico;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(String organizacion) {
        this.organizacion = organizacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagenTarjeta() {
        return imagenTarjeta;
    }

    public void setImagenTarjeta(String imagenTarjeta) {
        this.imagenTarjeta = imagenTarjeta;
    }

    public Boolean getPublico() {
        return publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }
}
