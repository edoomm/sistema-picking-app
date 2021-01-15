package com.example.pickingapp;

/**
 * Clase utilizada para poder mostrar la información en un Cardview
 */

public class Model {

    private String title;
    private String desc;
    private String cant;
    private String sucursal;

    public Model(String title, String desc, String cant, String sucursal) {
        this.title = title;
        this.desc = desc;
        this.cant = cant;
        this.sucursal = sucursal;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getCant() {
        return cant;
    }

    public String getSucursal() {
        return sucursal;
    }
}
