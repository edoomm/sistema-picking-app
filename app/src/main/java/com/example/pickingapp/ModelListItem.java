package com.example.pickingapp;

public class ModelListItem {
	private String sucursal;
	private String contenedor;
	private boolean checked;
	public  ModelListItem(String sucursal, String contenedor, boolean checked){
		this.sucursal = sucursal;
		this.contenedor = contenedor;
		this.checked = checked;
	}
	String getSucursal(){ return sucursal; }
	String getContenedor(){ return contenedor; }
	boolean getChecked(){ return checked; }
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public void setContenedor(String contenedor) {
		this.contenedor = contenedor;
	}
	public void setSucursal(String sucursal){
		this.sucursal = sucursal;
	}
}
