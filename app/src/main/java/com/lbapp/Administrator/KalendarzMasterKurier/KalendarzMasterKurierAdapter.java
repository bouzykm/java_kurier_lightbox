package com.lbapp.Administrator.KalendarzMasterKurier;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lbapp.R;

import java.util.ArrayList;

/**
 * Created by maciej on 18.11.2020.
 */

public class KalendarzMasterKurierAdapter extends BaseAdapter {

    Context c;
    ArrayList<TrasaMasterkurierObject> listaKalendarzMasterKurier;
    LayoutInflater inflater;



    public KalendarzMasterKurierAdapter(Context c, ArrayList<TrasaMasterkurierObject> listaKalendarzMasterKurier) {
        this.c = c;
        this.listaKalendarzMasterKurier = listaKalendarzMasterKurier;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return listaKalendarzMasterKurier.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listaKalendarzMasterKurier.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return listaKalendarzMasterKurier.get(position).getTrasaMasterkurierId();
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.administrator_kalendarz_master_kurierow_model,parent,false);
        }


        TextView tvMasterkurier=convertView.findViewById(R.id.tvDataKursu);
        TextView tvTrasaMasterKurier=convertView.findViewById(R.id.tvTrasaMasterKurier);

        final TrasaMasterkurierObject s = (TrasaMasterkurierObject) this.getItem(position);

        tvMasterkurier.setText(listaKalendarzMasterKurier.get(position).getMasterkurier());
        tvTrasaMasterKurier.setText(listaKalendarzMasterKurier.get(position).getNazwaTrasyMasterkurier());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity(s.getMasterkurier(), s.getMasterkurierTelefon());
            }
        });

        return convertView;
    }

    private void openDetailActivity(String masterkurier, final String masterkurierTelefon)
    {
        String[] wybor = {"Zadzwoń", "Napisz SMS"};
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(masterkurier)
                .setIcon(R.drawable.global_lightbox_logo)
                .setItems(wybor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Zadzwoń
                            {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + masterkurierTelefon)); // numer telefonu klienta wykorzystany do połączenia
                                c.startActivity(intent);
                                break;
                            }
                            case 1: // Napisz SMS
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("sms:" + masterkurierTelefon)); // numer telefonu klienta wykorzystany do połączenia
                                c.startActivity(intent);
                                break;
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}