package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.lbapp.Kurier.Trasa.TrasaAdapter;
import com.lbapp._Global.AdresObject;
import com.lbapp.Kurier.Trasa.arrTorby;
import com.lbapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by maciej on 11.07.2017.
 */

public class Kurier_Trasa__ListaAdresow_Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvListaAdresow, lvListaTras;
    String data;
    ArrayList<AdresObject> listaAdresow =new ArrayList<>();
    ProgressDialog pd;
    Button btnStatus;
    int iloscToreb, iloscBoxow, iloscPunktow, iloscPunktowDostarczonych;
    TextView tvInfo;



    public Kurier_Trasa__ListaAdresow_Parser(Context c, String data, ListView lvListaAdresow, Button btnStatus, TextView tvInfo) {
        this.c = c;
        this.data = data;
        this.lvListaAdresow = lvListaAdresow;
        this.btnStatus=btnStatus;
        this.tvInfo=tvInfo;
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
        TrasaAdapter adapter = new TrasaAdapter(c, listaAdresow);
        lvListaAdresow.setAdapter(adapter);
        tvInfo.setText("Punkty: "+iloscPunktowDostarczonych+"/"+iloscPunktow+"    Torby: "+iloscToreb+"    Pudełka: "+iloscBoxow);

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
            JSONArray arrAdresy = jsonObject.getJSONArray("arrAdresy");
            listaAdresow.clear();
            AdresObject l = null;
            arrTorby t = null;


            iloscToreb=jsonObject.getInt("numTorby");
            iloscBoxow=jsonObject.getInt("numBoxy");
            iloscPunktow=jsonObject.getInt("numAdresy");
            iloscPunktowDostarczonych=jsonObject.getInt("numAdresyDostarczone");
       //     statusPakowania=jsonObject.getString("statusPakowania");
        //    szacunekPakowania=jsonObject.getString("szacunekPakowania");





       //     l.setSzacunekPakowania(szacunekPakowania);


            for(int i=0;i<arrAdresy.length();i++)
            {

                JSONObject adresy=arrAdresy.getJSONObject(i);
                String adresId = adresy.getString("adresId");
                String ulica = adresy.getString("ulica");
                String miejscowosc = adresy.getString("miejscowosc");
                String dzielnica = adresy.getString("dzielnica");
                String godzinaOd = adresy.getString("godzinaOd");
                String godzinaDo = adresy.getString("godzinaDo");
                String statusDostarczenia = adresy.getString("statusDostarczenia");
                String firma = adresy.getString("firma");
                String telefon = adresy.getString("telefon");
                String uwagi = adresy.getString("uwagi");
                String kodDoDomofonu = adresy.getString("domofon");
                String kodPocztowy = adresy.getString("kod");
                String szacunekDostawy = adresy.getString("szacunek");
                int zostawTorbe = adresy.getInt("zostawTorbe");
                int czyBylaZmianaDanych = adresy.getInt("czyBylaZmianaDanych");
 //               int parametr = adresy.getInt("parametr"); //PARAMETR DO USTALANIA CZY KLIENT JEST VIPEM



                l = new AdresObject();

                l.setAdresId(adresId);
                l.setUlica(ulica);
                l.setMiejscowosc(miejscowosc);
                l.setDzielnica(dzielnica);
                l.setFirma(firma);
                l.setGodzinaOd(godzinaOd);
                l.setGodzinaDo(godzinaDo);
                l.setStatus(statusDostarczenia);
                l.setFirma(firma);
                l.setTelefon(telefon);
                l.setUwagi(uwagi);
                l.setKodDoDomofonu(kodDoDomofonu);
                l.setKodPocztowy(kodPocztowy);
                l.setSzacunekDostawy(szacunekDostawy);
                l.setZostawTorbe(zostawTorbe);
                l.setCzyBylaZmianaDanych(czyBylaZmianaDanych);
                //l.setParametr(parametr);

                JSONArray arrTorby = adresy.getJSONArray("arrTorby");
                for(int j = 0; j < arrTorby.length();j++) {

                    JSONObject torby=arrTorby.getJSONObject(j);
                    t = new arrTorby();
                    int nrTorby = torby.getInt("nrTorby");
                    int nrTorby2 = torby.getInt("nrTorby2");
                    t.setNrTorby(nrTorby);
                    t.setNrTorby2(nrTorby2);


                    //sprawdz czy wyswietlac dwa pudelka dla diety (4000kcal ma 2 pudelka)
                    String numeryToreb = "";
                    if (nrTorby2 != -1)
                    {
                        numeryToreb = (t.getNrTorby() +","+ t.getNrTorby2()+ " (2x❒)");
                    }
                    else
                    {
                        numeryToreb = ""+(t.getNrTorby());
                    }


                    l.listaToreb.add(t);

                    JSONArray arrProdukty = torby.getJSONArray("arrProdukty");
                    for (int k = 0; k < arrProdukty.length();k++)
                    {

                        String dieta = arrProdukty.getString(k);
                        l.listaToreb.get(j).listaProduktow.add(dieta);
                        l.setNumeryProduktow(l.getNumeryProduktow() + (numeryToreb+"   "+ dieta + '\n')); // wyswietlenie informacji o produktach w szczegolach adresu

                    }

                }

                listaAdresow.add(l);
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
    }}