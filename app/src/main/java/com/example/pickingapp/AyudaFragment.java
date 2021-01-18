package com.example.pickingapp;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickingapp.ui.main.ConsultaPreguntas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



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
        return view;
    }
}
