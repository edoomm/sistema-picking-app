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
import java.util.Vector;

import static android.app.Activity.RESULT_CANCELED;

public class PickUpFragment extends Fragment {

    private ViewPager viewPager;
    private Adapter adapter;
    private ArrayList<Model> models;
    private Vector<InformacionProducto> productos;
    private int estado; // 0 : Escanear producto, 1 : Escanear Contenedor asignado, 2 : Asignar contenedor
    private String numEmpleado;
    private Context context;
    private View view;

    void configurarBotones () {
        // btnLista
        Button btnLista = view.findViewById(R.id.button_lista);
        btnLista.setOnClickListener( new View.OnClickListener(){
                                         @Override
                                         public void onClick(View view) {
                                             Intent intent = new Intent(view.getContext(), Lista.class);
                                             intent.putExtra("InformacionProductos", productos);
                                             startActivity(intent);
                                         }
                                     }
        );

        //btnEscaneo
        Button btnEscaneo = view.findViewById(R.id.button_scan);
        btnEscaneo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                if(preferences.getString("escaneo_preferido", "escaner").equals("escaner")){
                    Intent intent = new Intent(view.getContext(), Escaneo.class);
                    startActivity(intent);
                } else {
                    escanear_codigo(view);
                }
            }
        });

        // buttonFinalizarPicking
        Button btnFinPicking = view.findViewById(R.id.button_finalizar_picking);
        btnFinPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for ( int i = 0 ; i < productos.size() ; i++ ) {
                    if ( productos.get(i).getEstado() == 0 ) {
                        Toast.makeText(
                                view.getContext(),
                                "Aún falta recolectar: " + productos.get(i).getDescripcion(),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    } else {
                        // Función para generar la remesa
                    }
                }
            }
        });

        // buttonProductoFaltante
        Button btnProductoFaltante = view.findViewById(R.id.button_producto_faltante);
        btnProductoFaltante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewPager.getCurrentItem();
                InformacionProducto producto = productos.get(index);
                int estado_actual_producto = producto.getEstado();
                if ( estado_actual_producto == 0 ) {
                    producto.setEstado(2);
                    Toast.makeText(getContext(), "Producto reportado y notificado al líder de almacén.", Toast.LENGTH_LONG ).show();
                    generar_transaccion(producto);
                } else if ( estado_actual_producto == 1 ) {
                    Toast.makeText(getContext(), "El producto ya ha sido recolectado.", Toast.LENGTH_LONG ).show();
                } else {
                    Toast.makeText(getContext(), "El producto ya ha sido marcado como faltante.", Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pick_up, container, false);
        context = view.getContext();
        // Obtenemos el número de empleado del operador
        SharedPreferences preferences = view.getContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        numEmpleado = preferences.getString("num_empleado", "");

        // Obtenemos la información del picking desde la base de datos
        String query = "select c.control_id, c.sku, c.apartado, c.id_sucursal, p.descripcion, u.pasillo, u.rack, u.columna, u.nivel, ohc.contenedor_id from control as c inner join operador_has_control as ohc on c.control_id = ohc.control_id inner join producto as p on p.sku = c.sku inner join ubicacion as u on u.sku = p.sku where ohc.num_empleado = \"" + numEmpleado + "\" and ohc.control_id not in (select control_id from transaccion where cantidad != 0) order by ohc.prioridad;";
        Database.query(getContext(), query, new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                try {
                    setProductInfo(response);
                    models = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject producto = response.getJSONObject(i);
                        String sku = producto.getString("sku");
                        String descripcion = producto.getString("descripcion");
                        models.add(new Model("SKU: " + sku, "Descripción: " + descripcion, "A.01.01.02"));
                    }
                    // Cargamos los productos a el ViewPager
                    setViewPagerUp();
                    // configuramos los botones
                    configurarBotones();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // Estado de recolección
        // 0: ningún producto ha sido escaneado
        // 1: se escaneó un producto y se debe escanear ahora la caja asignada
        // 2: se debe asignar el contenedor
        estado = 0;

        // Inicializamos vector de informacion
        productos = new Vector<>();

        return view;
    }

    private void pasar_a_siguiente_item() {
        int index_seleccionado = viewPager.getCurrentItem();
        viewPager.setCurrentItem(index_seleccionado + 1);
    }

    private void generar_transaccion(InformacionProducto producto) {
        int contenedor = producto.getContenedor();
        int sku = producto.getSku();
        int control_id = producto.getControl_id();
        int cantidad = producto.getApartadoGlobal() * -1;
        if ( producto.getEstado() == 2 ) {
            cantidad = 0;
        }
        String query = "insert into transaccion values (null, \""+numEmpleado+"\", "+contenedor+", "+sku+", "+control_id+", NOW(), \"P\", "+cantidad+");";

        Database.query(getContext(), query, new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                Toast.makeText(getContext(), "Transacción realizada con éxito.", Toast.LENGTH_LONG ).show();
            }
        });
    }

    // Guardamos la información del servidor en el vector productos
    private void setProductInfo( JSONArray info ) {
        try {
            for ( int i = 0 ; i < info.length() ; i++ ) {
                JSONObject informacion_modelo = info.getJSONObject(i);
                productos.add(new InformacionProducto(informacion_modelo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escanear_codigo ( View v ) {
        escanear();
    }


    private void setViewPagerUp () {
        // Inicializamos atributos/variables
        viewPager =  (ViewPager) view.findViewById(R.id.productsPager);
        ImageView planograma = view.findViewById(R.id.imgPlanograma);
        TextView txtPasillo = view.findViewById(R.id.textPasillo);
        TextView txtRack = view.findViewById(R.id.textRack);


        adapter = new Adapter(models, getContext());
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        SeleccionadorPlanograma seleccionador = new SeleccionadorPlanograma();
        InformacionProducto producto = productos.get(0);
        txtPasillo.setText("Pasillo: " + producto.getPasillo());
        txtRack.setText("Rack: " + producto.getRack());
        planograma.setImageResource(seleccionador.getDrawable(context, producto.getColumna(), producto.getNivel()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                SeleccionadorPlanograma seleccionador = new SeleccionadorPlanograma();
                InformacionProducto producto = productos.get(position);
                txtPasillo.setText("Pasillo: " + producto.getPasillo());
                txtRack.setText("Rack: " + producto.getRack());
                planograma.setImageResource(seleccionador.getDrawable(context, producto.getColumna(), producto.getNivel()));
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        // Obtenemos el index de la página seleccionada
        int index = viewPager.getCurrentItem();
        if ( scanningResult.getContents() != null ) {
            if ( estado == 0 ) { // Aún no se ha escaneado ningún producto
                try {
                    InformacionProducto producto = productos.get(index);
                    int sku = producto.getSku();
                    String  sku_escaneado = scanningResult.getContents();
                    if ( String.valueOf(sku).equals(sku_escaneado) ) {
                        // indicamos que hay un producto escaneado
                        estado = 1;

                        Toast.makeText(getContext(),"Escanee el contenedor " + producto.getContenedor(), Toast.LENGTH_SHORT).show();
                        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
                        integrator.setOrientationLocked(true);
                        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
                        integrator.setCaptureActivity(CapturaAuxiliar.class);
                        integrator.setPrompt("Escanee el contenedor " + producto.getContenedor());
                        integrator.initiateScan();
                    } else {
                        Toast.makeText(getContext(), "Por favor, escanea el producto: " + producto.getDescripcion(), Toast.LENGTH_SHORT).show();
                        escanear();
                    }
                } catch ( Exception e ) {
                    Toast.makeText(getContext(), e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
                }
            } else if ( estado == 1 ) { // Ya escaneamos un producto, debemos ponerlo en el contenedor
                try {
                    InformacionProducto producto = productos.get(index);
                    String contenedor = String.valueOf(producto.getContenedor());
                    String contenedor_escaneado = scanningResult.getContents();
                    if ( contenedor.equals(contenedor_escaneado) ) { // El contenedor escaneado es el asignado
                        // Volvemos al estado inicial
                        estado = 0;
                        producto.decrementarApartado();
                        productos.set(index, producto);
                        Toast.makeText(getContext(),"Restantes: " + producto.getApartado(), Toast.LENGTH_SHORT).show();
                        if ( producto.hasApartado() ) {
                            escanear();
                        } else {
                            producto.setEstado(1);
                            generar_transaccion(producto);
                            pasar_a_siguiente_item();
                        }
                    } else {
                        Toast.makeText(getContext(),"Escanee el contenedor " + producto.getContenedor(), Toast.LENGTH_SHORT).show();
                        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
                        integrator.setOrientationLocked(true);
                        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
                        integrator.setCaptureActivity(CapturaAuxiliar.class);
                        integrator.setPrompt("Escanee el contenedor " + producto.getContenedor());
                        integrator.initiateScan();
                    }
                } catch ( Exception e ) {
                    Toast.makeText(getContext(), "Error: PickupFragment OnActivityResultException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                // Escaneamos el contenedor asignado a el control y se realiza el proceso de decrementar por uno
                try {
                    InformacionProducto producto = productos.get(index);
                    String contenedor_escaneado = scanningResult.getContents();
                    if ( !contenedor_escaneado.equals("1") ) { // El contenedor escaneado es diferente al 1
                        // Volvemos al estado inicial
                        estado = 0;
                        producto.decrementarApartado();
                        productos.set(index, producto);
                        Toast.makeText(getContext(),"Restantes: " + producto.getApartado(), Toast.LENGTH_SHORT).show();
                        if ( producto.hasApartado() ) {
                            escanear();
                        } else {
                            producto.setEstado(1);
                            generar_transaccion(producto);
                            pasar_a_siguiente_item();
                        }
                    } else {
                        Toast.makeText(getContext(),"Escanee el contenedor " + producto.getContenedor(), Toast.LENGTH_SHORT).show();
                        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
                        integrator.setOrientationLocked(true);
                        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
                        integrator.setCaptureActivity(CapturaAuxiliar.class);
                        integrator.setPrompt("Escanee el contenedor " + producto.getContenedor());
                        integrator.initiateScan();
                    }
                } catch ( Exception e ) {
                    Toast.makeText(getContext(), "Error: PickupFragment OnActivityResultException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void escanear () {
        int index_producto = viewPager.getCurrentItem();
        InformacionProducto producto = productos.get(index_producto);
        if ( producto.hasApartado() ) {
            IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
            integrator.setOrientationLocked(true);
            integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
            integrator.setCaptureActivity(CapturaAuxiliar.class);
            integrator.setPrompt("Escanee el producto" + producto.getSku());
            integrator.initiateScan();
        } else {
            Toast.makeText(getContext(), "Este producto ya ha sido recolectado.", Toast.LENGTH_SHORT).show();
        }
    }
}