package com.lbapp.Koordynator.KoordynacjaTrasKurierskich;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lbapp.R;
import com.lbapp._Global._GlobalVariable;

import java.util.Locale;


public class SzczegolyAdresuKoordynatorAcitivty extends AppCompatActivity {

    String trasa, adresId, adres, firma, kodPocztowy, godzinaOd, godzinaDo, uwagi, kodDoDomofonu, raport, numeryProduktow, dzielnica, miejscowosc,
            login;
    TextView tvDzielnica, tvAdres, tvGodzina, tvFirma, tvUwagi, tvKodDoDomofonu, tvNumeryProduktow, FirmaLabel, UwagiLabel, KodDoDomofonuLabel, TorbaProduktLabel;
    FloatingActionButton fabMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.koordynator_administracja_tras_kurierskich_trasa_szczegoly_adresu_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(" ");

        Intent i = this.getIntent();
        adresId = i.getExtras().getString("ADRESID_KEY");
        adres = i.getExtras().getString("ADRES_KEY");
        miejscowosc = i.getExtras().getString("MIEJSCOWOSC_KEY");
        firma = i.getExtras().getString("FIRMA_KEY");
        dzielnica = i.getExtras().getString("DZIELNICA_KEY");
        kodPocztowy = i.getExtras().getString("KOD_KEY");
        godzinaOd = i.getExtras().getString("GODZINAOD_KEY");
        godzinaDo = i.getExtras().getString("GODZINADO_KEY");
        uwagi = i.getExtras().getString("UWAGI_KEY");
        kodDoDomofonu = i.getExtras().getString("KODDODOMOFONU_KEY");
        raport = i.getExtras().getString("RAPORT_KEY");
        numeryProduktow = i.getExtras().getString("NUMERYPRODUKTOW_KEY");

        tvAdres = findViewById(R.id.tvAdres);
        tvGodzina = findViewById(R.id.tvGodzina);
        tvDzielnica = findViewById(R.id.tvDzielnica);
        tvFirma = findViewById(R.id.tvFirma);
        FirmaLabel = findViewById(R.id.FirmaLabel);
        tvUwagi = findViewById(R.id.tvUwagi);
        UwagiLabel = findViewById(R.id.UwagiLabel);
        tvKodDoDomofonu = findViewById(R.id.tvKodDoDomofonu);
        KodDoDomofonuLabel = findViewById(R.id.KodDoDomofonuLabel);
        tvNumeryProduktow = findViewById(R.id.tvNumeryProduktow);
        TorbaProduktLabel = findViewById(R.id.TorbaProduktLabel);


        fabMapa = findViewById(R.id.fabMapa);

        tvAdres.setText(adres);
        tvAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvGodzina.setText(godzinaOd + "-" + godzinaDo);
        tvDzielnica.setText(dzielnica);
        tvFirma.setText(firma);
        tvUwagi.setText(uwagi);
        tvKodDoDomofonu.setText(kodDoDomofonu);
        tvNumeryProduktow.setText(numeryProduktow);

        trasa = _GlobalVariable.getTrasa();
        login = _GlobalVariable.getLogin();


        //----------------------------------------------------------------------------------------------------------------------------------------------------


        if (firma.equals("")) {
            tvFirma.setVisibility(View.GONE);
            FirmaLabel.setVisibility(View.GONE);
        }
        if (kodDoDomofonu.equals("")) {
            tvKodDoDomofonu.setVisibility(View.GONE);
            KodDoDomofonuLabel.setVisibility(View.GONE);
        }

    }

    public void onMapaClicked(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + adres + " " + miejscowosc + " " + kodPocztowy);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

}

