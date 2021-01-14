package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

// Toast.makeText(getContext(), "Toast para debug", Toast.LENGTH_LONG ).show();

public class PickUpFragment extends Fragment {

    private ViewPager viewPager;
    private Adapter adapter;
    private ArrayList<Model> models;
    private ArrayList<InformacionProducto> productos;
    private int estado; // 0 : Escanear producto, 1 : Escanear Contenedor asignado, 2 : Asignar contenedor
    private String numEmpleado;
    private Context context;
    private View view;

    // Sonidos para el escaneo
    private MediaPlayer successSound;
    private MediaPlayer errorSound;

    private int getIndex ( int indiceActual ) {
        int index = viewPager.getCurrentItem();
        // Obtenemos el model elegido en la lista horizontal
        Model m = models.get(index);
        // Obtenemos el sku del model
        int value = obtenerSkuDeString(m.getTitle());
        index = getSiguienteProductoAEscanear(value);
        //Toast.makeText(getContext(), "SKU: " + value + " Index: " + index, Toast.LENGTH_LONG ).show();
        Log.i("M", "SKU: " + value + " Index: " + index + " Size: " + models.size());
        return index;
    }

    void configurarBotones () {
        // btnLista
        Button btnLista = view.findViewById(R.id.button_lista);
        btnLista.setOnClickListener( new View.OnClickListener(){
                                         @Override
                                         public void onClick(View view) {
                                             Intent intent = new Intent(view.getContext(), Lista.class);
                                             ProductInformationSingleton.getProductInformation().setProductos(productos);
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
                    int index = getIndex(viewPager.getCurrentItem());
                    InformacionProducto producto = productos.get(index);
                    if ( producto.hasApartado() ) {
                        escanear_codigo(view);
                    }
                    else {
                        Toast.makeText(getContext(), "El producto ya ha sido recolectado.", Toast.LENGTH_LONG ).show();
                    }
                }
            }
        });


        // buttonProductoFaltante
        Button btnProductoFaltante = view.findViewById(R.id.button_producto_faltante);
        btnProductoFaltante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getIndex(viewPager.getCurrentItem());
                InformacionProducto producto = productos.get(index);
                int estado_actual_producto = producto.getEstado();
                if ( estado_actual_producto == 0 ) {
                    producto.setEstado(2);
                    int contenedor = producto.getContenedor();
                    int sku = producto.getSku();
                    int control_id = producto.getControl_id();
                    int cantidad = 0;
                    String query = "insert into transaccion values (null, \""+numEmpleado+"\", "+contenedor+", "+sku+", "+control_id+", NOW(), \"P\", "+cantidad+");";
                    Database.insert(getContext(), query);
                    Toast.makeText(getContext(), "Producto reportado y notificado al líder de almacén.", Toast.LENGTH_LONG ).show();
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

        // Inicializamos atributos peligrosos
        productos = new ArrayList<>();
        models = new ArrayList<>();

        // Inicializamos sonidos
        successSound = MediaPlayer.create(getContext(), R.raw.success);
        errorSound = MediaPlayer.create(getContext(), R.raw.error);

        verificarInformacion();

        // Estado de recolección
        // 0: ningún producto ha sido escaneado
        // 1: se escaneó un producto y se debe escanear ahora la caja asignada
        // 2: se debe asignar el contenedor
        estado = 0;

        return view;
    }

    private void verificarInformacion() {
        // Obtenemos la información del picking desde la base de datos
        String query = "select c.control_id, c.sku, c.apartado, c.id_sucursal, p.descripcion, u.pasillo, u.rack, u.columna, u.nivel, ohc.contenedor_id, ohc.prioridad from control as c inner join operador_has_control as ohc on c.control_id = ohc.control_id inner join producto as p on p.sku = c.sku inner join ubicacion as u on u.sku = p.sku where ohc.num_empleado = \""+numEmpleado+"\" and ohc.control_id not in (select control_id from transaccion where cantidad != 0) and (c.asignado = 1) and (ohc.contenedor_id is not null) order by ohc.prioridad;";

        Database.query(getContext(), query, new VolleyCallback() {
            @Override
            public void onSucces(JSONArray response) {
                try {
                    Log.i("PickUp", response.toString());
                    setProductInfo(response);
                    // Cargamos los productos a el ViewPager
                    setViewPagerUp();
                    // configuramos los botones
                    configurarBotones();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void pasar_a_siguiente_item() {
        int index_seleccionado = viewPager.getCurrentItem();
        //Model actual_model = models.get(index_seleccionado);
        //actual_model.setTitle(actual_model.getTitle()+"\n(Ya se ha recolectado)");
        //models.set(index_seleccionado, actual_model);
        if ( index_seleccionado + 1 < models.size() ) {
            viewPager.setCurrentItem(index_seleccionado + 1);
        }
    }

    private void generar_transaccion(InformacionProducto producto) {
        int contenedor = producto.getContenedor();
        int sku = producto.getSku();
        int control_id = producto.getControl_id();
        int cantidad = producto.getApartadoGlobal() * -1;
        String query;
        if ( producto.getEstado() == 2 ) {
            query = "update transaccion set cantidad = "+cantidad+" where control_id = "+control_id+";";
        } else {
            query = "insert into transaccion values (null, \""+numEmpleado+"\", "+contenedor+", "+sku+", "+control_id+", NOW(), \"P\", "+cantidad+");";
        }
        producto.setEstado(1);
        int index = getIndex(viewPager.getCurrentItem());
        productos.set(index, producto);
        Database.insert(getContext(), query);
        Toast.makeText(getContext(), "Transacción realizada exitosamente.", Toast.LENGTH_LONG ).show();
    }

    private boolean contieneA ( InformacionProducto p ) {
        for ( int i = 0 ; i < productos.size() ; i++ ) {
            if ( p.getControl_id() == productos.get(i).getControl_id() ) {
                return true;
            }
        }
        return false;
    }

    private boolean contieneA ( Model m ) {
        for ( int i = 0 ; i < models.size() ; i++ ) {
            if ( m.getTitle().equals(models.get(i).getTitle()) ) {
                return true;
            }
        }
        return false;
    }

    // Guardamos la información del servidor en el vector productos
    private void setProductInfo( JSONArray info ) {
        try {
            for ( int i = 0 ; i < info.length() ; i++ ) {
                JSONObject informacion_modelo = info.getJSONObject(i);
                if ( !contieneA(new InformacionProducto(informacion_modelo)) ) {
                    productos.add(new InformacionProducto(informacion_modelo));
                    String sku = informacion_modelo.getString("sku");
                    String descripcion = informacion_modelo.getString("descripcion");
                    if ( !contieneA (new Model("SKU: " + sku, "", "")) ) {
                        models.add(new Model("SKU: " + sku, "Descripción: " + descripcion, "A.01.01.02"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ( ProductInformationSingleton.getProductInformation() == null ) {
            ProductInformationSingleton.getProductInformation(productos, models);
        } else {
            ProductInformationSingleton.getProductInformation().setProductos(productos);
            ProductInformationSingleton.getProductInformation().setModels(models);
        }

    }

    private int obtenerSkuDeString ( String s) {
        // SKU:_
        // 01234
        String value = s.substring(5, s.length());
        return Integer.parseInt(value);
    }

    private int getSiguienteProductoAEscanear ( int sku ) {
        int indice_siguiente_a_escanear = -1;
        for ( int i = 0 ; i < productos.size() ; i++ ) {
            InformacionProducto pr = productos.get(i);
            if ( pr.getSku() == sku ) {
                indice_siguiente_a_escanear = i;
                if ( pr.hasApartado() ) {
                    return i;
                }
            }
        }
        return indice_siguiente_a_escanear;
    }

    public void escanear_codigo ( View v ) {
        int index_producto = getIndex(viewPager.getCurrentItem());
        InformacionProducto producto = productos.get(index_producto);
        if ( producto.hasApartado() ) {
            escanear("Escanee el producto: " + producto.getSku());
        } else {
            Toast.makeText(getContext(), "Este producto ya ha sido recolectado.", Toast.LENGTH_SHORT).show();
        }

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

        if (productos.size() > 0) {
            InformacionProducto producto = productos.get(0);
            txtPasillo.setText("Pasillo: " + producto.getPasillo());
            txtRack.setText("Rack: " + producto.getRack());
            planograma.setImageResource(SeleccionadorPlanograma.getDrawable(context, producto.getColumna(), producto.getNivel()));
        }
        else {
            planograma.setImageResource(R.drawable.planograma);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                SeleccionadorPlanograma seleccionador = new SeleccionadorPlanograma();
                InformacionProducto producto = productos.get(getIndex(position));
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
        int index = getIndex(viewPager.getCurrentItem());
        InformacionProducto producto = productos.get(index);
        if ( scanningResult.getContents() != null ) {
            if ( estado == 0 ) { // Aún no se ha escaneado ningún producto
                int sku = producto.getSku();
                String  sku_escaneado = scanningResult.getContents();
                if ( String.valueOf(sku).equals(sku_escaneado) ) {
                    successSound.start();
                    // indicamos que hay un producto escaneado
                    estado = 1;
                    Toast.makeText(getContext(),"Escanee el contenedor " + producto.getContenedor(), Toast.LENGTH_SHORT).show();
                    escanear("Escanee el contenedor " + producto.getContenedor());
                } else {
                    errorSound.start();
                    Toast.makeText(getContext(), "Por favor, escanea el producto: " + producto.getDescripcion(), Toast.LENGTH_SHORT).show();
                    escanear("Escanee el producto: " + producto.getSku());
                }
            } else if ( estado == 1 ) { // Ya escaneamos un producto, debemos ponerlo en el contenedor
                String contenedor = String.valueOf(producto.getContenedor());
                String contenedor_escaneado = scanningResult.getContents();
                if ( contenedor.equals(contenedor_escaneado) ) { // El contenedor escaneado es el asignado
                    // Volvemos al estado inicial
                    estado = 0;
                    producto.decrementarApartado();
                    productos.set(index, producto);
                    Toast.makeText(getContext(),"Restantes: " + producto.getApartado(), Toast.LENGTH_SHORT).show();
                    successSound.start();
                    if ( producto.hasApartado() ) {
                        escanear();
                    } else {
                        generar_transaccion(producto);
                        pasar_a_siguiente_item();
                    }
                } else {
                    errorSound.start();
                    Toast.makeText(getContext(),"Escanee el contenedor " + producto.getContenedor(), Toast.LENGTH_SHORT).show();
                    escanear("Escanee el contenedor " + producto.getContenedor());
                }
            }
        }
    }


    private void escanear () {
        int index = getIndex(viewPager.getCurrentItem());
        InformacionProducto producto = productos.get(index);
        escanear("Escanee el producto: " + producto.getSku());
    }

    private void escanear (String mensaje) {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setCaptureActivity(CapturaAuxiliar.class);
        integrator.setBeepEnabled(false);
        integrator.setPrompt(mensaje);
        integrator.initiateScan();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_pickup, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.actulizar_control:
                verificarInformacion();
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();
                Toast.makeText(getContext(), "El control está actualizado", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.asignar_contenedores:
                Toast.makeText(getContext(), "Verificando contenedores...", Toast.LENGTH_SHORT).show();
                ((PickUp)getActivity()).validarContenedores();
                return true;
            case R.id.ver_contenedores:
                startActivity(new Intent(getActivity(), Contenedor.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}