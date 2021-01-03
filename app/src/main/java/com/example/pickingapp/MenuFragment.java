package com.example.pickingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;

public class MenuFragment extends Fragment {
    private TextView lblBienvenida;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        /* SETTING WELCOME MESSAGE */
        SharedPreferences preferences = view.getContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String noEmpleado = preferences.getString("num_empleado", "0");
        if (noEmpleado.equals("0")) {
            Toast.makeText(view.getContext(), "No se pudo guardar la información del empleado al ingresar al sistema", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return view;
        }

        Database.query(getContext(), "SELECT nombre FROM Operador WHERE num_empleado = '" + noEmpleado + "'", new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                Log.i("Respuesta", response+"-");

                // Errors handling
                if (response == null) {
                    Log.i("Error en conexion", "Fallo en la conexión a la base de datos");
                    startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return;
                }
                if (response.length() == 0) {
                    Toast.makeText(view.getContext(), "Número de empleado incorrecto o no registrado en la base de datos", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return;
                }

                try {
                    String nombreEmpleado = response.getJSONObject(0).getString("nombre");
                    setWelcomeText(nombreEmpleado);
                } catch (JSONException jsone) {
                    jsone.printStackTrace();
                }
            }
        });
        lblBienvenida = (TextView) view.findViewById(R.id.lblBienvenido);

        /* SETTING LISTENERS TO BUTTONS */
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

        // btnAyuda
        ImageButton btnAyuda = (ImageButton) view.findViewById(R.id.btnAyuda);
        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Configuracion.class);
                intent.putExtra("firstFragment", "AyudaFragment");
                intent.putExtra("secondFragment", "none");
                startActivity(intent);
            }
        });


        //btnConfig
        ImageButton btnConfig = (ImageButton) view.findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Configuracion.class);
                intent.putExtra("firstFragment", "none");
                intent.putExtra("secondFragment", "ConfigFragment");
                startActivity(intent);
            }
        });

        return view;
    }

    private void setWelcomeText(String name) {
        lblBienvenida.setText("¡Bienvenida " + name + "!");
    }


}