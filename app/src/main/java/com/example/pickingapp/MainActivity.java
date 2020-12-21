package com.example.pickingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity  {
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
    }

    public void ingresar(View view) {
        EditText textNoEmpleado = findViewById(R.id.txtNoEmpleado);
        String numero_empleado = textNoEmpleado.getText().toString();
        ingresar(numero_empleado);
    }

    void ingresar ( String numero ) {
        if ( numero.equals("123456789") ) {
            startActivity(new Intent(this, Menu.class));
        } else {
            EditText textNoEmpleado = findViewById(R.id.txtNoEmpleado);
            textNoEmpleado.getText().clear();
            Toast.makeText(getApplicationContext(), "Número no reconocido, escanee su número de empleado", Toast.LENGTH_SHORT).show();
        }
    }

    void mostrar_main () {
        startActivity(new Intent(this, MainActivity.class));
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