package com.lbapp.Koordynator.KoordynacjaTrasKurierskich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lbapp._Global._GlobalVariable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class WyborTrasyKoordynator extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvListaTras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.lbapp.R.layout.koordynator_administracja_tras_kurierskich_wybor_trasy_activity);
        toolbar = findViewById(com.lbapp.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wybierz trasę");
        lvListaTras = findViewById(com.lbapp.R.id.lvListaTras);

        try {
            JSONArray trasy= _GlobalVariable.getTablicaTras();
            ArrayList al = new ArrayList();

            for (int i = 0; i < trasy.length(); ++i) {
                al.add(trasy.getString(i));
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            lvListaTras.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvListaTras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String trasa = (String)lvListaTras.getItemAtPosition(position);
            _GlobalVariable globalVariable = new _GlobalVariable();
            globalVariable.setTrasa(trasa);
            Intent intent = new Intent(WyborTrasyKoordynator.this, TrasaKoordynatorActivity.class);
            startActivity(intent);
        }
    });
}


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
