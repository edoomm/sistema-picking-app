package com.example.pickingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity  {

    private TextView txtNumEmpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.escanear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear_codigo();
            }
        });

        txtNumEmpl = findViewById(R.id.txtNoEmpleado);
    }

    public void ingresar(View view) {
//        EditText textNoEmpleado = findViewById(R.id.txtNoEmpleado);
//        String numero_empleado = textNoEmpleado.getText().toString();
//        ingresar(numero_empleado);

        if (txtNumEmpl.getText().equals("")) {
            Toast.makeText(this, "Ingrese su numero de empleado", Toast.LENGTH_SHORT).show();
        }

        ingresar(txtNumEmpl.getText().toString());
    }

    void ingresar ( String numero ) {
        Database.query(this, "SELECT nombre FROM Operador WHERE num_empleado = '" + numero + "'", new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                if (response == null) {
                    Log.i("Error en conexion", "Fallo en la conexión");
                }
                else {
                    if (response.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Número de empleado incorrecto o no registrado en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            // saving num_empl
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("num_empleado", numero);
                            editor.apply();

                            // acessing system
//                            Toast.makeText(getApplicationContext(), "¡Bienvenido " + response.getJSONObject(0).getString("nombre") + "!", Toast.LENGTH_SHORT).show();
                            mostrarMenu();
                        }
                        catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                            Log.i("Exception", e.getMessage());
                        }
                    }
                    Log.i("Respuesta", response+"-");
                }
            }
        });
    }

    void mostrar_main () {
        startActivity(new Intent(this, MainActivity.class));
    }

    void mostrarMenu() {
        startActivity(new Intent(this, Menu.class));
    }

    void escanear_codigo () {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CapturaAuxiliar.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt("Escaneando código de barras");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult (int request_code, int result_code, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
        if ( result.getContents() == null ) {
            mostrar_main();
        } else {
            ingresar( result.getContents() );
        }
        super.onActivityResult(request_code, result_code, data);
    }

}