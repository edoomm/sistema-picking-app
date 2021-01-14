package com.example.pickingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Contenedor extends AppCompatActivity {

	private ModelListItemAdapter adapter;
	private TextView sucursalAEscanear;
	private int numeroDeSucursales;
	private int indiceSucursalActual;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_contenedor);
		ArrayList<ModelListItem> sucursalArray = new ArrayList<>();
		adapter = new ModelListItemAdapter(this, sucursalArray);
		ListView lista = findViewById(R.id.lista_sucursal_contenedor);
		lista.setAdapter(adapter);
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (adapter.getItem(position).getChecked()){
					Log.i("Contenedor", "Clicked item " + position);
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("¿Eliminar contenedor de sucursal: " + adapter.getItem(position).getSucursal() + "?");
					builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cambiarContenedor(adapter.getItem(position).getSucursal(), adapter.getItem(position).getContenedor());
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
				else {
					sucursalAEscanear.setText(adapter.getItem(position).getSucursal());
					indiceSucursalActual = position;
				}
			}
		});
		sucursalAEscanear = findViewById(R.id.sucursal_a_escanear);
		findViewById(R.id.btn_escanear_contenedor).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: escaneo con pistola
				if ( indiceSucursalActual == numeroDeSucursales ) {
					Toast.makeText(getApplicationContext(), "Ya se han asignado todas las sucursales.", Toast.LENGTH_LONG).show();
				}
				else {
					escanear();
				}

			}
		});
		findViewById(R.id.btn_iniciar_picking).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startPickUp();
			}
		});
		findViewById(R.id.btn_iniciar_picking).setVisibility(View.INVISIBLE);

		cargarSucursales();
	}

	void cambiarContenedor(String idSucursal, String idContenedor){
		SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
		String noEmpleado = preferences.getString("num_empleado", "0");;
		String query = "SELECT c.control_id FROM `control` AS c " +
				"LEFT JOIN `operador_has_control` AS ohc ON ohc.control_id = c.control_id " +
				"WHERE c.id_sucursal = '" + idSucursal + "' " +
				"AND ohc.contenedor_id = '" + idContenedor + "' " +
				"AND c.asignado = 1 " +
				"AND c.control_id IN (SELECT `control_id` FROM `transaccion` WHERE cantidad < 0)";
		String queryUpdate = "UPDATE `operador_has_control` AS ohc " +
				"INNER JOIN `control` AS c ON ohc.control_id = c.control_id " +
				"SET ohc.contenedor_id = NULL " +
				"WHERE ohc.num_empleado = '" + noEmpleado + "' AND c.id_sucursal = '" + idSucursal + "' " +
				"AND ohc.control_id NOT IN (SELECT `control_id` FROM `transaccion` WHERE `cantidad` != 0) " +
				"AND (c.asignado = 1)";
		Database.query(context, query, new VolleyCallback() {
			@Override
			public void onSucces(JSONArray response) {
				if (response != null && response.length() == 0){
					Log.i("Contenedor", response.toString());
					Database.insert(context, queryUpdate);
					recreate();
				}
				else {
					Toast.makeText(context, "El contenedor ya está en uso", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	void cargarSucursales(){
		SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
		String noEmpleado = preferences.getString("num_empleado", "0");;
		String query = "SELECT DISTINCT c.control_id, c.id_sucursal, ohc.contenedor_id " +
				"FROM control AS c " +
				"INNER JOIN `operador_has_control` AS ohc ON c.control_id = ohc.control_id " +
				"WHERE ohc.num_empleado = '" + noEmpleado + "' " + /*"' AND ohc.contenedor_id IS NULL  " +*/
				"AND ohc.control_id NOT IN (SELECT `control_id` FROM `transaccion` WHERE `cantidad` != 0) " +
				"AND (c.asignado = 1) " +
				"GROUP BY c.id_sucursal " +
				"ORDER BY ohc.contenedor_id DESC";
		Database.query(this, query, new VolleyCallback() {
			@Override
			public void onSucces(JSONArray response) {
				if (response != null){
					indiceSucursalActual = -1;
					for (int i = 0; i < response.length(); i++){
						try {
							Log.i("Contenedor", response.getJSONObject(i).toString());
							JSONObject jsonObject = response.getJSONObject(i);
							if (jsonObject.isNull("contenedor_id")) {
								adapter.add(new ModelListItem(jsonObject.getString("id_sucursal"), "Sin asignar", false));
								if (indiceSucursalActual == -1) {
									indiceSucursalActual = i;
									sucursalAEscanear.setText(jsonObject.getString("id_sucursal"));
								}
							}
							else {
								adapter.add(new ModelListItem(jsonObject.getString("id_sucursal"), jsonObject.getString("contenedor_id"), true));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					numeroDeSucursales = response.length();
				}
			}
		});
	}

	void escanear () {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setCaptureActivity(CapturaAuxiliar.class);
		integrator.setOrientationLocked(true);
		integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
		integrator.setPrompt("Escaneando contenedor");
		integrator.initiateScan();
	}

	int suguienteSucursal(){
		adapter.sort(new Comparator<ModelListItem>() {
			@Override
			public int compare(ModelListItem o1, ModelListItem o2) {
				if (o1.getChecked() == o2.getChecked()) {
					return o1.getContenedor().compareTo(o2.getContenedor());
				}
				else if (o1.getChecked()) {
					return -1;
				}
				else {
					return 1;
				}
			}
		});
		int i = 0;
		for (; i < adapter.getCount(); i++){
			if (adapter.getItem(i).getChecked() == false){
				return i;
			}
		}
		return i;
	}

	void asignarContenedor(String idContenedor){
		String idSucursal = adapter.getItem(indiceSucursalActual).getSucursal();
		//modificar sucursal escaneadp
		if (indiceSucursalActual >= 0 && indiceSucursalActual < numeroDeSucursales) {
			adapter.getItem(indiceSucursalActual).setChecked(true);
			adapter.getItem(indiceSucursalActual).setContenedor(idContenedor);
		}
		//establecer siguiente sucursal
		indiceSucursalActual = suguienteSucursal();
		if (indiceSucursalActual < numeroDeSucursales) {
			sucursalAEscanear.setText(adapter.getItem(indiceSucursalActual).getSucursal());
		}
		else {
			sucursalAEscanear.setText("");
			TextView titulo = findViewById(R.id.contenedor_titulo);
			titulo.setText("Ya puede comenzar el picking");
			titulo.setTextColor(Color.parseColor("#4CAF50"));
			findViewById(R.id.btn_iniciar_picking).setVisibility(View.VISIBLE);
		}
		adapter.notifyDataSetChanged();
		SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
		String noEmpleado = preferences.getString("num_empleado", "0");
		String query = "UPDATE `operador_has_control` AS ohc " +
				"INNER JOIN `control` AS c ON ohc.control_id = c.control_id " +
				"SET ohc.contenedor_id = '" + idContenedor + "' " +
				"WHERE ohc.num_empleado = '" + noEmpleado + "' AND c.id_sucursal = '" + idSucursal + "' AND ohc.contenedor_id IS NULL " +
				"AND ohc.control_id NOT IN (SELECT `control_id` FROM `transaccion` WHERE `cantidad` != 0) " +
				"AND (c.asignado = 1)";
		Database.insert(this, query);
	}

	void verificarExisteContenedor(String contenedorId){
		Database.query(this, "SELECT `contenedor_id` FROM `contenedor` WHERE `contenedor_id` = '" + contenedorId + "'", new VolleyCallback() {
			@Override
			public void onSucces(JSONArray response) {
				if (response != null && response.length() == 1){
					verificarContenedorNoRepetido(contenedorId);
				}
				else {
					Toast.makeText(context, "Contenedor no válido", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	void verificarContenedorNoRepetido(String contenedorId){
		SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
		String noEmpleado = preferences.getString("num_empleado", "0");
		String query = "SELECT `contenedor_id` FROM `operador_has_control` " +
				"WHERE `contenedor_id` IN (" +
				"SELECT DISTINCT ohc.contenedor_id " +
				"FROM control AS c " +
				"INNER JOIN `operador_has_control` AS ohc ON c.control_id = ohc.control_id " +
				"WHERE ohc.num_empleado = '" + noEmpleado + "' AND ohc.contenedor_id = '" + contenedorId + "' " +
				"AND ohc.control_id NOT IN (SELECT `control_id` FROM `transaccion` WHERE `cantidad` != 0) " +
				"AND (c.asignado = 1) " +
				")";
		Database.query(this, query, new VolleyCallback() {
			@Override
			public void onSucces(JSONArray response) {
				if (response != null && response.length() == 0){
					asignarContenedor(contenedorId);
				}
				else {
					Toast.makeText(context, "El contendor ya está en uso", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int request_code, int result_code, Intent data) {
		super.onActivityResult(request_code, result_code, data);
		if (request_code != Activity.RESULT_CANCELED && data != null){
			IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
			verificarExisteContenedor(result.getContents());
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case android.R.id.home:
				startPickUp();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		startPickUp();
		super.onBackPressed();
	}

	void startPickUp(){
		Intent intent = new Intent(this, PickUp.class);
		intent.putExtra("firstFragment", "PickUpFragment");
		intent.putExtra("secondFragment", "none");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}