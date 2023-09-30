package com.lbapp.Kurier.Trasa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lbapp.R;
import com.lbapp._Global.AdresObject;
import com.lbapp._Global._GlobalVariable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by maciej on 19.07.2017.
 */

public class TrasaAdapter extends BaseAdapter {


    Context c;
    ArrayList<AdresObject> listaAdresow;
    LayoutInflater inflater;

    long    roznicaCzasuMinuty,
            roznicaCzasuMilisekundy;
    SimpleDateFormat
            timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    String  godzina = timeFormat.format(Calendar.getInstance().getTime()),
            godzinaDo,
            statusDostarczenia,
            czyBylaZmianaDanych;
    Date    czasTeraz,
            adresGodzDo;


    public TrasaAdapter(Context c, ArrayList<AdresObject> listaAdresow) {
        this.c = c;
        this.listaAdresow = listaAdresow;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaAdresow.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listaAdresow.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return listaAdresow.get(position).getId();
    }


    @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView==null)
            {
                convertView=inflater.inflate(com.lbapp.R.layout.kurier_trasa_model,parent,false);
            }

        TextView tvAdres=convertView.findViewById(R.id.tvAdres);
        TextView tvDzielnica=convertView.findViewById(R.id.tvDzielnica);
        TextView tvStatusDostarczenia=convertView.findViewById(R.id.tvStatusDostarczenia);
        TextView tvGodzinaOd=convertView.findViewById(R.id.tvGodzinaOd);
        TextView tvGodzinaDo=convertView.findViewById(R.id.tvGodzinaDo);
        TextView tvSzacunek=convertView.findViewById(R.id.tvSzacunek);
        TextView tvCzyBylaZmianaDanych=convertView.findViewById(R.id.tvCzyBylaZmianaDanych);
        TextView tv15minsLeft=convertView.findViewById(R.id.tv15minsLeft);

        final AdresObject s = (AdresObject) this.getItem(position);

        tvAdres.setText(listaAdresow.get(position).getUlica());
        tvDzielnica.setText(listaAdresow.get(position).getDzielnica());
        tvStatusDostarczenia.setText(listaAdresow.get(position).getStatus());
            statusDostarczenia = tvStatusDostarczenia.getText().toString();
        tvCzyBylaZmianaDanych.setText(String.valueOf(listaAdresow.get(position).getCzyBylaZmianaDanych()));
            czyBylaZmianaDanych = tvCzyBylaZmianaDanych.getText().toString();
        tvGodzinaOd.setText(listaAdresow.get(position).getGodzinaOd()+" - ");
        tvGodzinaDo.setText(listaAdresow.get(position).getGodzinaDo());
            godzinaDo = tvGodzinaDo.getText().toString();
            try {
                adresGodzDo = timeFormat.parse(godzinaDo);
                czasTeraz =  timeFormat.parse(godzina);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        tvSzacunek.setText("("+listaAdresow.get(position).getSzacunekDostawy()+")");

        //---------------------------------------------------------------------------------
        //                              SYMBOL DOSTARCZENIA
        if(statusDostarczenia.equals("1")){
            tvStatusDostarczenia.setText("✓");
            tvStatusDostarczenia.setTextColor(Color.rgb(0,128,0));
            tvStatusDostarczenia.setTypeface(null, Typeface.BOLD);
            tvStatusDostarczenia.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        } else if (statusDostarczenia.equals("0"))
        {
            tvStatusDostarczenia.setText("✗");
            tvStatusDostarczenia.setTextColor(Color.RED);
            tvStatusDostarczenia.setTypeface(null, Typeface.BOLD);
            tvStatusDostarczenia.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        }
        //---------------------------------------------------------------------------------
        //                             SYMBOL ZMIANY GODZIN/UWAG
        if (czyBylaZmianaDanych.equals("1"))
        {
            tvCzyBylaZmianaDanych.setText("!");
            tvCzyBylaZmianaDanych.setTextColor(Color.rgb(255,136,0));
            tvCzyBylaZmianaDanych.setTypeface(null, Typeface.BOLD);
            tvCzyBylaZmianaDanych.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        } else if (czyBylaZmianaDanych.equals("0"))
        {
            tvCzyBylaZmianaDanych.setText(" ");
        }

        //---------------------------------------------------------------------------------
        //                         ALERT O EWENTUALNYCH SPOZNIENIACH
        // jezeli adres niedostarczony, roznica do czasu dostarczenia jest <= 15min, ale max do 2h
        // pokaz alarm przy adresie
        if (!godzinaDo.equals("")) {
            roznicaCzasuMilisekundy = adresGodzDo.getTime() - czasTeraz.getTime();
            roznicaCzasuMinuty = roznicaCzasuMilisekundy / (60 * 1000);
        }
        if (statusDostarczenia.equals("0") && roznicaCzasuMinuty <= 15 && roznicaCzasuMinuty >= -120 && !godzinaDo.equals(""))
        {
            tv15minsLeft.setText("⏱");
            tv15minsLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            // jezeli opoznienie dostawy - dodaj jeszcze wykrzyknik
            if (roznicaCzasuMinuty<= 0)
            {
                tv15minsLeft.setText("⏱!");
                tv15minsLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            }
        } else
            tv15minsLeft.setText(" ");

        //---------------------------------------------------------------------------------


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity(s.getAdresId(), s. getSymbolTrasy(), s.getUlica(), s.getMiejscowosc(), s.getFirma(), s.getDzielnica(), s.getStatus(), s.getKodPocztowy(), s.getGodzinaOd(), s.getGodzinaDo(), s.getTelefon(), s.getUwagi(), s.getKodDoDomofonu(), s.getNumeryProduktow(), s.getZostawTorbe());
            }
        });

        return convertView;
    }

    private void openDetailActivity(String adresId, String symbolTrasy, String adres, String miejscowosc, String firma, String dzielnica, String raport, String kod, String godzinaOd, String godzinaDo, String telefon, String uwagi, String kodDoDomofonu, String numeryProduktow, int zostawTorbe)
    {
        Intent i = new Intent(c, SzczegolyAdresuAcitivty.class);

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
        i.putExtra("ZOSTAWTORBE_KEY", zostawTorbe);

        c.startActivity(i);
    }
}