package com.example.pickingapp;

import android.content.Context;

public class SeleccionadorPlanograma {
	public static int getDrawable(Context context, int columna, int nivel){
		return context.getResources().getIdentifier("planogram_"+columna+"_"+nivel, "drawable", context.getPackageName());
	}
}
