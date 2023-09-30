package com.lbapp.MasterKurier.Trasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;

import java.util.ArrayList;
import java.util.List;

public class WyborTrasyMasterkurierActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvListaTras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_trasa_wybor_trasy_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wybierz listę");
        lvListaTras = findViewById(R.id.lvListaTras);



        List trasy= _GlobalVariable.getListTrasMK();
        final List listTrasyId = _GlobalVariable.getListTrasIdMK();
        ArrayList al = new ArrayList();

        for (int i = 0; i < trasy.size(); ++i) {
            al.add(trasy.get(i));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
        lvListaTras.setAdapter(adapter);

        lvListaTras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trasa = (String)lvListaTras.getItemAtPosition(position);

                _GlobalVariable globalVariable = new _GlobalVariable();
                globalVariable.setTrasaIdMK((Integer) listTrasyId.get(position));
                globalVariable.setTrasaMK(trasa);

                Intent intent = new Intent(WyborTrasyMasterkurierActivity.this, TrasaActivity.class);
                startActivity(intent);
            }
        });
    }
}
