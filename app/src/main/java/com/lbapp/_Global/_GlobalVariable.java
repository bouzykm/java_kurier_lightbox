package com.lbapp._Global;

import org.json.JSONArray;

import java.util.List;


/**
 * Created by maciej on 30.10.2017.
 */

public class _GlobalVariable {

    static String  dateFormat, trasa, trasaKoor, trasaMK, login, requestId;
    static JSONArray trasy, trasyKoor;
    static int trasaIdMK;
    static List listTrasMK, listTrasIdMK;

    public static String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public static JSONArray getTablicaTras() {
        return trasy;
    }

    public void setTablicaTras(JSONArray trasy) {
        this.trasy = trasy;
    }

    public static JSONArray getTablicaTrasKoor() {
        return trasyKoor;
    }

    public void setTablicaTrasKoor(JSONArray trasyKoor) {
        this.trasyKoor = trasyKoor;
    }

    public static List getListTrasMK() {
        return listTrasMK;
    }

    public void setListTrasMK(List listTrasMK) {
        this.listTrasMK = listTrasMK;
    }

    public static List getListTrasIdMK() {
        return listTrasIdMK;
    }

    public void setListTrasIdMK(List listTrasIdMK) {
        this.listTrasIdMK = listTrasIdMK;
    }

    public static String getTrasa() {
        return trasa;
    }

    public void setTrasa(String trasa) {
        this.trasa = trasa;
    }

    public static String getTrasaKoor() {
        return trasaKoor;
    }

    public void setTrasaKoor(String trasaKoor) {
        this.trasaKoor = trasaKoor;
    }

    public static String getTrasaMK() {
        return trasaMK;
    }

    public void setTrasaMK(String trasaMK) {
        this.trasaMK = trasaMK;
    }

    public static int getTrasaIdMK() {
        return trasaIdMK;
    }

    public void setTrasaIdMK(int trasaIdMK) {
        this.trasaIdMK = trasaIdMK;
    }

    public static String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public static String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


}
