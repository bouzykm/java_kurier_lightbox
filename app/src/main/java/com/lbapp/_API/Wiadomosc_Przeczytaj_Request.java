package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maciej on 16.09.2017.
 */

public class Wiadomosc_Przeczytaj_Request extends StringRequest {

    static String port = _PORT.getPort();
    private static final String url=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/wiadomoscPrzeczytaj";
    Map<String, String> params, headers;

    public Wiadomosc_Przeczytaj_Request(Context c, int pushId, String login, int rola, String dataPrzeczytania, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");

        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);

        params = new HashMap<>();
        params.put("pushId", String.valueOf(pushId));
        params.put("login", login);
        params.put("rola", String.valueOf(rola));
        params.put("dataPrzeczytania", dataPrzeczytania);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}

