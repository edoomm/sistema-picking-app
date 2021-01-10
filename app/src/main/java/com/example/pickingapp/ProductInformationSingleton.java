package com.example.pickingapp;

import java.util.ArrayList;

public class ProductInformationSingleton {
    private ArrayList<InformacionProducto> productos;
    private ArrayList<Model> models;
    public static ProductInformationSingleton productInformation = null;

    private ProductInformationSingleton ( ArrayList<InformacionProducto> informacion, ArrayList<Model> models ) {
        productos = informacion;
        this.models = models;
    }

    // Method for first instance in the program
    public static ProductInformationSingleton getProductInformation ( ArrayList<InformacionProducto> informacion, ArrayList<Model> models ) {
        if ( productInformation == null ) {
            productInformation = new ProductInformationSingleton( informacion, models );
        }
        return productInformation;
    }

    public static ProductInformationSingleton getProductInformation () {
        return productInformation;
    }

    public ArrayList<InformacionProducto> getProductos () {
        return productos;
    }

    public void setProductos(ArrayList<InformacionProducto> productos) {
        this.productos = productos;
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public void setModels(ArrayList<Model> models) {
        this.models = models;
    }

}
