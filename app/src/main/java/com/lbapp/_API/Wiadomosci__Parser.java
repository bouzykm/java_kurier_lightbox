package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ListView;
import com.lbapp.R;
import com.lbapp.Wiadomosci.WiadomoscObject;
import com.lbapp.Wiadomosci.WiadomosciAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by maciej on 11.07.2017.
 */

public class Wiadomosci__Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvWiadomosci;
    String data;
    ArrayList<WiadomoscObject> listaWiadomosci =new ArrayList<>();
    ProgressDialog pd;

    public Wiadomosci__Parser(Context c, String data, ListView lvWiadomosci) {
        this.c = c;
        this.data = data;
        this.lvWiadomosci = lvWiadomosci;

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

        WiadomosciAdapter adapter = new WiadomosciAdapter(c, listaWiadomosci);
        lvWiadomosci.setAdapter(adapter);

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

            JSONArray arrWiadomosci = jsonObject.getJSONArray("arrWiadomosci");

            listaWiadomosci.clear();

            WiadomoscObject l = null;

            for(int i=0;i<arrWiadomosci.length();i++)
            {

                JSONObject wiadomosci=arrWiadomosci.getJSONObject(i);
                int idWiadomosci = wiadomosci.getInt("idWiadomosci");
                String dataWiadomosci = wiadomosci.getString("dataWiadomosci");
                String tytulWiadomosci = wiadomosci.getString("tytulWiadomosci");
                String trescWiadomosci = wiadomosci.getString("trescWiadomosci");
                boolean czyWiadomoscPrzeczytana = wiadomosci.getBoolean("czyPrzeczytana");


                l = new WiadomoscObject();

                l.setIdWiadomosci(idWiadomosci);
                l.setDataWiadomosci(dataWiadomosci);
                l.setTytulWiadomosci(tytulWiadomosci);
                l.setTrescWiadomosci(trescWiadomosci);
                l.setCzyWiadomoscPrzeczytana(czyWiadomoscPrzeczytana);

                listaWiadomosci.add(l);
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