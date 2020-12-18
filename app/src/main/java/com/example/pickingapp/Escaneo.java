package com.example.pickingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Escaneo extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escaneo);
		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_pickup);
		bottomNavigationView.setSelectedItemId(R.id.nav_pickup);
		bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
	}

	private  BottomNavigationView.OnNavigationItemSelectedListener navListener =
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					switch (item.getItemId()) {
						case R.id.nav_inicio:
							Intent intent = new Intent(getBaseContext(), Menu.class);
							startActivity(intent);
							break;
						case R.id.nav_pickup:
							Intent intent1 = new Intent(getBaseContext(), PickUp.class);
							intent1.putExtra("firstFragment", "PickUpFragment");
							intent1.putExtra("secondFragment", "none");
							startActivity(intent1);
							break;
						case R.id.nav_almacen:
							Intent intent2 = new Intent(getBaseContext(), PickUp.class);
							intent2.putExtra("firstFragment", "none");
							intent2.putExtra("secondFragment", "AlmacenFragment");
							startActivity(intent2);
							break;
						case R.id.nav_cambio:
							// TODO: Popup - Y cambiar a la modalidad surte almacen
							Toast.makeText(getApplicationContext(), "Aca debe ir un popup explicando el surte almacen y cambiando a esa modalidad", Toast.LENGTH_SHORT).show();
							break;
						case R.id.nav_config:
							Intent intent3 = new Intent(getBaseContext(), Configuracion.class);
							startActivity(intent3);
							break;
					}
					return false;
				}
			};
}