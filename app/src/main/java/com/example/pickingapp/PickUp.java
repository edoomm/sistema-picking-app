package com.example.pickingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class PickUp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_pickup);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Deciding which fragment will show first
        Fragment firstFragment;
        Intent intent = getIntent();
        if (!intent.getStringExtra("firstFragment").equals("none")) {
            firstFragment = new PickUpFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_pickup);
            // TODO: Cambiar el titulo del actionbar (PickUp)
        }
        else {
            firstFragment = new AlmacenFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_almacen);
            // TODO: Cambiar el titulo del actionbar (Almacen)
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu_pickup, firstFragment).commit();
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
                            selectedFragment = new PickUpFragment();
                            // TODO: Cambiar el titulo del actionbar (PickUp)
                            break;
                        case R.id.nav_cambio:
                            // TODO: Popup - Y cambiar a la modalidad surte almacen
                            Toast.makeText(getApplicationContext(), "Aca debe ir un popup explicando el surte almacen y cambiando a esa modalidad", Toast.LENGTH_SHORT).show();
                            return false;
                        case R.id.nav_almacen:
                            selectedFragment = new AlmacenFragment();
                            // TODO: Cambiar el titulo del actionbar (Almacen)
                            break;
                        case R.id.nav_config:
                            // TODO: Popup - Y cambiar a la modalidad surte almacen
                            Toast.makeText(getApplicationContext(), "Empezar config activity", Toast.LENGTH_SHORT).show();
                            return false;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu_pickup, selectedFragment).commit();

                    return true;
                }
            };
    public void escanear_codigo ( View v ) {
        escanear();
    }

    void escanear () {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CapturaAuxiliar.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt("Escaneando código de barras");
        integrator.initiateScan();
    }

    // What to do when the camera has scanned something
    @Override
    protected void onActivityResult (int request_code, int result_code, Intent data) {
        // result stores the result of scanned stuff
        IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
        Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_SHORT).show();
        super.onActivityResult(request_code, result_code, data);
    }
}