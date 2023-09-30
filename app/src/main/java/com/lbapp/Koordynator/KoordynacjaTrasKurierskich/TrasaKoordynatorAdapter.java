package com.lbapp.Koordynator.KoordynacjaTrasKurierskich;

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

import java.util.ArrayList;

/**
 * Created by maciej on 19.07.2017.
 */

public class TrasaKoordynatorAdapter extends BaseAdapter {

    Context c;
    ArrayList<AdresKoordynatorObject> listaAdresow;
    LayoutInflater inflater;

    public TrasaKoordynatorAdapter(Context c, ArrayList<AdresKoordynatorObject> listaAdresow) {
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
                convertView=inflater.inflate(R.layout.koordynator_administracja_tras_kurierskich_trasa_model,parent,false);
            }

         TextView tvAdres=convertView.findViewById(R.id.tvAdres);
         TextView tvDzielnica=convertView.findViewById(R.id.tvDzielnica);
         TextView tvStatusDostarczenia=convertView.findViewById(R.id.tvStatusDostarczenia);
         TextView tvGodzinaOd=convertView.findViewById(R.id.tvGodzinaOd);
         TextView tvGodzinaDo=convertView.findViewById(R.id.tvGodzinaDo);
         TextView tvRaportDostarczenia=convertView.findViewById(R.id.tvRaportDostarczenia);

        final AdresKoordynatorObject s = (AdresKoordynatorObject) this.getItem(position);

         tvAdres.setText(listaAdresow.get(position).getUlica());
         tvDzielnica.setText(listaAdresow.get(position).getDzielnica());
         tvStatusDostarczenia.setText(listaAdresow.get(position).getStatusDostarczenia());
         tvGodzinaOd.setText(listaAdresow.get(position).getGodzinaOd()+" - ");
         tvGodzinaDo.setText(listaAdresow.get(position).getGodzinaDo());
         tvRaportDostarczenia.setText("Dostarczył(a): "+listaAdresow.get(position).getLoginDostarczenia()+", "+listaAdresow.get(position).getDataDostarczenia());

        if(tvStatusDostarczenia.getText().toString().equals("1")){
            tvStatusDostarczenia.setText("✓");
            tvStatusDostarczenia.setTextColor(Color.rgb(0,128,0));
            tvStatusDostarczenia.setTypeface(null, Typeface.BOLD);
            tvStatusDostarczenia.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        }
        else if (tvStatusDostarczenia.getText().toString().equals("0"))
        {
            tvStatusDostarczenia.setText("✗");
            tvStatusDostarczenia.setTextColor(Color.RED);
            tvStatusDostarczenia.setTypeface(null, Typeface.BOLD);
            tvStatusDostarczenia.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity(s.getAdresId(), s. getSymbolTrasy(), s.getUlica(), s.getMiejscowosc(), s.getFirma(), s.getDzielnica(), s.getStatusDostarczenia(), s.getKodPocztowy(), s.getGodzinaOd(), s.getGodzinaDo(), s.getUwagi(), s.getKodDoDomofonu(),  s.getNumeryProduktow());
            }
        });

        return convertView;
    }

    private void openDetailActivity(String adresId, String symbolTrasy, String adres, String miejscowosc, String firma, String dzielnica, String raport, String kod, String godzinaOd, String godzinaDo, String uwagi, String kodDoDomofonu, String numeryProduktow)
    {
        Intent i = new Intent(c, SzczegolyAdresuKoordynatorAcitivty.class);

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
        i.putExtra("UWAGI_KEY", uwagi);
        i.putExtra("KODDODOMOFONU_KEY", kodDoDomofonu);
        i.putExtra("NUMERYPRODUKTOW_KEY", numeryProduktow);

        c.startActivity(i);
    }
}