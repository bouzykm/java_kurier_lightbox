package com.lbapp.Koordynator.Prognoza;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lbapp._Global.AdresObject;
import com.lbapp.R;

import java.util.ArrayList;

/**
 * Created by maciej on 19.07.2017.
 */

public class ListaPrognozyAdapter extends BaseAdapter {

    int statusZmiany, czyAdresPrognozyPrzeczytany;
    Context c;
    ArrayList<AdresObject> listaAdresowPrognoza;
    LayoutInflater inflater;



    public ListaPrognozyAdapter(Context c, ArrayList<AdresObject> listaAdresow) {
        this.c = c;
        this.listaAdresowPrognoza = listaAdresow;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return listaAdresowPrognoza.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listaAdresowPrognoza.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return listaAdresowPrognoza.get(position).getId();
    }


    @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView==null)
            {
                convertView=inflater.inflate(R.layout.koordynator_prognoza_model,parent,false);
            }


         TextView tvSymbolTrasy=convertView.findViewById(R.id.tvSymbolTrasy);
         TextView tvAdres=convertView.findViewById(R.id.tvAdres);
         TextView tvDzielnica=convertView.findViewById(R.id.tvDzielnica);
         TextView tvRaport=convertView.findViewById(R.id.tvRaport);
         TextView tvGodzinaOd=convertView.findViewById(R.id.tvGodzinaOd);
         TextView tvGodzinaDo=convertView.findViewById(R.id.tvGodzinaDo);



        final AdresObject s = (AdresObject) this.getItem(position);

         tvSymbolTrasy.setText(listaAdresowPrognoza.get(position).getSymbolTrasy());
         tvAdres.setText(listaAdresowPrognoza.get(position).getUlica());
         tvDzielnica.setText(listaAdresowPrognoza.get(position).getDzielnica());
         statusZmiany = Integer.parseInt((listaAdresowPrognoza.get(position).getStatus()));
         czyAdresPrognozyPrzeczytany = Integer.parseInt((listaAdresowPrognoza.get(position).getCzyAdresPrognozyPrzeczytany()));
         tvGodzinaOd.setText(listaAdresowPrognoza.get(position).getGodzinaOd()+" - ");
         tvGodzinaDo.setText(listaAdresowPrognoza.get(position).getGodzinaDo());

        if(statusZmiany==1 & czyAdresPrognozyPrzeczytany==0){
            String nieprzeczytany = "<font color='#FF8800'>!</font>";

            tvRaport.setText(Html.fromHtml("⇄ " + nieprzeczytany));

            tvRaport.setTextColor(Color.rgb(30,144,255));
            tvRaport.setTypeface(null, Typeface.BOLD);
            tvRaport.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        }
        else if(statusZmiany==0 & czyAdresPrognozyPrzeczytany==1){
            tvRaport.setText(" ");
        }

        else if(statusZmiany==1 & czyAdresPrognozyPrzeczytany==1){
            tvRaport.setText("⇄");
            tvRaport.setTextColor(Color.rgb(30,144,255));
            tvRaport.setTypeface(null, Typeface.BOLD);
            tvRaport.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        }
        else if (statusZmiany==0 & czyAdresPrognozyPrzeczytany==0)
        {
            tvRaport.setText("!");
            tvRaport.setTextColor(Color.rgb(255,136,0));
            tvRaport.setTypeface(null, Typeface.BOLD);
            tvRaport.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        }






        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity(s.getAdresId(), s.getSymbolTrasy(), s.getUlica(), s.getMiejscowosc(), s.getFirma(), s.getDzielnica(), s.getStatus(), s.getKodPocztowy(), s.getGodzinaOd(), s.getGodzinaDo(), s.getTelefon(), s.getUwagi(), s.getKodDoDomofonu(), s.getNumeryProduktow());
            }
        });

        return convertView;
    }

    private void openDetailActivity(String adresId, String symbolTrasy, String adres, String miejscowosc, String firma, String dzielnica, String raport, String kod, String godzinaOd, String godzinaDo, String telefon, String uwagi, String kodDoDomofonu, String numeryProduktow)
    {
        Intent i = new Intent(c, SzczegolyAdresuPrognozyActivity.class);

        i.putExtra("ADRESID_KEY", adresId);
        i.putExtra("SYMBOLTRASY_KEY", symbolTrasy);
        i.putExtra("ADRES_KEY", adres);
        i.putExtra("MIEJSCOWOSC_KEY", miejscowosc);
        i.putExtra("FIRMA_KEY", firma);
        i.putExtra("DZIELNICA_KEY", dzielnica);
        i.putExtra("RAPORT_KEY", raport);
        i.putExtra("KOD_KEY", kod);
        i.putExtra("GODZINAOD_KEY", godzinaOd);
        i.putExtra("GODZINADO_KEY", godzinaDo);
        i.putExtra("TELEFON_KEY", telefon);
        i.putExtra("UWAGI_KEY", uwagi);
        i.putExtra("KODDODOMOFONU_KEY", kodDoDomofonu);
        i.putExtra("NUMERYPRODUKTOW_KEY", numeryProduktow);

        c.startActivity(i);
    }
}