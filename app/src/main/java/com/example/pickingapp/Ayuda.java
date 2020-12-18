package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Ayuda extends AppCompatActivity  {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_help);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu_pickup, new AyudaFragment()).commit();
    }



}