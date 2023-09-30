package com.lbapp._API;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Koordynator_Prognoza_DodajListeDoHistorii_Request extends StringRequest
{
    static String port = _PORT.getPort();
    private static final String URL=port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/prognozaHistoria";
    private Map<String, String> params, headers;



    public Koordynator_Prognoza_DodajListeDoHistorii_Request(Context c, List<String> listaAdresyId, List<String> listaTras, String dataDostawy, String symbolKoor, int liczWiersze, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Request.Method.POST, URL, listener, errorListener);

        SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", "");


        headers = new HashMap<>();
        headers.put("sessionToken", sessionToken);

        params = new HashMap<>();
        for (int i = 0; i < liczWiersze; i++)
        {
            params.put("listaAdresyId["+i+"]", listaAdresyId.get(i));
            params.put("listaTras["+i+"]", listaTras.get(i));
        }

        params.put("dataDostawy", dataDostawy);
        params.put("symbolKoor", symbolKoor);
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

