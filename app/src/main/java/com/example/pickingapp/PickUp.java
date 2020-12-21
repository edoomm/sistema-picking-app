package com.example.pickingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
                            selectedFragment = new PickUpFragment();
                            getSupportActionBar().setTitle("Pick Up");
                            break;
                        case R.id.nav_cambio:
                            // TODO: Popup - Y cambiar a la modalidad surte almacen
                            Toast.makeText(getApplicationContext(), "Aca debe ir un popup explicando el surte almacen y cambiando a esa modalidad", Toast.LENGTH_SHORT).show();
                            return false;
                        case R.id.nav_almacen:
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

    // What to do when the camera has scanned something
    @Override
    protected void onActivityResult (int request_code, int result_code, Intent data) {
        // result stores the result of scanned stuff
        if(result_code != RESULT_CANCELED && data != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(request_code, result_code, data);
            Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_SHORT).show();
            super.onActivityResult(request_code, result_code, data);
        }
    }
}