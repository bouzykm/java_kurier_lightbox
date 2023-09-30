package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;

import com.lbapp.Koordynator.KoordynacjaTrasKurierskich.TrasaKoordynatorAdapter;
import com.lbapp.Koordynator.KoordynacjaTrasKurierskich.arrTorbyKoordynator;
import com.lbapp.R;
import com.lbapp.Koordynator.KoordynacjaTrasKurierskich.AdresKoordynatorObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by maciej on 11.07.2017.
 */

public class Koordynator_AdministracjaTrasKurierskich__Trasa_Parser extends AsyncTask<Void, Integer, Integer>{

    Context c;
    ListView lvListaAdresow;
    String data, retKurier;
    ArrayList<AdresKoordynatorObject> listaAdresow =new ArrayList<>();
    ProgressDialog pd;
    int iloscToreb, iloscBoxow, iloscPunktow, iloscPunktowDostarczonych;
    TextView tvInfo;

    public Koordynator_AdministracjaTrasKurierskich__Trasa_Parser(Context c, String data, ListView lvListaAdresow, TextView tvInfo) {
        this.c = c;
        this.data = data;
        this.lvListaAdresow = lvListaAdresow;
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
        TrasaKoordynatorAdapter adapter = new TrasaKoordynatorAdapter(c, listaAdresow);
        lvListaAdresow.setAdapter(adapter);
        tvInfo.setText("Punkty: "+iloscPunktowDostarczonych+"/"+iloscPunktow+"    Torby: "+iloscToreb+"    Pudełka: "+iloscBoxow +"\nKurier: "+retKurier);

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
            AdresKoordynatorObject l = null;
            arrTorbyKoordynator t = null;

            iloscToreb=jsonObject.getInt("numTorby");
            retKurier=jsonObject.getString("retKurier");
            iloscBoxow=jsonObject.getInt("numBoxy");
            iloscPunktow=jsonObject.getInt("numAdresy");
            iloscPunktowDostarczonych=jsonObject.getInt("numAdresyDostarczone");

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
                String uwagi = adresy.getString("uwagi");
                String kodDoDomofonu = adresy.getString("domofon");
                String kodPocztowy = adresy.getString("kod");
                String loginDostarczenia = adresy.getString("loginDostarczenia");
                String dataDostarczenia = adresy.getString("dataDostarczenia");



                l = new AdresKoordynatorObject();

                l.setAdresId(adresId);
                l.setUlica(ulica);
                l.setMiejscowosc(miejscowosc);
                l.setDzielnica(dzielnica);
                l.setGodzinaOd(godzinaOd);
                l.setGodzinaDo(godzinaDo);
                l.setStatusDostarczenia(statusDostarczenia);
                l.setFirma(firma);
                l.setUwagi(uwagi);
                l.setKodDoDomofonu(kodDoDomofonu);
                l.setKodPocztowy(kodPocztowy);
                l.setLoginDostarczenia(loginDostarczenia);
                l.setDataDostarczenia(dataDostarczenia);

                JSONArray arrTorbyKoordynator = adresy.getJSONArray("arrTorby");
                for(int j = 0; j < arrTorbyKoordynator.length();j++) {

                    JSONObject torby=arrTorbyKoordynator.getJSONObject(j);
                    t = new arrTorbyKoordynator();
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