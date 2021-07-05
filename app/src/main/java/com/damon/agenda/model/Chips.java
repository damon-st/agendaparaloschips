package com.damon.agenda.model;

import java.util.Date;

public class Chips {

   private String image,numero,check,pid,category,time,productState,date,fecha,codigo,search;

   private float numerofecha;

   private long formatDate;


    public long getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(long formatDate) {
        this.formatDate = formatDate;
    }

    public float getNumerofecha() {
        return numerofecha;
    }

    public void setNumerofecha(float numerofecha) {
        this.numerofecha = numerofecha;
    }

    public Chips() {
    }

    public Chips(String image, String numero, String check, String pid, String category, String time, String productState, String date, String fecha, String codigo, String search, float numerofecha, long formatDate) {
        this.image = image;
        this.numero = numero;
        this.check = check;
        this.pid = pid;
        this.category = category;
        this.time = time;
        this.productState = productState;
        this.date = date;
        this.fecha = fecha;
        this.codigo = codigo;
        this.search = search;
        this.numerofecha = numerofecha;
        this.formatDate = formatDate;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public Date getConverDate(){
        Date date = new Date();
        date.setTime(getFormatDate());
        return date;
    }
}
