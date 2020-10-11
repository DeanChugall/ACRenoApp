package rs.acreno.racuni;

import java.util.Objects;

public class Racun {

    private int idRacuna;
    private int IdAutomobila;
    private String kilometraza;
    private String datum;
    private String datumPrometa;
    private String datumValute;
    private int popust;
    private String napomeneRacuna;
    private String staJeUradjeno;

    public Racun() {
    }

    public int getIdRacuna() {
        return idRacuna;
    }

    public void setIdRacuna(int idRacuna) {
        this.idRacuna = idRacuna;
    }

    public int getIdAutomobila() {
        return IdAutomobila;
    }

    public void setIdAutomobila(int idAutomobila) {
        IdAutomobila = idAutomobila;
    }

    public String getKilometraza() {
        return kilometraza;
    }

    public void setKilometraza(String kilometraza) {
        this.kilometraza = kilometraza;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getDatumPrometa() {
        return datumPrometa;
    }

    public void setDatumPrometa(String datumPrometa) {
        this.datumPrometa = datumPrometa;
    }

    public String getDatumValute() {
        return datumValute;
    }

    public void setDatumValute(String datumValute) {
        this.datumValute = datumValute;
    }

    public int getPopust() {
        return popust;
    }

    public void setPopust(int popust) {
        this.popust = popust;
    }

    public String getNapomeneRacuna() {
        return napomeneRacuna;
    }

    public void setNapomeneRacuna(String napomeneRacuna) {
        this.napomeneRacuna = napomeneRacuna;
    }


    public String getStaJeUradjeno() {
        return staJeUradjeno;
    }

    public void setStaJeUradjeno(String staJeUradjeno) {
        this.staJeUradjeno = staJeUradjeno;
    }



}


