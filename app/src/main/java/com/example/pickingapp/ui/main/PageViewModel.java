package com.example.pickingapp.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageViewModel extends ViewModel {

	private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
	private LiveData<List<String>> lista_apartados = Transformations.map(mIndex, new Function<Integer, List<String>>() {
		@Override
		public List<String> apply(Integer input) {
			switch (input){
				case 1:
					return Arrays.asList("12345", "12346", "12347");
				case 2:
					return Arrays.asList("12349", "12350", "12351");
				case 3:
					return Arrays.asList("12345", "12346", "12347","12349", "12350", "12351");
				default:
					return Arrays.asList();
			}
		}
	});

	public void setIndex(int index) {
		mIndex.setValue(index);
	}

	public LiveData<List<String>> getList() {
		return lista_apartados;
	}
}