package rs.acreno.nalozi;

import java.util.Objects;

public class RadniNalog {

    private int IdRadnogNaloga;
    private int IdAutomobila;
    private String datum;
    private String vreme;
    private String kilometraza;
    private String detaljiStranke;
    private String detaljiServisera;

    public RadniNalog() {
    }

    public RadniNalog(int idRadnogNaloga, int idAutomobila, String datum, String vreme, String kilometraza, String detaljiStranke, String detaljiServisera) {
        IdRadnogNaloga = idRadnogNaloga;
        IdAutomobila = idAutomobila;
        this.datum = datum;
        this.vreme = vreme;
        this.kilometraza = kilometraza;
        this.detaljiStranke = detaljiStranke;
        this.detaljiServisera = detaljiServisera;
    }

    public int getIdRadnogNaloga() {
        return IdRadnogNaloga;
    }

    public void setIdRadnogNaloga(int idRadnogNaloga) {
        IdRadnogNaloga = idRadnogNaloga;
    }

    public int getIdAutomobila() {
        return IdAutomobila;
    }

    public void setIdAutomobila(int idAutomobila) {
        IdAutomobila = idAutomobila;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getVreme() {
        return vreme;
    }

    public void setVreme(String vreme) {
        this.vreme = vreme;
    }

    public String getKilometraza() {
        return kilometraza;
    }

    public void setKilometraza(String kilometraza) {
        this.kilometraza = kilometraza;
    }

    public String getDetaljiStranke() {
        return detaljiStranke;
    }

    public void setDetaljiStranke(String detaljiStranke) {
        this.detaljiStranke = detaljiStranke;
    }

    public String getDetaljiServisera() {
        return detaljiServisera;
    }

    public void setDetaljiServisera(String detaljiServisera) {
        this.detaljiServisera = detaljiServisera;
    }

    @Override public String toString() {
        return "RadniNalog{" +
                "IdRadnogNaloga=" + IdRadnogNaloga +
                ", IdAutomobila=" + IdAutomobila +
                ", datum='" + datum + '\'' +
                ", vreme='" + vreme + '\'' +
                ", kilometraza='" + kilometraza + '\'' +
                ", detaljiStranke='" + detaljiStranke + '\'' +
                ", detaljiServisera='" + detaljiServisera + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RadniNalog that = (RadniNalog) o;
        return IdRadnogNaloga == that.IdRadnogNaloga &&
                IdAutomobila == that.IdAutomobila &&
                datum.equals(that.datum) &&
                kilometraza.equals(that.kilometraza) &&
                Objects.equals(detaljiStranke, that.detaljiStranke) &&
                Objects.equals(detaljiServisera, that.detaljiServisera);
    }

    @Override public int hashCode() {
        return Objects.hash(IdRadnogNaloga, IdAutomobila, datum, kilometraza, detaljiStranke, detaljiServisera);
    }
}
