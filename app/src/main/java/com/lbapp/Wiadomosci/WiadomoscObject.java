package com.lbapp.Wiadomosci;

public class WiadomoscObject {

    boolean czyWiadomoscPrzeczytana;
    int idWiadomosci;
    String tytulWiadomosci, trescWiadomosci, dataWiadomosci;

    public int getIdWiadomosci() {
        return idWiadomosci;
    }

    public void setIdWiadomosci(int idWiadomosci) {
        this.idWiadomosci = idWiadomosci;
    }

    public String getTytulWiadomosci() {
        return tytulWiadomosci;
    }

    public void setTytulWiadomosci(String tytulWiadomosci) {
        this.tytulWiadomosci = tytulWiadomosci;
    }

    public String getTrescWiadomosci() {
        return trescWiadomosci;
    }

    public void setTrescWiadomosci(String trescWiadomosci) {
        this.trescWiadomosci = trescWiadomosci;
    }

    public String getDataWiadomosci() {
        return dataWiadomosci;
    }

    public void setDataWiadomosci(String dataWiadomosci) {
        this.dataWiadomosci = dataWiadomosci;
    }

    public boolean getCzyWiadomoscPrzeczytana() {
        return czyWiadomoscPrzeczytana;
    }

    public void setCzyWiadomoscPrzeczytana(boolean czyWiadomoscPrzeczytana) {
        this.czyWiadomoscPrzeczytana = czyWiadomoscPrzeczytana;
    }

}
