package com.lbapp.MasterKurier.Trasa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lbapp.R;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class Trasa_ListaTrasKurierskichAdapter extends BaseAdapter {

    Context c;
    ArrayList<TrasaObject> listaMKTras;
    LayoutInflater inflater;
    Toast toast;
    int statusZaznaczeniaWiersza=0;


    public Trasa_ListaTrasKurierskichAdapter(Context c, ArrayList<TrasaObject> listaMKTras) {
        this.c = c;
        this.listaMKTras = listaMKTras;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaMKTras.size();
    }

    @Override
    public Object getItem(int position) {
        return listaMKTras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaMKTras.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.mk_trasa_lista_tras_kurierskich_model,parent,false);
        }

        final TextView tvTrasa=convertView.findViewById(R.id.tvTrasa);
        final TextView tvLiczbaToreb=convertView.findViewById(R.id.tvLiczbaToreb);
        final TextView tvLiczbaBoxow=convertView.findViewById(R.id.tvLiczbaBoxow);
        final TextView tvLiczbaBoxowOstTor=convertView.findViewById(R.id.tvLiczbaBoxowOstTor);


        final TrasaObject s = (TrasaObject) this.getItem(position);

        tvTrasa.setText(listaMKTras.get(position).getTrasa());
        tvLiczbaToreb.setText(listaMKTras.get(position).getLiczbaToreb());
        tvLiczbaBoxow.setText(listaMKTras.get(position).getLiczbaBoxow());
        tvLiczbaBoxowOstTor.setText(listaMKTras.get(position).getLiczbaBoxowOstTor());

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
