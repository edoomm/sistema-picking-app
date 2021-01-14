package com.example.pickingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;

public class PickUp extends AppCompatActivity {

    Context context;
    AlertDialog dialogContenedores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_pickup);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        context = this;

        // Deciding which fragment will show first
        Fragment firstFragment;
        Intent intent = getIntent();
        if (!intent.getStringExtra("firstFragment").equals("none")) {
            firstFragment = new PickUpFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_pickup);
            getSupportActionBar().setTitle("Pick Up");
        }
        else {
            firstFragment = new AlmacenFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_almacen);
            getSupportActionBar().setTitle("Almacén");
        }
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_inicio:
                            Intent intent = new Intent(getBaseContext(), Menu.class);
                            startActivity(intent);
                            return false;
                        case R.id.nav_pickup:
                            getIntent().putExtra("firstFragment", "PickUpFragment");
                            getIntent().putExtra("secondFragment", "none");
                            validarContenedores();
                            selectedFragment = new PickUpFragment();
                            getSupportActionBar().setTitle("Pick Up");
                            break;
                        case R.id.nav_almacen:
                            getIntent().putExtra("firstFragment", "none");
                            getIntent().putExtra("secondFragment", "AlmacentFragment");
                            selectedFragment = new AlmacenFragment();
                            getSupportActionBar().setTitle("Almacén");
                            break;
                        case R.id.nav_config:
                            Intent intent1 = new Intent(getBaseContext(), Configuracion.class);
                            intent1.putExtra("firstFragment", "none");
                            intent1.putExtra("secondFragment", "ConfigFragment");
                            startActivity(intent1);
                            //Toast.makeText(getApplicationContext(), "Empezar config activity", Toast.LENGTH_SHORT).show();
                            return false;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu_pickup, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        //validarContenedores();
    }

    void validarContenedores(){
        Intent intent = getIntent();
        if (!intent.getStringExtra("firstFragment").equals("none")){
            SharedPreferences preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
            String noEmpleado = preferences.getString("num_empleado", "0");;
            String query = "SELECT DISTINCT c.control_id, c.id_sucursal " +
                    "FROM control AS c " +
                    "INNER JOIN `operador_has_control` AS ohc ON c.control_id = ohc.control_id " +
                    "WHERE ohc.num_empleado = '" + noEmpleado + "' AND ohc.contenedor_id IS NULL  GROUP BY c.id_sucursal";
            Database.query(this, query, new VolleyCallback() {
                @Override
                public void onSucces(JSONArray response) {
                    if(response != null && response.length() > 0) {
                        if(dialogContenedores == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Tiene sucursales sin contenedor, por favor, asigne los contenedores");
                            builder.setPositiveButton("Asignar ahora", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    iniciarContenedorActivity();
                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dialogContenedores = builder.create();
                            dialogContenedores.show();
                        }
                        else {
                            if (!dialogContenedores.isShowing()) {
                                dialogContenedores.show();
                            }
                        }
                    }
                }
            });
        }
    }

    void iniciarContenedorActivity(){
        startActivity(new Intent(this, Contenedor.class));
    }

}