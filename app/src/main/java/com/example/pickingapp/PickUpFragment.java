package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import static android.app.Activity.RESULT_CANCELED;

public class PickUpFragment extends Fragment {

    private ViewPager viewPager;
    private Adapter adapter;
    private List<Model> models;
    private JSONArray productInfo;
    private Boolean state;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_up, container, false);
        state = false;
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

    private void setProductInfo( JSONArray info ) {
        productInfo = info;
        /*
        try {
            for (int i = 0; i < productInfo.length(); i++) {
                JSONObject producto = productInfo.getJSONObject(i);
                String sku = producto.getString("sku");
                String descripcion = producto.getString("descripcion");
                Toast.makeText(getContext(), "SKU: " + sku + "Descripción: " + descripcion, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }

    public void escanear_codigo ( View v ) {
        escanear();
    }


    private void setViewPagerUp (View view) {
        ImageView planograma = view.findViewById(R.id.imgPlanograma);
        TextView txtPasillo = view.findViewById(R.id.txtPasillo);
        TextView txtRack = view.findViewById(R.id.txtRack);

        String query = "select c.sku, c.apartado, c.id_sucursal, p.descripcion, u.pasillo, u.rack, ohc.contenedor_id from control as c right join producto as p on p.sku = c.sku inner join ubicacion as u on u.sku = p.sku inner join operador_has_control as ohc on c.control_id = ohc.control_id where ohc.num_empleado = \"111111\";";
        Database.query(getContext(), query, new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                try {
                    models = new ArrayList<>();
                    setProductInfo(response);
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
        });

        planograma.setImageResource(R.drawable.planograma_1_7);
        txtPasillo.setText("A");
        txtRack.setText("2");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*
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
                 */
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if ( scanningResult.getContents() != null ) {
            if ( !state ) { // Aún no se ha escaneado ningún producto
                try {
                    int index = viewPager.getCurrentItem();
                    JSONObject model_info = productInfo.getJSONObject(index);
                    String sku = model_info.getString("sku");
                    String scanned_sku = scanningResult.getContents();
                    if ( sku.equals(scanned_sku) ) {
                        state = true;
                        Toast.makeText(getContext(),"Correct product", Toast.LENGTH_SHORT).show();
                        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
                        // use forSupportFragment or forFragment method to use fragments instead of activity
                        integrator.setOrientationLocked(true);
                        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
                        integrator.setCaptureActivity(CapturaAuxiliar.class);
                        integrator.setPrompt("Escanee la caja");
                        integrator.initiateScan();
                    } else {
                        Toast.makeText(getContext(),"Please scan indicated product", Toast.LENGTH_SHORT).show();
                    }
                    /*
                    Toast.makeText(getContext(),
                            model_info.getString("sku") + " " +
                            model_info.getString("descripcion") + " " +
                            model_info.getString("contenedor_id")
                            , Toast.LENGTH_SHORT).show();
                     */
                    //Toast.makeText(getContext(), model.getTitle() + " " + model.getDesc(), Toast.LENGTH_SHORT).show();
                } catch ( Exception e ) {
                    Toast.makeText(getContext(), "Error: PickupFragment OnActivityResultException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                try {
                    int index = viewPager.getCurrentItem();
                    JSONObject model_info = productInfo.getJSONObject(index);
                    String contenedor = model_info.getString("contenedor_id");
                    String scanned_contenedor = scanningResult.getContents();
                    if ( contenedor.equals(scanned_contenedor) ) {
                        state = false;
                        Toast.makeText(getContext(),"Correct container", Toast.LENGTH_SHORT).show();
                    }
                } catch ( Exception e ) {
                    Toast.makeText(getContext(), "Error: PickupFragment OnActivityResultException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void escanear () {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        // use forSupportFragment or forFragment method to use fragments instead of activity
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setCaptureActivity(CapturaAuxiliar.class);
        integrator.setPrompt("Escanee el producto");
        integrator.initiateScan();
    }
}