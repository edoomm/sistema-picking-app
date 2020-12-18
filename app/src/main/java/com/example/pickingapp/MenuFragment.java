package com.example.pickingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // btnPickUp
        ImageButton btnPickUp = (ImageButton) view.findViewById(R.id.btnPickUp);
        btnPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickUp.class);
                intent.putExtra("firstFragment", "PickUpFragment");
                intent.putExtra("secondFragment", "none");
                startActivity(intent);
            }
        });

        // btnAlmacen
        ImageButton btnAlmacen = (ImageButton) view.findViewById(R.id.btnAlmacen);
        btnAlmacen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickUp.class);
                intent.putExtra("firstFragment", "none");
                intent.putExtra("secondFragment", "AlmacenFragment");
                startActivity(intent);
            }
        });

        //btnConfig
        ImageButton btnConfig = (ImageButton) view.findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Configuracion.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void btnClicked(View v) {

    }
}
