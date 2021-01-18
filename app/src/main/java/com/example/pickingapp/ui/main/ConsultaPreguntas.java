package com.example.pickingapp.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.pickingapp.Database;
import com.example.pickingapp.VolleyCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConsultaPreguntas extends AppCompatActivity {
String string="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_preguntas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("Seguimiento");
        TextView textView = findViewById(R.id.textViewConsultaPreguntas);
        consultarPreguntas();
        textView.setText(string);
    }

    private void consultarPreguntas(){
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String num_emp = "'"+sharedPreferences.getString("num_empleado",null)+"'";

        Database.query(this, "SELECT * from reporte where operador_num_empleado=" + num_emp, new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                try {
                    JSONObject consulta_reporte;
                    for (int i=0;i<response.length();i++){
                         consulta_reporte= response.getJSONObject(i);
                        string=string + "ID reporte:"+consulta_reporte.getString("reporte_id")
                                +"\nReporte:"+consulta_reporte.getString("reporte");

                        if (consulta_reporte.getString("respuesta").equals("null")){
                            string = string+"\nRespuesta:Aún no ha recibido respuesta"
                                    +"\nFecha de reporte:"+consulta_reporte.getString("fecha")+"\n\n\n";
                        }else {
                            string = string+"\nRespuesta:"+consulta_reporte.getString("respuesta")
                                    +"\nFecha de reporte:"+consulta_reporte.getString("fecha")+"\n\n\n";
                        }
                    }
                    TextView textView = findViewById(R.id.textViewConsultaPreguntas);
                    textView.setText(string);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}