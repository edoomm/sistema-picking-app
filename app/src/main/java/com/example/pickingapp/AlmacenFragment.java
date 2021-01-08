package com.example.pickingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private int estatus_escaneo = -1;
    private final int NO_ESCANEO = 0;
    private final int SURTE_ALMACEN = 1;
    private final int REABASTECER_SKU = 2;
    private final int REABASTECER_UBICACION = 3;
    private final int SOLICITAR_SKU = 1;
    private final int SOLICITAR_UBICACION = 2;
    private final int SOLICITAR_CANTIDAD = 3;
    String skuReabasto = "";
    String ubicacionReabasto = "";

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
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
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

        /* REABASTECER */
        Button btnReabastecer = (Button) view.findViewById(R.id.button_reabastecer);
        btnReabastecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                String noEmpleado = preferences.getString("num_empleado", "0");
                Database.query(context, "SELECT `tipo_usuario` FROM `usuario` WHERE `operador_num_empleado`='" + noEmpleado + "'", new VolleyCallback() {
                    @Override
                    public void onSucces(JSONArray response) {
                        try {
                            JSONObject usuario = response.getJSONObject(0);
                            if(usuario.getInt("tipo_usuario") == 1)
                                reabastecer(SOLICITAR_SKU);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        return view;
    }

    void reabastecer(int estatus){
        if(estatus == SOLICITAR_SKU) {
            Log.i("Almacen", "Reabasteciendo");
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(context);
            confirmacion.setTitle("Escanea el sku a reabastecer");
            confirmacion.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    estatus_escaneo = REABASTECER_SKU;
                    //TODO: escaneo con pistola
                    escanear();
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = confirmacion.create();
            dialog.show();
        }
        else if(estatus == SOLICITAR_UBICACION){
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(context);
            confirmacion.setTitle("Escanea la ubicacion de sku: "+skuReabasto);
            confirmacion.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    estatus_escaneo = REABASTECER_UBICACION;
                    //TODO: escaneo con pistola
                    escanear();
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = confirmacion.create();
            dialog.show();
        }
        else if(estatus == SOLICITAR_CANTIDAD){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Ingresa la cantidad");
            final EditText entrada = new EditText(context);
            entrada.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(entrada);
            builder.setPositiveButton("Reabastecer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int cantidad = Integer.parseInt(entrada.getText().toString());
                    enviarTransaccion(skuReabasto, "RA", cantidad);
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
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
                    enviarTransaccion(sku, "SA", -1);
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
                        enviarTransaccion(sku, "SA", -1);
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
                    estatus_escaneo = SURTE_ALMACEN;
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

    void enviarTransaccion(String sku, String tipoMovimiento, int cantidad){
        SharedPreferences preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String noEmpleado = preferences.getString("num_empleado", "0");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String horaYFecha = simpleDateFormat.format(new Date());
        Database.insert(
                context,
                "INSERT INTO `transaccion` (`transaccion_id`, `num_empleado`, `contenedor_id`, `sku`, `control_id`, `hora_realizada`, `tipo_movimiento`, `cantidad`) " +
                        "VALUES (NULL, '"+noEmpleado+"', NULL, '"+sku+"', NULL, '"+horaYFecha+"', '"+tipoMovimiento+"', '"+cantidad+"')"
        );
        Toast.makeText(context, "Hecho", Toast.LENGTH_SHORT).show();
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
        if (result_code != getActivity().RESULT_CANCELED && data != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
            switch (estatus_escaneo){
                case SURTE_ALMACEN:
                    Log.i("Almacen", "Sku escaneado: "+result.getContents());
                    Database.query(context, "SELECT `sku` FROM `producto` WHERE `sku`='" + result.getContents() + "'", new VolleyCallback() {
                        @Override
                        public void onSucces(JSONArray response) {
                            if (response == null || response.length() == 0){
                                Toast.makeText(context, "No se encontró el sku en la base de datos", Toast.LENGTH_LONG).show();
                            }
                            else{
                                enviarTransaccion(result.getContents(), "SU", -1);
                            }
                        }
                    });
                    break;
                case REABASTECER_UBICACION:
                    ubicacionReabasto = result.getContents();
                    Log.i("Almacen", "Ubicacion escaneada: "+result.getContents());
                    Database.query(context, "SELECT `ubicacion` FROM ubicacion WHERE sku='" + skuReabasto + "'", new VolleyCallback() {
                        @Override
                        public void onSucces(JSONArray response) {
                            boolean ubicacionCorrecta = false;
                            if (response == null || response.length() == 0){
                                Toast.makeText(context, "No se encontró la unicación en la base de datos", Toast.LENGTH_LONG).show();
                            }
                            else {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject ubicacion = response.getJSONObject(i);
                                        if (ubicacion.getString("ubicacion").equals(ubicacionReabasto)) {
                                            reabastecer(SOLICITAR_CANTIDAD);
                                            ubicacionCorrecta = true;
                                            break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if(ubicacionCorrecta == false)
                                Toast.makeText(context, "La ubicación no pertenece al sku: " + skuReabasto, Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case REABASTECER_SKU:
                    skuReabasto = result.getContents();
                    Log.i("Almacen", "Sku escaneado: "+result.getContents());
                    Database.query(context, "SELECT `sku` FROM `producto` WHERE `sku`='" + skuReabasto + "'", new VolleyCallback() {
                        @Override
                        public void onSucces(JSONArray response) {
                            if (response == null || response.length() == 0){
                                Toast.makeText(context, "No se encontró el sku en la base de datos", Toast.LENGTH_LONG).show();
                            }
                            else{
                                reabastecer(SOLICITAR_UBICACION);
                            }
                        }
                    });
                    break;
                default:
                    break;
	        }
	        estatus_escaneo = NO_ESCANEO;
        }
    }
}
