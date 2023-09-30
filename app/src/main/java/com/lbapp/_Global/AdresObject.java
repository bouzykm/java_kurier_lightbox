package com.lbapp._Global;

import com.lbapp.Kurier.Trasa.arrTorby;

import java.util.ArrayList;

/**
 * Created by maciej on 19.07.2017.
 */

public class AdresObject {

    String adresId, ulica, miejscowosc, dzielnica, godzinaOd, godzinaDo, firma, telefon, uwagi, kod, kodDoDomofonu, numeryProduktow, status, czyAdresPrognozyPrzeczytany,  szacunekDostawy, symbolTrasy;
    int zostawTorbe, czyBylaZmianaDanych;
    int id;

    public int getCzyBylaZmianaDanych() {
        return czyBylaZmianaDanych;
    }

    public void setCzyBylaZmianaDanych(int czyBylaZmianaDanych) {
        this.czyBylaZmianaDanych = czyBylaZmianaDanych;
    }

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

    public String getCzyAdresPrognozyPrzeczytany() {
        return czyAdresPrognozyPrzeczytany;
    } // czy adres jest oznaczony jako przeczytany w prognozach

    public void setCzyAdresPrognozyPrzeczytany(String czyAdresPrognozyPrzeczytany) {
        this.czyAdresPrognozyPrzeczytany = czyAdresPrognozyPrzeczytany;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
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

    public ArrayList<arrTorby> listaToreb;

    public AdresObject(){
        listaToreb=new ArrayList();
        numeryProduktow = "";
    }

    public String getNumeryProduktow() {
        return numeryProduktow;
    }

    public void setNumeryProduktow(String numeryProduktow) {
        this.numeryProduktow = numeryProduktow;
    }

    public String getSzacunekDostawy() {
        return szacunekDostawy;
    }

    public void setSzacunekDostawy(String szacunekDostawy) {
        this.szacunekDostawy = szacunekDostawy;
    }

//    public int getParametrAdresu() {
//        return parametrAdresu;
//    }
//
//    public void setParametrAdresu(int parametrAdresu) {
//        this.parametrAdresu = parametrAdresu;
//    }

    public int getZostawTorbe() {
        return zostawTorbe;
    }

    public void setZostawTorbe(int zostawTorbe) {
        this.zostawTorbe = zostawTorbe;
    }




}

