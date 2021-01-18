package com.example.pickingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProblemaPregunta extends AppCompatActivity {
    String id_rep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema_pregunta);

        Button button= findViewById(R.id.button_enviar_problema);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReporte();
                EditText editText= (EditText) findViewById(R.id.editTextProblema);
                editText.setText("");
            }
        });

    }

    private void enviarReporte(){
        final Handler handler = new Handler(Looper.getMainLooper());
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String num_emp = "'"+ sharedPreferences.getString("num_empleado",null)+"'";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = "'"+ dateFormat.format(date) +"'";
        EditText editText = (EditText) findViewById(R.id.editTextProblema);

        String reporte = "'"+editText.getText().toString()+"'";

        String string="INSERT INTO reporte (operador_num_empleado,reporte,fecha) VALUES("+num_emp+","+reporte+","+fecha+")";
        Database.insert(this,string);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Database.query(getApplicationContext(), "SELECT * from reporte where operador_num_empleado="+num_emp+"order by reporte_id desc limit 1", new VolleyCallback() {
                    @Override
                    public void onSucces(JSONArray response) {
                        try {
                            JSONObject respuesta = response.getJSONObject(0);
                            id_rep = respuesta.getString("reporte_id");
                            Toast.makeText(getApplicationContext(),"Reporte enviado con el ID:"+id_rep,Toast.LENGTH_LONG).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        },100);

    }
}