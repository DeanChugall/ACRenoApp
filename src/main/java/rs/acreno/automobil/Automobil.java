package rs.acreno.automobil;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Automobil {

    private int idAuta;
    private int idKlijenta;
    private String regOznaka;
    private String kilomteraza;
    private String vrstaVozila;
    private String markaVozila;
    private String modelVozila;
    private int godisteVozila;
    private int zapreminaVozila;
    private int snagaVozila;
    private String vinVozila;
    private String brojMotoraVozila;
    private String vrstaGorivaVozila;
    private String bojaVozila;
    private int masaVozila;
    private int najvecaDozvoljenaMasaVozila;
    private String datumPrveRegistracijeVozila;
    private int brojMestaZaSedenje;
    private int brojVrataVozila;
    private String napomeneAutomobila;

    private AutomobilDAO automobilDAO;

    public Automobil() {
    }

    public Automobil(int idAuta, int idKlijenta) {
        this.idAuta = idAuta;
        this.idKlijenta = idKlijenta;
    }

    public Automobil(int idKlijenta, String regOznaka, String kilometraza, String markaVozila,
                     String modelVozila, int godisteVozila, String vinVozila, String napomeneAutomobila) {
        this.idKlijenta = idKlijenta;
        this.regOznaka = regOznaka;
        this.kilomteraza = kilometraza;
        this.markaVozila = markaVozila;
        this.modelVozila = modelVozila;
        this.godisteVozila = godisteVozila;
        this.vinVozila = vinVozila;
        this.napomeneAutomobila = napomeneAutomobila;
    }

    public Automobil(String regOznaka, String markaVozila,
                     String modelVozila, int godisteVozila, String vinVozila) {
        this.regOznaka = regOznaka;
        this.markaVozila = markaVozila;
        this.modelVozila = modelVozila;
        this.godisteVozila = godisteVozila;
        this.vinVozila = vinVozila;
    }

    public int getIdAuta() {
        return idAuta;
    }

    public void setIdAuta(int idAuta) {
        this.idAuta = idAuta;
    }

    public int getIdKlijenta() {
        return idKlijenta;
    }

    public void setIdKlijenta(int idKlijenta) {
        this.idKlijenta = idKlijenta;
    }

    public String getRegOznaka() {
        return regOznaka;
    }

    public void setRegOznaka(String regOznaka) {
        this.regOznaka = regOznaka;
    }

    public String getKilomteraza() {
        return kilomteraza;
    }

    public void setKilomteraza(String kilomteraza) {
        this.kilomteraza = kilomteraza;
    }

    public String getVrstaVozila() {
        return vrstaVozila;
    }

    public void setVrstaVozila(String vrstaVozila) {
        this.vrstaVozila = vrstaVozila;
    }

    public String getMarkaVozila() {
        return markaVozila;
    }

    public void setMarkaVozila(String markaVozila) {
        this.markaVozila = markaVozila;
    }

    public String getModelVozila() {
        return modelVozila;
    }

    public void setModelVozila(String modelVozila) {
        this.modelVozila = modelVozila;
    }

    public int getGodisteVozila() {
        return godisteVozila;
    }

    public void setGodisteVozila(int godisteVozila) {
        this.godisteVozila = godisteVozila;
    }

    public int getZapreminaVozila() {
        return zapreminaVozila;
    }

    public void setZapreminaVozila(int zapreminaVozila) {
        this.zapreminaVozila = zapreminaVozila;
    }

    public int getSnagaVozila() {
        return snagaVozila;
    }

    public void setSnagaVozila(int snagaVozila) {
        this.snagaVozila = snagaVozila;
    }

    public String getVinVozila() {
        return vinVozila;
    }

    public void setVinVozila(String vinVozila) {
        this.vinVozila = vinVozila;
    }

    public String getBrojMotoraVozila() {
        return brojMotoraVozila;
    }

    public void setBrojMotoraVozila(String brojMotoraVozila) {
        this.brojMotoraVozila = brojMotoraVozila;
    }

    public String getVrstaGorivaVozila() {

        return vrstaGorivaVozila;
    }

    public void setVrstaGorivaVozila(String vrstaGorivaVozila) {
        this.vrstaGorivaVozila = vrstaGorivaVozila;
    }

    public String getBojaVozila() {
        return bojaVozila;
    }

    public void setBojaVozila(String bojaVozila) {
        this.bojaVozila = bojaVozila;
    }

    public int getMasaVozila() {
        return masaVozila;
    }

    public void setMasaVozila(int masaVozila) {
        this.masaVozila = masaVozila;
    }

    public int getNajvecaDozvoljenaMasaVozila() {
        return najvecaDozvoljenaMasaVozila;
    }

    public void setNajvecaDozvoljenaMasaVozila(int najvecaDozvoljenaMasaVozila) {
        this.najvecaDozvoljenaMasaVozila = najvecaDozvoljenaMasaVozila;
    }

    public String getDatumPrveRegistracijeVozila() {
        return datumPrveRegistracijeVozila;
    }

    public void setDatumPrveRegistracijeVozila(String datumPrveRegistracijeVozila) {
        this.datumPrveRegistracijeVozila = datumPrveRegistracijeVozila;
    }

    public int getBrojMestaZaSedenje() {
        return brojMestaZaSedenje;
    }

    public void setBrojMestaZaSedenje(int brojMestaZaSedenje) {
        this.brojMestaZaSedenje = brojMestaZaSedenje;
    }

    public int getBrojVrataVozila() {
        return brojVrataVozila;
    }

    public void setBrojVrataVozila(int brojVrataVozila) {
        this.brojVrataVozila = brojVrataVozila;
    }

    public String getNapomeneAutomobila() {
        return napomeneAutomobila;
    }

    public void setNapomeneAutomobila(String napomeneAutomobila) {
        this.napomeneAutomobila = napomeneAutomobila;
    }

    @Override
    public String toString() {
        return "Automobil{" +
                "idAuta=" + idAuta +
                ", idKlijenta=" + idKlijenta +
                ", regOznaka='" + regOznaka + '\'' +
                ", kilomteraza=" + kilomteraza +
                ", vrstaVozila='" + vrstaVozila + '\'' +
                ", markaVozila='" + markaVozila + '\'' +
                ", modelVozila='" + modelVozila + '\'' +
                ", godistelVozila=" + godisteVozila +
                ", zapreminalVozila=" + zapreminaVozila +
                ", snagalVozila=" + snagaVozila +
                ", vinVozila='" + vinVozila + '\'' +
                ", brojMotoraVozila='" + brojMotoraVozila + '\'' +
                ", vrstaGorivaVozila='" + vrstaGorivaVozila + '\'' +
                ", bojaVozila='" + bojaVozila + '\'' +
                ", masaVozila=" + masaVozila +
                ", najvecaDozvoljenaMasaVozila=" + najvecaDozvoljenaMasaVozila +
                ", datumPrveRegistracijeVozila='" + datumPrveRegistracijeVozila + '\'' +
                ", brojMestaZaSedenje=" + brojMestaZaSedenje +
                ", brojVrataVozila=" + brojVrataVozila +
                ", napomeneAutomobila='" + napomeneAutomobila + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAuta, regOznaka, markaVozila, modelVozila,
                godisteVozila, zapreminaVozila, snagaVozila, vinVozila,
                brojMotoraVozila, bojaVozila, masaVozila, najvecaDozvoljenaMasaVozila,
                datumPrveRegistracijeVozila, brojMestaZaSedenje, brojVrataVozila);
    }
}
