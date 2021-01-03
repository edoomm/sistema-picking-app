package com.example.pickingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlmacenFragment extends Fragment{

    private ListView search;
    private ArrayAdapter<String> adapter;
    private ImageView planograma;
    private TextView pasillo;
    private TextView rack;
    private TextView skuSeleccionado;
    private Context context;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_almacen, container, false);

        /* SETTING IU ELEMENTS */
        // Setting adapter
        ArrayList<String> sku_array = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                sku_array
        );
        context = view.getContext();

        // IU elemnts
        skuSeleccionado = (TextView) view.findViewById(R.id.sku_seleccionado);
        planograma = (ImageView) view.findViewById(R.id.planograma_almacen);
        pasillo = (TextView) view.findViewById(R.id.pasillo_almacen);
        rack = (TextView) view.findViewById(R.id.rack_almacen);

        //Search bar functionality
        search = view.findViewById(R.id.SKU_list_view);
        search.setAdapter(adapter);
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sku = (String) parent.getItemAtPosition(position);
                Log.i("Almacen", "Clicked item: " + position + " data: " + sku);
                Database.query(context, "SELECT * FROM `ubicacion` WHERE `sku`=" + sku, new VolleyCallback() {
                    @Override
                    public void onSucces(JSONArray response) {
                        cambiarUbicaion(response);
                    }
                });
            }
        });
        SearchView searchView = view.findViewById(R.id.SKU_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Database.query(context, "SELECT * FROM `ubicacion` WHERE `sku`=" + query, new VolleyCallback() {
                    @Override
                    public void onSucces(JSONArray response) {
                        cambiarUbicaion(response);
                    }
                });
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        //Inserting skus into adapter
        Database.query(context, "SELECT `sku` FROM `producto`", new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                mostrarProductos(response);
            }
        });

        /* SURTE ALMACEN */
        Button btnSurteAlmacen = (Button) view.findViewById(R.id.button_surte_almacen);
        btnSurteAlmacen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surteAlmacen();
            }
        });

        return view;
    }

    void surteAlmacen(){
        int size = skuSeleccionado.getText().length();
        Log.i("Almacen", "Range: "+17+" "+size);
        String sku = skuSeleccionado.getText().subSequence(17, size).toString();
        sku.replace(" ", "");
        if(adapter.getCount() == 1 && !sku.isEmpty()){
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(context);
            confirmacion.setTitle("Surte almacén del sku: " + sku);
            confirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    enviarTransaccion(sku);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = confirmacion.create();
            dialog.show();
        }
        else{
            AlertDialog.Builder metodo = new AlertDialog.Builder(context);
            if(!sku.isEmpty()) {
                metodo.setTitle("Surte almacén del sku: " + sku + " o ¿Desea escanear otro sku?");
                metodo.setPositiveButton("SKU: " + sku, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enviarTransaccion(sku);
                    }
                });
            }
            else{
                metodo.setTitle("¿Qué desea hacer?");
            }
            metodo.setNeutralButton("Escanear nuevo sku", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: escanear codigo con pistola
                    escanear();
                }
            });
            metodo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = metodo.create();
            dialog.show();
        }
    }

    void enviarTransaccion(String sku){
        SharedPreferences preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String noEmpleado = preferences.getString("num_empleado", "0");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String horaYFecha = simpleDateFormat.format(new Date());
        String tipoMovimiento = "SA";
        int cantidad = -1;
        Database.insert(
                context,
                "INSERT INTO `transaccion` (`transaccion_id`, `num_empleado`, `contenedor_id`, `sku`, `control_id`, `hora_realizada`, `tipo_movimiento`, `cantidad`) " +
                        "VALUES (NULL, '"+noEmpleado+"', NULL, '"+sku+"', NULL, '"+horaYFecha+"', '"+tipoMovimiento+"', '"+cantidad+"')"
        );
    }

    void mostrarProductos(JSONArray response){
        if(response != null){
            for (int i = 0; i < response.length(); i++){
                try {
                    Log.i("Almacen", response.getJSONObject(i).toString());
                    adapter.add(response.getJSONObject(i).getString("sku"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void cambiarUbicaion(JSONArray response){
        Log.i("Connection", response.toString());
        try {
            JSONObject ubicacion = response.getJSONObject(0);
            planograma.setImageResource(SeleccionadorPlanograma.getDrawable(context, ubicacion.getInt("columna"), ubicacion.getInt("nivel")));
            pasillo.setText("Pasillo: " + ubicacion.getString("pasillo"));
            rack.setText("Rack: " + ubicacion.getString("rack"));
            skuSeleccionado.setText("Sku seleccionado: "+ubicacion.getString("sku"));
        } catch (JSONException e) {
            Toast.makeText(context, "No se encontró la ubicación", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void escanear () {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        integrator.setCaptureActivity(CapturaAuxiliar.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt("Escaneando sku");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data) {
        Log.i("Almacen", "on activity result");
        if (result_code != getActivity().RESULT_CANCELED && data != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
            enviarTransaccion(result.getContents());
            Log.i("Almacen", "Sku escaneado: "+result.getContents());
        }
    }
}
