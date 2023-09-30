package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.lbapp._Global._GlobalVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maciej on 28.06.2017.
 */

public class MK_Trasa_SprawdzStrzegowo_Request extends StringRequest{

    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/SprawdzStrzegowo";
    private Map<String, String> params, headers;
    String dataKursu= _GlobalVariable.getDateFormat();
    String trasaId = String.valueOf(_GlobalVariable.getTrasaIdMK());

    public MK_Trasa_SprawdzStrzegowo_Request(Context c, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET, URL, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);
        headers.put("dataKursu", dataKursu);
        headers.put("trasaId", trasaId);

        params = new HashMap<>();

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
