package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ListView;
import com.lbapp.Administrator.KalendarzMasterKurier.KalendarzMasterKurierAdapter;
import com.lbapp.Administrator.KalendarzMasterKurier.TrasaMasterkurierObject;
import com.lbapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by maciej on 11.07.2017.
 */

public class Administrator_KalendarzMasterKurier__Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvListaKalendarzMasterKurier;
    String data;
    ArrayList<TrasaMasterkurierObject> listaKalendarzMasterKurier =new ArrayList<>();
    ProgressDialog pd;

    public Administrator_KalendarzMasterKurier__Parser(Context c, String data, ListView lvListaKalendarzMasterKurier) {
        this.c = c;
        this.data = data;
        this.lvListaKalendarzMasterKurier = lvListaKalendarzMasterKurier;

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

        KalendarzMasterKurierAdapter adapter = new KalendarzMasterKurierAdapter(c, listaKalendarzMasterKurier);
        lvListaKalendarzMasterKurier.setAdapter(adapter);

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

            JSONArray arrKalendarzMasterKurier = jsonObject.getJSONArray("arrKalendarzMasterKurier");

            listaKalendarzMasterKurier.clear();

            TrasaMasterkurierObject l = null;

            for(int i=0;i<arrKalendarzMasterKurier.length();i++)
            {

                JSONObject trasy=arrKalendarzMasterKurier.getJSONObject(i);
                String masterkurier = trasy.getString("masterkurier");
                String masterkurierTelefon = trasy.getString("masterkurierTelefon");
                String nazwaTrasyMasterkurier = trasy.getString("nazwaTrasyMasterkurier");

                l = new TrasaMasterkurierObject();

                l.setMasterkurier(masterkurier);
                l.setNazwaTrasyMasterkurier(nazwaTrasyMasterkurier);
                l.setMasterkurierTelefon(masterkurierTelefon);

                listaKalendarzMasterKurier.add(l);
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