package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maciej on 28.06.2017.
 */

public class Kurier_Trasa_SzczegolyAdresu_DodajSkanTorby_Request extends StringRequest{

    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/torbySkan";
    private Map<String, String> params, headers;


    public Kurier_Trasa_SzczegolyAdresu_DodajSkanTorby_Request(Context c, String torbaId, String adresId, String trasa, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST, URL, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);

        params = new HashMap<>();
        params.put("torbaId", torbaId);
        params.put("adresId", adresId);
        params.put("trasa", trasa);

    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
