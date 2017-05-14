package com.kokodev.contactame.Objetos;

/**
 * Created by koko on 13-05-17.
 */

public class Tarjeta {

    private String cargo,imagenTarjeta;
    private Boolean publico;

    public Tarjeta() {
    }

    public Tarjeta(String cargo, String imagenTarjeta, Boolean publico) {
        this.cargo = cargo;
        this.imagenTarjeta = imagenTarjeta;
        this.publico = publico;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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
