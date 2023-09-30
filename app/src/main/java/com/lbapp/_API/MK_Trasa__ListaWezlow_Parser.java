package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ListView;

import com.lbapp.Kurier.Trasa.arrTorby;
import com.lbapp.MasterKurier.Trasa.TrasaAdapter;
import com.lbapp.MasterKurier.Trasa.WezelObject;
import com.lbapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by maciej on 11.07.2017.
 */

public class MK_Trasa__ListaWezlow_Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvListaMK;
    String data, liczbaToreb, liczbaBoxow;
    ArrayList<WezelObject> listaWezlow =new ArrayList<>();
    ProgressDialog pd;

    public MK_Trasa__ListaWezlow_Parser(Context c, String data, ListView lvListaMK) {
        this.c = c;
        this.data = data;
        this.lvListaMK = lvListaMK;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(c);
        pd.setTitle("Wstawianie danych");
        pd.setMessage("Trwa wstawianie danych...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
          return this.parse();
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        pd.dismiss();

        TrasaAdapter adapter = new TrasaAdapter(c, listaWezlow);
        lvListaMK.setAdapter(adapter);
    }


    private int parse(){

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;


        if (connected ==true) {
        try{
            JSONObject jsonObject = new JSONObject(data);
            JSONArray arrAdresyMK = jsonObject.getJSONArray("arrAdresyMK");

            listaWezlow.clear();

            WezelObject l = null;
            arrTorby t = null;


            for(int i=0;i<arrAdresyMK.length();i++)
            {
                JSONObject adresy=arrAdresyMK.getJSONObject(i);
                int wezelId = adresy.getInt("wezelId");
                String wezel = adresy.getString("wezel");
                String statusDostarczenia = adresy.getString("statusDostarczenia");
                liczbaToreb = adresy.getString("liczbaToreb");


                l = new WezelObject();
                l.setId(wezelId);
                l.setWezel(wezel);
                l.setStatusDostarczenia(statusDostarczenia);
                l.setLiczbaToreb(liczbaToreb);

                listaWezlow.add(l);
            }

            return 1;

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return 0;
    }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Błąd");
            builder.setMessage("Brak połączenia z Internetem.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Powrót",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }});
            pd.dismiss();
            builder.show();
        }
        return 0;
    }

}