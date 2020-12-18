package com.example.pickingapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pickingapp.R;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	private PageViewModel pageViewModel;

	public static PlaceholderFragment newInstance(int index) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_SECTION_NUMBER, index);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
		int index = 1;
		if (getArguments() != null) {
			index = getArguments().getInt(ARG_SECTION_NUMBER);
		}
		pageViewModel.setIndex(index);
	}

	@Override
	public View onCreateView(
			@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_lista, container, false);
		final LinearLayout linearLayout = root.findViewById(R.id.lista);
		pageViewModel.getList().observe(this, new Observer<List<String>>() {
			@Override
			public void onChanged(List<String> strings) {
				for(String item:strings){
					TextView text = new TextView(getContext());
					text.setText(item);
					text.setPadding(10,10,10,10);
					text.setTextSize(30);
					linearLayout.addView(text);
				}
			}
		});
		return root;
	}
}