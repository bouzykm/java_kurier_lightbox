package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ListView;

import com.lbapp.Koordynator.Prognoza.Prognoza_ListaTrasKurierskichAdapter;
import com.lbapp.Koordynator.Prognoza.Prognoza_TrasaKurierskaObject;
import com.lbapp.MasterKurier.Trasa.Trasa_ListaTrasKurierskichAdapter;
import com.lbapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Koordynator_Prognoza__ListaTrasKurierskich_Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvListaTrasKurierskich;
    String data;
    ArrayList<Prognoza_TrasaKurierskaObject> listaTrasKurierskich =new ArrayList<>();
    ProgressDialog pd;

    public Koordynator_Prognoza__ListaTrasKurierskich_Parser(Context c, String data, ListView lvListaTrasKurierskich) {
        this.c = c;
        this.data = data;
        this.lvListaTrasKurierskich = lvListaTrasKurierskich;

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

        Prognoza_ListaTrasKurierskichAdapter adapter = new Prognoza_ListaTrasKurierskichAdapter(c, listaTrasKurierskich);
        lvListaTrasKurierskich.setAdapter(adapter);
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
            JSONArray arrTrasy = jsonObject.getJSONArray("arrTrasy");

            listaTrasKurierskich.clear();

            Prognoza_TrasaKurierskaObject l = null;

            for(int i=0;i<arrTrasy.length();i++)
            {
                JSONObject adresy=arrTrasy.getJSONObject(i);
                String trasa = adresy.getString("trasa");
                String liczbaPunktow = adresy.getString("liczbaPunktow");

                l = new Prognoza_TrasaKurierskaObject();
                l.setTrasa(trasa);
                l.setLiczbaPunktow(liczbaPunktow);

                listaTrasKurierskich.add(l);
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