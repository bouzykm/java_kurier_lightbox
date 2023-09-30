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

public class Koordynator_Prognoza_SzczegolyAdresuPrognozy_DodajUwage_Request extends StringRequest{

    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/prognozaUwaga";
    private Map<String, String> params, headers;


    public Koordynator_Prognoza_SzczegolyAdresuPrognozy_DodajUwage_Request(Context c, String adresId, String trasa, String uwaga, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST, URL, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);

        params = new HashMap<>();
        params.put("adresId", adresId);
        params.put("trasa", trasa);
        params.put("uwaga", uwaga);
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
