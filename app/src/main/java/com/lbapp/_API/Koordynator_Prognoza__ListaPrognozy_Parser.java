package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lbapp._Global.AdresObject;
import com.lbapp.Koordynator.Prognoza.ListaPrognozyAdapter;
import com.lbapp.Kurier.Trasa.arrTorby;
import com.lbapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maciej on 11.07.2017.
 */

public class Koordynator_Prognoza__ListaPrognozy_Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvListaPrognozy, lvListaSymboliTras;
    String data;
    ArrayList<AdresObject> listaAdresow =new ArrayList<>();
    ArrayList<String> listaSymboliTras =new ArrayList<>();
    ProgressDialog pd;
    TextView tvInfoPrognoza;

    public Koordynator_Prognoza__ListaPrognozy_Parser(Context c, String data, ListView lvListaPrognozy, TextView tvInfoPrognoza) {
        this.c = c;
        this.data = data;
        this.lvListaPrognozy = lvListaPrognozy;
        this.tvInfoPrognoza = tvInfoPrognoza;
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

        saveArrayList(listaSymboliTras, "symbolTrasy");
        ListaPrognozyAdapter adapter = new ListaPrognozyAdapter(c, listaAdresow);
        lvListaPrognozy.setAdapter(adapter);
    }

    public void saveArrayList(ArrayList<String> listaSymboliTras, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listaSymboliTras);
        editor.putString(key, json);
        editor.apply();
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

            JSONArray arrAdresy = jsonObject.getJSONArray("arrAdresyPrognozy");
            JSONArray arrSymboleTras = jsonObject.getJSONArray("arrSymboleTras");
            tvInfoPrognoza.setText("Liczba adresów: "+arrAdresy.length());

            listaAdresow.clear();
            listaSymboliTras.clear();

            AdresObject l = null;
            arrTorby t = null;
            List<String> listaAdresyId = new ArrayList<String>();
            List<String> listaTras = new ArrayList<String>();

            for(int i=0; i<arrSymboleTras.length();i++)
            {
                JSONObject trasy=arrSymboleTras.getJSONObject(i);
                String symbolTrasy = trasy.getString("symbolTrasy");

                listaSymboliTras.add(symbolTrasy);
            }


            for(int i=0;i<arrAdresy.length();i++)
            {

                JSONObject adresy=arrAdresy.getJSONObject(i);
                String adresId = adresy.getString("adresId");
                listaAdresyId.add(adresId);
                String symbolTrasy = adresy.getString("symbolTrasy");
                listaTras.add(symbolTrasy);
                String czyBylaZmiana = adresy.getString("czyBylaZmiana");
                String czyAdresPrognozyPrzeczytany = adresy.getString("czyPrzeczytany");
                String ulica = adresy.getString("ulica");
                String miejscowosc = adresy.getString("miejscowosc");
                String dzielnica = adresy.getString("dzielnica");
                String godzinaOd = adresy.getString("godzinaOd");
                String godzinaDo = adresy.getString("godzinaDo");
                String firma = adresy.getString("firma");
                String telefon = adresy.getString("telefon");
                String uwagi = adresy.getString("uwagi");
                String kodDoDomofonu = adresy.getString("domofon");
                String kodPocztowy = adresy.getString("kod");
                //               int parametr = adresy.getInt("parametr"); //PARAMETR DO USTALANIA CZY KLIENT JEST VIPEM



                l = new AdresObject();

                l.setAdresId(adresId);
                l.setSymbolTrasy(symbolTrasy);
                l.setStatus(czyBylaZmiana);
                l.setCzyAdresPrognozyPrzeczytany(czyAdresPrognozyPrzeczytany);
                l.setUlica(ulica);
                l.setMiejscowosc(miejscowosc);
                l.setDzielnica(dzielnica);
                l.setGodzinaOd(godzinaOd);
                l.setGodzinaDo(godzinaDo);
                l.setFirma(firma);
                l.setTelefon(telefon);
                l.setUwagi(uwagi);
                l.setKodDoDomofonu(kodDoDomofonu);
                l.setKodPocztowy(kodPocztowy);
   //             l.setParametr(parametr);

                listaAdresow.add(l);
            }

            writeList(c, listaAdresyId, "listaAdresyId");
            writeList(c, listaTras, "listaTras");


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

    public static void writeList(Context context, List<String> list, String prefix)
    {
        SharedPreferences prefs = context.getSharedPreferences("liczWiersze", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int size = prefs.getInt(prefix+"_size", 0);

        // clear the previous data if exists
        for(int i=0; i<size; i++)
            editor.remove(prefix+"_"+i);

        // write the current list
        for(int i=0; i<list.size(); i++)
            editor.putString(prefix+"_"+i, list.get(i));

        editor.putInt(prefix+"_size", list.size());
        editor.commit();
    }
}