package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Menu extends AppCompatActivity implements BottomNavigationView.OnNavigationItemReselectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu, new MenuFragment()).commit();

        bottomNavigationView.setOnNavigationItemReselectedListener(this);
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        // TODO: Mensaje de confirmación para salir de la aplicación

        Toast.makeText(getApplicationContext(), "Hasta pronto", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));

        // TODO: Limpiar la navegación para atrás cuando se le de click en **salir** (es decir que no se pueda regresar al menu principal a través del botón **atrás**).
    }
}