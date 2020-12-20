package com.example.pickingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class AlmacenFragment extends Fragment{

    ListView search;
    ArrayAdapter<String> adapter;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_almacen, container, false);

        ArrayList<String> sku_array = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.lista_SKU_ejemplo)));

        adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                sku_array
        );

        search = view.findViewById(R.id.SKU_list_view);
        search.setAdapter(adapter);
        SearchView searchView = view.findViewById(R.id.SKU_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return view;
    }
}
