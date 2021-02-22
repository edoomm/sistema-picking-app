package com.example.pickingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

// Clase adaptadora para la lista expandible
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> titulosLista;
    private HashMap<String, DetalleTutorial> elementosSublista;

    public CustomExpandableListAdapter(Context context, List<String> titulosLista, HashMap<String, DetalleTutorial> elementosSublista) {
        this.context = context;
        this.titulosLista = titulosLista;
        this.elementosSublista = elementosSublista;
    }

    @Override
    public int getGroupCount() {
        return this.titulosLista.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // Cada elemento de la lista solo tiene un elemento hijo
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.titulosLista.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.elementosSublista.get(this.titulosLista.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String titulo = (String) getGroup(groupPosition);
        DetalleTutorial tutorial = (DetalleTutorial) getChild(groupPosition, 0);

        if ( convertView == null ) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_tutorial_group, null);
        }

        TextView textView = convertView.findViewById(R.id.titulo);

        textView.setText(titulo);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        DetalleTutorial tutorial = (DetalleTutorial) getChild(groupPosition, childPosition);


        if ( convertView == null ) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_sublist_tutorial_item, null);

            List<String> instrucciones = tutorial.getInstrucciones();
            LinearLayout linearLayoutInstrucciones = convertView.findViewById(R.id.instrucciones);

            if ( !tutorial.isUsado() ) {
                for ( String instruccion : instrucciones ) {
                    TextView textViewInstruccion = new TextView(context);
                    textViewInstruccion.setPadding(40, 20, 40, 20);
                    textViewInstruccion.setTextColor(Color.parseColor("#000000"));
                    textViewInstruccion.setTextSize(18);
                    textViewInstruccion.setText(instruccion);
                    linearLayoutInstrucciones.addView(textViewInstruccion);
                }
                tutorial.setUsado(true);
            }

            GifImageView gifImageView = convertView.findViewById(R.id.gif_container);
            gifImageView.setPadding(0,20,0,30);
            gifImageView.setImageResource(tutorial.getGif());
        }

        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), tutorial.getGif());
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.start();

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
