package com.lbapp.Koordynator.Prognoza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lbapp.MasterKurier.Trasa.TrasaObject;
import com.lbapp.R;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class Prognoza_ListaTrasKurierskichAdapter extends BaseAdapter {

    Context c;
    ArrayList<Prognoza_TrasaKurierskaObject> listaTrasKurierskich;
    LayoutInflater inflater;
    Toast toast;


    public Prognoza_ListaTrasKurierskichAdapter(Context c, ArrayList<Prognoza_TrasaKurierskaObject> listaTrasKurierskich) {
        this.c = c;
        this.listaTrasKurierskich = listaTrasKurierskich;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaTrasKurierskich.size();
    }

    @Override
    public Object getItem(int position) {
        return listaTrasKurierskich.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaTrasKurierskich.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.koordynator_prognoza_lista_tras_kurierskich_model,parent,false);
        }

        final TextView tvTrasa=convertView.findViewById(R.id.tvTrasa);
        final TextView tvLiczbaPunktow=convertView.findViewById(R.id.tvLiczbaPunktow);


        final Prognoza_TrasaKurierskaObject s = (Prognoza_TrasaKurierskaObject) this.getItem(position);

        tvTrasa.setText(listaTrasKurierskich.get(position).getTrasa());
        tvLiczbaPunktow.setText(listaTrasKurierskich.get(position).getLiczbaPunktow());

        return convertView;
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(c, message, LENGTH_SHORT);
        toast.show();
    }

}
