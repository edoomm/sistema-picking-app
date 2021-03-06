package com.example.pickingapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Clase que sirve como adaptadora para mostrar la información que sera desplegada en los CardViews
 */

public class Adapter extends PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item, container, false);

        TextView title, desc, cant, sucursal;

        title = view.findViewById(R.id.txtSku);
        desc = view.findViewById(R.id.txtDesc);
        cant = view.findViewById(R.id.txtCant);
        sucursal = view.findViewById(R.id.txtSucursal);

        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDesc());
        cant.setText(models.get(position).getCant());
        sucursal.setText(models.get(position).getSucursal());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
