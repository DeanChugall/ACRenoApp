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

    @Override public String toString() {
        return "Racun{" +
                "idRacuna=" + idRacuna +
                ", IdAutomobila=" + IdAutomobila +
                ", kilometraza='" + kilometraza + '\'' +
                ", datum='" + datum + '\'' +
                ", datumPrometa='" + datumPrometa + '\'' +
                ", datumValute='" + datumValute + '\'' +
                ", popust=" + popust +
                ", napomeneRacuna='" + napomeneRacuna + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Racun racun = (Racun) o;
        return idRacuna == racun.idRacuna &&
                IdAutomobila == racun.IdAutomobila &&
                popust == racun.popust &&
                Objects.equals(datum, racun.datum) &&
                Objects.equals(napomeneRacuna, racun.napomeneRacuna);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRacuna, IdAutomobila, datum, popust, napomeneRacuna);
    }
}


