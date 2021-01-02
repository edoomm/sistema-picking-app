package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PickUpFragment extends Fragment implements VolleyCallback{

    private ViewPager viewPager;
    private Adapter adapter;
    private List<Model> models;
    private ArrayList<ArrayList<String>> attributes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_up, container, false);
        //btn lista
        Button btnLista = view.findViewById(R.id.button_lista);
        btnLista.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), Lista.class);
                        startActivity(intent);
                    }
                }
        );

        viewPager =  (ViewPager) view.findViewById(R.id.productsPager);

        //btnEscaneo
        Button btnEscaneo = view.findViewById(R.id.button_scan);
        btnEscaneo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                if(preferences.getString("escaneo_preferido", "escaner").equals("escaner")){
                    Intent intent = new Intent(view.getContext(), Escaneo.class);
                    startActivity(intent);
                }
                else{
                    escanear_codigo(view);
                }

            }
        });

        setViewPagerUp(view);

        return view;
    }

    public void escanear_codigo ( View v ) {
        escanear();
    }

    @Override
    public void onSucces (JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject producto = response.getJSONObject(i);
                String sku = producto.getString("sku");
                String descripcion = producto.getString("descripcion");
                models.add(new Model("SKU: " + sku, "Descripción: " + descripcion, "A.01.01.02"));
            }
            adapter = new Adapter(models, getContext());
            viewPager.setAdapter(adapter);
            viewPager.setPadding(130, 0, 130, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addModel ( String a, String b) {
        ArrayList<String> aux = new ArrayList<>();
        aux.add(a);
        aux.add(b);
        attributes.add(aux);
        //adapter.notifyDataSetChanged();
    }

    public void addModel ( Model m ) {
        models.add(m);
        //adapter.notifyDataSetChanged();
    }

    private void setViewPagerUp (View view) {
        ImageView planograma = view.findViewById(R.id.imgPlanograma);
        TextView txtPasillo = view.findViewById(R.id.txtPasillo);
        TextView txtRack = view.findViewById(R.id.txtRack);

        // Creacion de card views
        models = new ArrayList<>();
        String query = "select * from producto;";
        Database.query(getContext(), query, this);

        adapter = new Adapter(models, getContext());
        /*
        viewPager =  (ViewPager) view.findViewById(R.id.productsPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);
         */

        planograma.setImageResource(R.drawable.planograma_1_7);
        txtPasillo.setText("A");
        txtRack.setText("2");

        // TODO: Cambiar por metodo no depreciado
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        planograma.setImageResource(R.drawable.planograma_1_7);
                        txtPasillo.setText("A");
                        txtRack.setText("2");
                        break;
                    case 1:
                        planograma.setImageResource(R.drawable.planograma_2_8);
                        txtPasillo.setText("A");
                        txtRack.setText("2");
                        break;
                    case 2:
                        planograma.setImageResource(R.drawable.planograma_1_7);
                        txtPasillo.setText("A");
                        txtRack.setText("3");
                        break;
                    default:
                        planograma.setImageResource(R.drawable.planograma);
                        txtPasillo.setText("");
                        txtRack.setText("");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void escanear () {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity());
        integrator.setCaptureActivity(CapturaAuxiliar.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt("Escaneando código de barras");
        integrator.initiateScan();
    }
}