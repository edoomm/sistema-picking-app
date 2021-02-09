package com.example.pickingapp;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Clase que modela toda la información relevante al producto
 */

public class InformacionProducto implements Serializable, Comparable {
    private int control_id;
    private int sku;
    private int apartado_global;
    private int apartado;
    private int id_sucursal;
    private String descripcion;
    private String pasillo;
    private int rack;
    private int columna;
    private int nivel;
    private int contenedor;
    private int estado; // 0 sin recolectar, 1 : recolectado, 2 : producto faltante
    private int prioridad;
    private int unidadMedida;


    public InformacionProducto (JSONObject json_informacion) {
        try {
            control_id = json_informacion.getInt("control_id");
            sku = json_informacion.getInt("sku");
            apartado = json_informacion.getInt("apartado");
            apartado_global = json_informacion.getInt("apartado");
            id_sucursal = json_informacion.getInt("id_sucursal");
            descripcion = json_informacion.getString("descripcion");
            pasillo = json_informacion.getString("pasillo");
            rack = json_informacion.getInt("rack");
            columna = json_informacion.getInt("columna");
            nivel = json_informacion.getInt("nivel");
            contenedor = json_informacion.getInt("contenedor_id");
            estado = 0;
            prioridad = json_informacion.getInt("prioridad");
            unidadMedida = json_informacion.getInt("unidad_medida");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getApartadoGlobal() {
        return apartado_global;
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

    public void decrementarApartado (int cantidad) {
        apartado -= cantidad;
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

    public int getControl_id() {
        return control_id;
    }

    public void setControl_id(int control_id) {
        this.control_id = control_id;
    }

    public int getUnidadMedida() { return  unidadMedida; }

    @Override
    public int compareTo(Object o) {
        return this.prioridad - ((InformacionProducto) o).prioridad;
    }
}
