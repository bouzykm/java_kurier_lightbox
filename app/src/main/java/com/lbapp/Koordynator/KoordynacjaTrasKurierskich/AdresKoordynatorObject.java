package com.lbapp.Koordynator.KoordynacjaTrasKurierskich;

import java.util.ArrayList;

/**
 * Created by maciej on 19.07.2017.
 */

public class AdresKoordynatorObject {

    String adresId, ulica, miejscowosc, dzielnica, godzinaOd, godzinaDo, firma, uwagi, kodDoDomofonu, kod, numeryProduktow, statusDostarczenia,symbolTrasy, loginDostarczenia, dataDostarczenia;
    int id;

    public int getId() {
        return id;
    } // wewnetrzny ID adresu

    public void setId(int id) {
        this.id = id;
    }

    public String getAdresId() {
        return adresId;
    } // zewnetrzny ID adresu, zapisany w bazie danych

    public void setAdresId(String adresId) {
        this.adresId = adresId;
    }

    public String getSymbolTrasy() {
        return symbolTrasy;
    }

    public void setSymbolTrasy(String symbolTrasy) {
        this.symbolTrasy = symbolTrasy;
    }

    public String getMiejscowosc() {
        return miejscowosc;
    }

    public void setMiejscowosc(String miejscowosc) {
        this.miejscowosc = miejscowosc;
    }

    public String getUlica() {
        return ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public String getDzielnica() {
        return dzielnica;
    }

    public void setDzielnica(String dzielnica) {
        this.dzielnica = dzielnica;
    }

    public String getGodzinaOd() {
        return godzinaOd;
    }

    public void setGodzinaOd(String godzinaOd) {
        this.godzinaOd = godzinaOd;
    }

    public String getGodzinaDo() {
        return godzinaDo;
    }

    public void setGodzinaDo(String godzinaDo) {
        this.godzinaDo = godzinaDo;
    }

    public String getStatusDostarczenia() {
        return statusDostarczenia;
    }

    public void setStatusDostarczenia(String statusDostarczenia) {
        this.statusDostarczenia = statusDostarczenia;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }


    public String getUwagi() {
        return uwagi;
    }

    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }

    public String getKodDoDomofonu() {
        return kodDoDomofonu;
    }

    public void setKodDoDomofonu(String kodDoDomofonu) {
        this.kodDoDomofonu = kodDoDomofonu;
    }

    public String getKodPocztowy() {
        return kod;
    }

    public void setKodPocztowy(String kod) {
        this.kod = kod;
    }

    public ArrayList<arrTorbyKoordynator> listaToreb;

    public AdresKoordynatorObject(){
        listaToreb=new ArrayList();
        numeryProduktow = "";
    }

    public String getNumeryProduktow() {
        return numeryProduktow;
    }

    public void setNumeryProduktow(String numeryProduktow) {
        this.numeryProduktow = numeryProduktow;
    }

    public String getLoginDostarczenia() {
        return loginDostarczenia;
    }

    public void setLoginDostarczenia(String loginDostarczenia) {
        this.loginDostarczenia = loginDostarczenia;
    }

    public String getDataDostarczenia() {
        return dataDostarczenia;
    }

    public void setDataDostarczenia(String dataDostarczenia) {
        this.dataDostarczenia = dataDostarczenia;
    }
}

