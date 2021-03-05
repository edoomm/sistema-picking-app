package com.example.pickingapp;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.pickingapp.ui.main.ConsultaPreguntas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AyudaFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ayuda,container,false);

        FloatingActionButton floatingActionButton1 = (FloatingActionButton) view.findViewById(R.id.my_fab1);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsultaPreguntas.class);
                startActivity(intent);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.my_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),ProblemaPregunta.class);
                startActivity(intent);
            }
        });

        consultaFaq(view);
        return view;
    }

    public void consultaFaq(View view){
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.ayuda);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



        Database.query(getContext(), "Select * from Faq", new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                JSONObject respuesta1,respuesta2,respuesta3;
                TextView[] textViewsPreguntas = new  TextView[response.length()];
                TextView[] textViewsRespuestas = new TextView[response.length()];
                try {
                    respuesta1=response.getJSONObject(0);
                    respuesta2=response.getJSONObject(1);
                    respuesta3=response.getJSONObject(2);
                    TextView p1,p2,p3,r1,r2,r3;
                    p1 = (TextView) relativeLayout.findViewById(R.id.pregunta1);
                    p2 = (TextView) relativeLayout.findViewById(R.id.pregunta2);
                    p3 = (TextView) relativeLayout.findViewById(R.id.pregunta3);

                    r1 = (TextView) relativeLayout.findViewById(R.id.respuesta1);
                    r2 = (TextView) relativeLayout.findViewById(R.id.respuesta2);
                    r3 = (TextView) relativeLayout.findViewById(R.id.respuesta3);


                    p1.setText(respuesta1.getString("pregunta"));
                    r1.setText(respuesta1.getString("respuesta"));

                    p2.setText(respuesta2.getString("pregunta"));
                    r2.setText(respuesta2.getString("respuesta"));

                    p3.setText(respuesta3.getString("pregunta"));
                    r3.setText(respuesta3.getString("respuesta"));

                    //código para inserción dinámica de preguntas

                    /*for (int i=0; i<3;i++){
                        respuesta=response.getJSONObject(i);
                        textViewsPreguntas[i] = new TextView(getContext());
                        textViewsRespuestas[i] = new TextView(getContext());

                        textViewsPreguntas[i].setText(respuesta.getString("pregunta"));
                        textViewsRespuestas[i].setText(respuesta.getString("respuesta"));

                        textViewsPreguntas[i].setLayoutParams(params);
                        textViewsPreguntas[i].setTextSize(24);
                        textViewsPreguntas[i].setTextColor(Color.BLUE);
                        textViewsRespuestas[i].setLayoutParams(params);

                        relativeLayout.addView(textViewsPreguntas[i]);
                        relativeLayout.addView(textViewsRespuestas[i]);
                    }*/
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
