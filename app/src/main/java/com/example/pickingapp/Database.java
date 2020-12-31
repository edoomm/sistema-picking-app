package com.example.pickingapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Database {

	 public static void query(Context context, String query, final VolleyCallback callback){
		String url = URLs.URL_QUERY;
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONArray jsonArray = null;
						try {
							jsonArray = new JSONArray(response);
							callback.onSucces(jsonArray);
						} catch (JSONException e) {
							callback.onSucces(null);
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
						Toast.makeText(context, "Contactese con su líder de almacén para informale del error", Toast.LENGTH_LONG).show();
					}
				}){
			@Override
			protected Map<String, String> getParams(){
				Map<String, String> params = new HashMap<>();
				params.put("query", query);
				return params;
			}
		};
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(stringRequest);
	}
}
