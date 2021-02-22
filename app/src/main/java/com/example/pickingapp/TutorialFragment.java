package com.example.pickingapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Atributos para la lista expandible del tutorial
    private Context context;
    private View view;
    private ExpandableListView expandableListViewTutorial;
    private CustomExpandableListAdapter customExpandableListViewAdapter;
    private HashMap<String, DetalleTutorial> tutorial;
    private int ultimaPosicionExpandida;

    public TutorialFragment() {
        // Required empty public constructor
    }

    // Se inicializan los componentes del tutorial
    private void initialize () {
        this.expandableListViewTutorial = view.findViewById(R.id.expandableListView);
        tutorial = getDetalleTutorialHashMap();
        this.customExpandableListViewAdapter = new CustomExpandableListAdapter(context,
                new ArrayList<>(tutorial.keySet()), tutorial);

    }

    private HashMap<String, DetalleTutorial> getDetalleTutorialHashMap () {
        HashMap<String, DetalleTutorial> listaTutorial = new HashMap<>();
        ArrayList<String> pasos = new ArrayList<>();

        pasos.add(" 1.- Dirijase a la sección de Configuración.");
        pasos.add(" 2.- Presione una de las dos opciones: Escáner (selecciona el escáner de mano por defecto), Cámara (Selecciona la cámara del dispositivo móvil).");
        pasos.add(" 3.- Presione el botón \"Sincronización de escáner\"");
        listaTutorial.put("Cambiar método de escaneo", new DetalleTutorial(pasos, R.drawable.tutorial_configuracion));

        pasos.clear();
        pasos.add(" 1.- Dirijase a la sección de Ayuda.");
        pasos.add(" 2.- Presione el botón naranja flotante con el icono de información en la parte inferior derecha de la pantalla.");
        pasos.add(" 3.- Ingrese detalladamente el error o problema encontrado.");
        pasos.add(" 4.- Presione el botón enviar.");
        pasos.add(" 5.- Resolveremos los problemas lo más pronto posible, una vez solucionado puedes dar seguimiento en la sección de seguimiento presionando el botón naranja flotante en la sección de ayuda.");
        listaTutorial.put("Reportar errores en la aplicación", new DetalleTutorial(new ArrayList<>(pasos), R.drawable.tutorial_configuracion));


        return listaTutorial;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TutorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment newInstance(String param1, String param2) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        this.context = view.getContext();

        // Inicialización de la lista expandible del tutorial
        initialize();

        expandableListViewTutorial.setAdapter(customExpandableListViewAdapter);

        expandableListViewTutorial.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(ultimaPosicionExpandida != -1 && groupPosition != ultimaPosicionExpandida){
                    expandableListViewTutorial.collapseGroup(ultimaPosicionExpandida);
                }
                ultimaPosicionExpandida = groupPosition;
            }
        });

        return view;
    }
}