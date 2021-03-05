package com.example.pickingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import  android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConfigFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        RadioGroup radioGroup =(RadioGroup) view.findViewById(R.id.pref_escaner);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences preferences = getContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if (checkedId == R.id.escaner){
                    editor.putString("escaneo_preferido", "escaner");
                }
                else {
                    editor.putString("escaneo_preferido", "camara");
                }
                editor.apply();
            }
        });

        SharedPreferences preferences = getContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        if(preferences.getString("escaneo_preferido", "escaner").equals("escaner")){
            radioGroup.check(R.id.escaner);
        }
        else{
            radioGroup.check(R.id.camara);
        }

        return view;
    }
}
