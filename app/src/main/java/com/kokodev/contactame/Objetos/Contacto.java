package com.kokodev.contactame.Objetos;

/**
 * Created by koko on 15-05-17.
 */

public class Contacto {
    String nombres,apellidos,correo_electronico,telefono,imagen_usuario,id_tarjeta;

    public Contacto() {
    }

    public Contacto(String nombres, String apellidos, String correo_electronico, String telefono, String imagen_usuario, String id_tarjeta) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo_electronico = correo_electronico;
        this.telefono = telefono;
        this.imagen_usuario = imagen_usuario;
        this.id_tarjeta = id_tarjeta;
    }

    public Contacto(String nombres, String apellidos, String correo_electronico, String telefono, String imagen_usuario) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo_electronico = correo_electronico;
        this.telefono = telefono;
        this.imagen_usuario = imagen_usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagen_usuario() {
        return imagen_usuario;
    }

    public void setImagen_usuario(String imagen_usuario) {
        this.imagen_usuario = imagen_usuario;
    }
}
