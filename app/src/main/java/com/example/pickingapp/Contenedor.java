package com.example.pickingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Contenedor extends AppCompatActivity {

	private ModelListItemAdapter adapter;
	private TextView sucursalAEscanear;
	private int numeroDeSucursales;
	private int indiceSucursalActual;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contenedor);
		ArrayList<ModelListItem> sucursalArray = new ArrayList<>();
		adapter = new ModelListItemAdapter(this, sucursalArray);
		ListView lista = findViewById(R.id.lista_sucursal_contenedor);
		lista.setAdapter(adapter);
		sucursalAEscanear = findViewById(R.id.sucursal_a_escanear);
		findViewById(R.id.btn_escanear_contenedor).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: escaneo con pistola
				escanear();
			}
		});
		cargarSucursales();
	}

	void cargarSucursales(){
		SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
		String noEmpleado = preferences.getString("num_empleado", "0");;
		String query = "SELECT DISTINCT c.control_id, c.id_sucursal " +
				"FROM control AS c " +
				"INNER JOIN `operador_has_control` AS ohc ON c.control_id = ohc.control_id " +
				"WHERE ohc.num_empleado = '" + noEmpleado + "' AND ohc.contenedor_id IS NULL  GROUP BY c.id_sucursal";
		Database.query(this, query, new VolleyCallback() {
			@Override
			public void onSucces(JSONArray response) {
				if(response != null){
					for (int i = 0; i < response.length(); i++){
						try {
							Log.i("Contenedor", response.getJSONObject(i).toString());
							JSONObject jsonObject = response.getJSONObject(i);
							adapter.add(new ModelListItem(jsonObject.getString("id_sucursal"), "Sin asignar",false));
							if(i == 0) {
								sucursalAEscanear.setText(jsonObject.getString("id_sucursal"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					numeroDeSucursales = response.length();
					indiceSucursalActual = 0;
				}
				if (response.length() == 0){
					finish();
				}
			}
		});
	}

	void cambiarSucursalActual(int indice, String idContenedor){
		Log.i("Contenedor", "sucursal: " + indice);
		if((indice - 1) >= 0 && (indice - 1) < numeroDeSucursales) {
			adapter.getItem(indice - 1).setChecked(true);
			adapter.getItem(indice - 1).setContenedor(idContenedor);
		}
		if (indice < numeroDeSucursales) {
			sucursalAEscanear.setText(adapter.getItem(indice).getSucursal());
		}
		else {
			sucursalAEscanear.setText("");
			TextView titulo = findViewById(R.id.contenedor_titulo);
			titulo.setText("Ya puede comenzar el picking");
			titulo.setTextColor(Color.parseColor("#4CAF50"));
		}
		adapter.notifyDataSetChanged();
	}

	void escanear () {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setCaptureActivity(CapturaAuxiliar.class);
		integrator.setOrientationLocked(true);
		integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
		integrator.setPrompt("Escaneando contenedor");
		integrator.initiateScan();
	}

	void asignarContenedor(int indice, String idContenedor){
		String idSucursal = adapter.getItem(indice).getSucursal();
		cambiarSucursalActual(++indiceSucursalActual, idContenedor);
		SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
		String noEmpleado = preferences.getString("num_empleado", "0");;
		Database.insert(this, "UPDATE `operador_has_control` AS ohc " +
				"INNER JOIN `control` AS c ON ohc.control_id = c.control_id " +
				"SET ohc.contenedor_id = '" + idContenedor + "' " +
				"WHERE ohc.num_empleado = '" + noEmpleado + "' AND c.id_sucursal = '" + idSucursal + "'");
	}

	@Override
	public void onActivityResult(int request_code, int result_code, Intent data) {
		super.onActivityResult(request_code, result_code, data);
		if (request_code != Activity.RESULT_CANCELED && data != null){
			IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
			asignarContenedor(indiceSucursalActual, result.getContents());
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}