package com.example.pickingapp;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;

public class ModelListItemAdapter extends ArrayAdapter<ModelListItem> {
	public ModelListItemAdapter(Context context, ArrayList<ModelListItem> items){
		super(context, 0, items);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ModelListItem item = getItem(position);
		if(convertView == null)
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_contenedor, parent, false);
		TextView sucursal = convertView.findViewById(R.id.sucursal);
		TextView contenedor = convertView.findViewById(R.id.contenedor);
		ImageView icon = convertView.findViewById(R.id.checked_icon);
		sucursal.setText(item.getSucursal());
		contenedor.setText(item.getContenedor());
		if(item.getChecked())
			icon.setVisibility(View.VISIBLE);
		else
			icon.setVisibility(View.INVISIBLE);
		return convertView;
	}
}
