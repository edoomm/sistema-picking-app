package com.example.pickingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        startActivity(new Intent(this, Menu.class));
    }

    void ingreso_escaneo(){
        startActivity(new Intent(this, Menu.class));
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
            ingreso_escaneo();
        }

        super.onActivityResult(request_code, result_code, data);
    }

}