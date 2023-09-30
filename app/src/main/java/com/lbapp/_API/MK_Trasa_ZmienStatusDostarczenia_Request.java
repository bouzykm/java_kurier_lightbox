package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.lbapp._Global._GlobalVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maciej on 16.09.2017.
 */

public class MK_Trasa_ZmienStatusDostarczenia_Request extends StringRequest {

   // private static final String url = "https://rhb.lightboxgdansk.pl:51443/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/Adres/";
        private static String url = "";
        Map<String, String> params, headers;
        String dataKursu= _GlobalVariable.getDateFormat();



    public MK_Trasa_ZmienStatusDostarczenia_Request(Context c, String statusDostarczenia, String dlugoscGPS, String szerokoscGPS, String godzinaDostarczenia, int trasaId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, url, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);
        headers.put("dataKursu", dataKursu);

        params = new HashMap<>();
        params.put("statusDostarczenia", statusDostarczenia);
        params.put("dlugoscGPS", dlugoscGPS);
        params.put("szerokoscGPS", szerokoscGPS);
        params.put("godzinaDostarczenia", godzinaDostarczenia);
        params.put("trasaId", String.valueOf(trasaId));
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

