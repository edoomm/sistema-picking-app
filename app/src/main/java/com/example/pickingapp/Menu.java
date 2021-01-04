package com.example.pickingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Menu extends AppCompatActivity implements BottomNavigationView.OnNavigationItemReselectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Finding view to be shown
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);
        // Place selected view in current Screen
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu, new MenuFragment()).commit();
        // Listeners for navigation menu
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
    }
    public void salir () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("¿Seguro que desea salir?");
        confirmacion.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                salir();
                // finish();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = confirmacion.create();
        dialog.show();

        //Toast.makeText(getApplicationContext(), "Hasta pronto", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("¿Seguro que desea salir?");
        confirmacion.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                salir();
                // finish();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = confirmacion.create();
        dialog.show();
    }
}