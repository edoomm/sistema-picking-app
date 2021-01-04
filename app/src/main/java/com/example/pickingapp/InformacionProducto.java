package com.example.pickingapp;

import org.json.JSONObject;

public class InformacionProducto {
    private int sku;
    private int apartado;
    private int id_sucursal;
    private String descripcion;
    private String pasillo;
    private int rack;
    private int contenedor;

    public InformacionProducto (JSONObject json_informacion) {
        try {
            sku = json_informacion.getInt("sku");
            apartado = json_informacion.getInt("apartado");
            id_sucursal = json_informacion.getInt("id_sucursal");
            descripcion = json_informacion.getString("descripcion");
            pasillo = json_informacion.getString("pasillo");
            rack = json_informacion.getInt("rack");
            contenedor = json_informacion.getInt("contenedor_id");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public int getApartado() {
        return apartado;
    }

    public int getContenedor() {
        return contenedor;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public void setApartado(int apartado) {
        this.apartado = apartado;
    }

    public Boolean hasApartado (  ) {
        return apartado > 0;
    }

    public void decrementarApartado () {
        apartado--;
    }

    public int getId_sucursal() {
        return id_sucursal;
    }

    public void setId_sucursal(int id_sucursal) {
        this.id_sucursal = id_sucursal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPasillo() {
        return pasillo;
    }

    public void setPasillo(String pasillo) {
        this.pasillo = pasillo;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public void setContenedor(int contenedor) {
        this.contenedor = contenedor;
    }
}
