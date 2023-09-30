package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maciej on 16.09.2017.
 */

public class Kurier_Trasa_SzczegolyAdresu_DodajSzacunek_Request extends StringRequest {

    // private static final String url = "https://rhb.lightboxgdansk.pl:51443/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/Adres/";
    private static String url = "";
    Map<String, String> params, headers;



    public Kurier_Trasa_SzczegolyAdresu_DodajSzacunek_Request(Context c, String szacunekDostawy , String trasa, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.PUT, url, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);

        params = new HashMap<>();
        params.put("szacunekDostawy", szacunekDostawy);
        params.put("trasa", trasa);

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    public static void urlSet(String urlId){
        url=urlId;
    }

}

