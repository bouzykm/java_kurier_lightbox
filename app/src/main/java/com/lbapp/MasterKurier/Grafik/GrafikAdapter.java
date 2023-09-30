package com.lbapp.MasterKurier.Grafik;
import android.content.Context;
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

public class GrafikAdapter extends BaseAdapter {

    Context c;
    ArrayList<GrafikObject> arrMkGrafik;
    LayoutInflater inflater;


    public GrafikAdapter(Context c, ArrayList<GrafikObject> arrMkGrafik) {
        this.c = c;
        this.arrMkGrafik = arrMkGrafik;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return arrMkGrafik.size();
    }

    @Override
    public Object getItem(int position)
    {
        return arrMkGrafik.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return arrMkGrafik.get(position).getTrasaMasterkurierId();
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.mk_grafik_model,parent,false);
        }

        TextView tvTrasaMasterKurier=convertView.findViewById(R.id.tvTrasaMasterKurier);
        TextView tvDataKursu=convertView.findViewById(R.id.tvDataKursu);


        //final GrafikObject obj = (GrafikObject) this.getItem(position);

        tvTrasaMasterKurier.setText(arrMkGrafik.get(position).getNazwaTrasyMasterkurier());
        tvDataKursu.setText(arrMkGrafik.get(position).getDataKursu());

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openDetailActivity(obj.getNazwaTrasyMasterkurier(), obj.getDataKursu());
//            }
//        });

        return convertView;
    }

//    private void openDetailActivity()
//    {
//
//    }
}