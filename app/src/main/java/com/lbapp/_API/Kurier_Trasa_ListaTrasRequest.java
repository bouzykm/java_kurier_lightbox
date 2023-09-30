package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

import com.lbapp._Global._GlobalVariable;

/**
 * Created by maciej on 28.06.2017.
 */

public class Kurier_Trasa_ListaTrasRequest extends StringRequest{

    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/Trasa";
    private Map<String, String> params, headers;
    String dataDostawy= _GlobalVariable.getDateFormat();

    public Kurier_Trasa_ListaTrasRequest(Context c, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Request.Method.GET, URL, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);
        headers.put("dataDostawy", dataDostawy);

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
