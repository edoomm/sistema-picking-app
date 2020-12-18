package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Configuracion extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_config);
		bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

		bottomNavigationView.setSelectedItemId(R.id.nav_settings);

		// Deciding which fragment will show first
		Fragment firstFragment = new ConfigFragment();
//		Intent intent = getIntent();
//		if (!intent.getStringExtra("secondFragment").equals("none")) {
//			firstFragment = new ConfigFragment();
//			bottomNavigationView.setSelectedItemId(R.id.nav_config);
//			// TODO: Cambiar el titulo del actionbar (PickUp)
//		}

//		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_menu_pickup, firstFragment).commit();
	}

	private  BottomNavigationView.OnNavigationItemSelectedListener navListener =
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					Fragment selectedFragment = null;

					switch (item.getItemId()) {
						case R.id.nav_home:
							Intent intent = new Intent(getBaseContext(), Menu.class);
							startActivity(intent);
							return false;
							// Ninguno tiene break porque no hay fragments
						case R.id.nav_settings:
							selectedFragment = new ConfigFragment();
							// TODO: Cambiar titulo action bar
							break;
						case R.id.nav_tutorial:
							// TODO: Empezar fragment tutorial
						case R.id.nav_faq:
							// TODO: Empezar fragment FAQ
							// TODO: Cuando se empiece este debe aparecer en activity_configuracion.xml un boton flotante que servira para mandar una nueva pregunta
						default:
							return false;
					}

					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_config, selectedFragment).commit();

					return true;
				}
			};
}