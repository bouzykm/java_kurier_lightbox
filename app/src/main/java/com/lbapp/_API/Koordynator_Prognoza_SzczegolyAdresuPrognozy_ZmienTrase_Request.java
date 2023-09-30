package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Koordynator_Prognoza_SzczegolyAdresuPrognozy_ZmienTrase_Request extends StringRequest {


    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/prognozaZmienTrase";
    private Map<String, String> params, headers;

    public Koordynator_Prognoza_SzczegolyAdresuPrognozy_ZmienTrase_Request(Context c, String zmianaNaStale, String adresId, String trasaDefault, String trasaEdit, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Request.Method.POST, URL, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);

        params = new HashMap<>();
        params.put("zmianaNaStale", zmianaNaStale);
        params.put("adresId", adresId);
        params.put("trasaDefault", trasaDefault);
        params.put("trasaEdit", trasaEdit);

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
