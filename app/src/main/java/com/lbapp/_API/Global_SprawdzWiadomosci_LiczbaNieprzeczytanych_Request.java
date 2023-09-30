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

public class Global_SprawdzWiadomosci_LiczbaNieprzeczytanych_Request extends StringRequest{

    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/sprawdzWiadomosciNieprzeczytane";
    private Map<String, String> params, headers;

    public Global_SprawdzWiadomosci_LiczbaNieprzeczytanych_Request(Context c, String login, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET, URL, listener, errorListener);

        headers = new HashMap<>();
        headers.put("login", login);
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
