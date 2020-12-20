package com.example.pickingapp;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PickUpFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_up, container, false);
//        //btn lista
//        Button btnLista = view.findViewById(R.id.button_lista);
//        btnLista.setOnClickListener(
//                new View.OnClickListener(){
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(view.getContext(), Lista.class);
//                        startActivity(intent);
//                    }
//                }
//        );

        //btnEscaneo
        Button btnEscaneo = view.findViewById(R.id.button_scan);
        btnEscaneo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), Escaneo.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
