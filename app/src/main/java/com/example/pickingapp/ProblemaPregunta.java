package com.example.pickingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProblemaPregunta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema_pregunta);

        Button button= findViewById(R.id.button_enviar_problema);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReporte();
                Toast.makeText(getApplicationContext(), "Reporte Enviado", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String enviarReporte(){
        String mensaje="";
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String num_emp = "'"+ sharedPreferences.getString("num_empleado",null)+"'";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = "'"+ dateFormat.format(date) +"'";
        EditText editText = (EditText) findViewById(R.id.editTextProblema);
        String reporte = "'"+editText.getText().toString()+"'";

        String string="INSERT INTO reporte (operador_num_empleado,reporte,fecha) VALUES("+num_emp+","+reporte+","+fecha+")";

        Database.insert(this,string,new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                if (response == null){
                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getApplicationContext(), "Reporte enviado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return mensaje;
    }
}