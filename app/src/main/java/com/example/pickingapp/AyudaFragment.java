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

        //consultaFaq(view);
        return view;
    }

    public void consultaFaq(View view){
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.ayuda);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



        Database.query(getContext(), "Select * from Faq", new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                JSONObject respuesta;
                TextView[] textViewsPreguntas = new  TextView[response.length()];
                TextView[] textViewsRespuestas = new TextView[response.length()];
                try {
                    for (int i=0; i<response.length();i++){
                        respuesta=response.getJSONObject(i);

                        textViewsPreguntas[i] = new TextView(getContext());
                        textViewsRespuestas[i] = new TextView(getContext());

                        textViewsPreguntas[i].setText(respuesta.getString("pregunta"));
                        textViewsRespuestas[i].setText(respuesta.getString("respuesta"));

                        textViewsPreguntas[i].setLayoutParams(params);
                        textViewsPreguntas[i].setTextSize(24);
                        textViewsPreguntas[i].setTextColor(Color.BLUE);
                        textViewsRespuestas[i].setLayoutParams(params);
                        if (i!=0){

                        }else {

                        }


                        relativeLayout.addView(textViewsPreguntas[i]);
                        relativeLayout.addView(textViewsRespuestas[i]);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
