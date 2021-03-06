package com.example.pickingapp.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.pickingapp.InformacionProducto;
import com.example.pickingapp.ProductInformationSingleton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class PageViewModel extends ViewModel {

	private final int numeroCaracteresDescripcion = 20;
	private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

	private LiveData<List<String>> lista_apartados = Transformations.map(mIndex, new Function<Integer, List<String>>() {
		@Override
		public List<String> apply(Integer input) {
			ArrayList<InformacionProducto> productos = ProductInformationSingleton.getProductInformation().getProductos();
			ArrayList<String> cadenas = new ArrayList<>();
			switch (input){
				case 1: // Por escanear
					for ( int i = 0 ; i < productos.size() ; i++ ) {
						InformacionProducto producto = productos.get(i);
						if ( producto.hasApartado() ) {
							String cadena = "";
							String descripcion = producto.getDescripcion();
							if ( descripcion.length() > numeroCaracteresDescripcion ) {
								descripcion = descripcion.substring(0, numeroCaracteresDescripcion) + "...";
							}
							cadena = descripcion + "\n" +
									"SKU: " + producto.getSku() + "\n" +
									"CANTIDAD: " + producto.getApartado() + "\n" +
									"SUCURSAL: " + producto.getId_sucursal() + "\n";
							cadenas.add(cadena);
						}
					}
					return cadenas;
				case 2: // Escaneado
					for ( int i = 0 ; i < productos.size() ; i++ ) {
						InformacionProducto producto = productos.get(i);
						if ( producto.getEstado() == 2 ) {
							String cadena = "";
							String descripcion = producto.getDescripcion();
							if ( descripcion.length() > numeroCaracteresDescripcion ) {
								descripcion = descripcion.substring(0, numeroCaracteresDescripcion) + "...";
							}
							cadena = descripcion + "\n" +
									"SKU: " + producto.getSku() + "\n" +
									"CANTIDAD: " + producto.getApartado() + "\n" +
									"SUCURSAL: " + producto.getId_sucursal() + "\n";
							cadenas.add(cadena);
						}
					}
					return cadenas;
				default:
					return Arrays.asList();
			}
		}
	});

	public void setIndex(int index) {
		mIndex.setValue(index);
	}

	public LiveData<List<String>> getList() {
		return lista_apartados;
	}
}