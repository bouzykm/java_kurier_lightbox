package com.lbapp.Wiadomosci;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp.Menu.Menu;
import com.lbapp.R;
import com.lbapp._API.Wiadomosc_Przeczytaj_Request;
import com.lbapp._Global.GetUnreadMessagesNumber;
import com.lbapp._Global._GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WiadomosciAdapter extends BaseAdapter {

    Context c;
    ArrayList<WiadomoscObject> listaWiadomosci;
    LayoutInflater inflater;
    String login = _GlobalVariable.getLogin();
    ProgressDialog pd;

    public WiadomosciAdapter(Context c, ArrayList<WiadomoscObject> listaWiadomosci) {
        this.c = c;
        this.listaWiadomosci = listaWiadomosci;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    
    @Override
    public int getCount() {
        return listaWiadomosci.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listaWiadomosci.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return listaWiadomosci.get(position).getIdWiadomosci();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.wiadomosci_model,parent,false);
        }


        TextView tvTytulWiadomosci=convertView.findViewById(R.id.tvTytulWiadomosci);
        TextView tvTrescWiadomosci=convertView.findViewById(R.id.tvTrescWiadomosci);
        TextView tvDataWiadomosci=convertView.findViewById(R.id.tvDataWiadomosci);

        final WiadomoscObject s = (WiadomoscObject) this.getItem(position);


        tvTytulWiadomosci.setText(listaWiadomosci.get(position).getTytulWiadomosci());
        tvTrescWiadomosci.setText(listaWiadomosci.get(position).getTrescWiadomosci());
        tvDataWiadomosci.setText(listaWiadomosci.get(position).getDataWiadomosci());

        if ((listaWiadomosci.get(position).getCzyWiadomoscPrzeczytana()))
        {
            tvTytulWiadomosci.setTextColor(Color.GRAY);
            tvTytulWiadomosci.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tvTrescWiadomosci.setTextColor(Color.GRAY);
            tvTrescWiadomosci.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }else
        {
            tvTytulWiadomosci.setTextColor(Color.parseColor("#006000"));
            tvTytulWiadomosci.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvTrescWiadomosci.setTextColor(Color.parseColor("#ff669900"));
            tvTrescWiadomosci.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity(s.getIdWiadomosci(), s.getTytulWiadomosci(), s.getTrescWiadomosci(), s.getCzyWiadomoscPrzeczytana());
            }
        });

        return convertView;
    }

    private void openDetailActivity(int idWiadomosci, String tytulWiadomosci, String trescWiadomosci, boolean czyWiadomoscPrzeczytana)
    {
        if (!czyWiadomoscPrzeczytana) {
            SharedPreferences sharedPreferencesRetRole = c.getSharedPreferences("retRolePreferences", Context.MODE_PRIVATE);
            int rola = sharedPreferencesRetRole.getInt("retRole", 0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            final String dataPrzeczytania = dateFormat.format(date);

            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int retCode = jsonResponse.getInt("retCode");

                        if (retCode == 0) {
                            GetUnreadMessagesNumber.getUnreadMessagesNumber(c, login);
                        } else {
                            Toast.makeText(c, "Wiadomość nie mogła zostać oznaczona jako przeczytana", Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(c, "Wiadomość nie mogła zostać oznaczona jako przeczytana", Toast.LENGTH_SHORT).show();

//                NetworkResponse networkResponse = error.networkResponse;
//                String jsonError = new String(networkResponse.data);
//                Log.e("UNR_MSG_ERR", jsonError);
                }
            };

            Wiadomosc_Przeczytaj_Request wiadomosc_przeczytaj_request = new Wiadomosc_Przeczytaj_Request(c, idWiadomosci, login, rola, dataPrzeczytania, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(c.getApplicationContext());
            queue.add(wiadomosc_przeczytaj_request);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(tytulWiadomosci)
                .setMessage(trescWiadomosci)
                .setCancelable(false)
                .setIcon(R.drawable.global_lightbox_logo)
                .setPositiveButton("Powrót", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(c, WiadomosciActivity.class);
                        c.startActivity(intent);
                    }
                })
                .create()
                .show();
    }
}
