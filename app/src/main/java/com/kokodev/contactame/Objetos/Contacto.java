package com.kokodev.contactame.Objetos;

import java.io.Serializable;

/**
 * Created by koko on 15-05-17.
 */

public class Contacto implements Serializable {
    String idContacto,nombres,apellidos,correo_electronico,telefono,imagen_usuario;

    public Contacto() {
    }

    public Contacto(String idContacto, String nombres, String apellidos, String correo_electronico, String telefono, String imagen_usuario) {
        this.idContacto = idContacto;
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

    public String getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(String idContacto) {
        this.idContacto = idContacto;
    }
}
