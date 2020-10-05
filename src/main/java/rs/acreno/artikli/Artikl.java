package rs.acreno.artikli;

import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;

import java.util.Objects;

public class Artikl extends PosaoArtikli {

    private int idArtikla;
    private String kataloskiBrArtikla;
    private String nazivArtikla;
    private String opisArtikla;
    private String jedinicaMere;
    private int kolicina;
    private double nabavnaCenaArtikla;
    private double cenaArtikla;

    public Artikl() {
    }

    public Artikl(int idArtikla, String kataloskiBrArtikla, String nazivArtikla, String opisArtikla,
                  String jedinicaMere, int kolicina, double nabavnaCenaArtikla, double cenaArtikla) {
        this.idArtikla = idArtikla;
        this.kataloskiBrArtikla = kataloskiBrArtikla;
        this.nazivArtikla = nazivArtikla;
        this.opisArtikla = opisArtikla;
        this.jedinicaMere = jedinicaMere;
        this.kolicina = kolicina;
        this.nabavnaCenaArtikla = nabavnaCenaArtikla;
        this.cenaArtikla = cenaArtikla;
    }

    public int getIdArtikla() {
        return idArtikla;
    }

    public void setIdArtikla(int idArtikla) {
        this.idArtikla = idArtikla;
    }

    public String getKataloskiBrArtikla() {
        return kataloskiBrArtikla;
    }

    public void setKataloskiBrArtikla(String kataloskiBrArtikla) {
        this.kataloskiBrArtikla = kataloskiBrArtikla;
    }

    public String getNazivArtikla() {
        return nazivArtikla;
    }

    public void setNazivArtikla(String nazivArtikla) {
        this.nazivArtikla = nazivArtikla;
    }

    public String getOpisArtikla() {
        return opisArtikla;
    }

    public void setOpisArtikla(String opisArtikla) {
        this.opisArtikla = opisArtikla;
    }

    public String getJedinicaMere() {
        return jedinicaMere;
    }

    public void setJedinicaMere(String jedinicaMere) {
        this.jedinicaMere = jedinicaMere;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public double getNabavnaCenaArtikla() {
        return nabavnaCenaArtikla;
    }

    public void setNabavnaCenaArtikla(double nabavnaCenaArtikla) {
        this.nabavnaCenaArtikla = nabavnaCenaArtikla;
    }

    public double getCenaArtikla() {
        return cenaArtikla;
    }

    public void setCenaArtikla(double cenaArtikla) {
        this.cenaArtikla = cenaArtikla;
    }

    @Override
    public String toString() {
        return "Artikl{" +
                "idArtikla=" + idArtikla +
                ", kataloskiBrArtikla='" + kataloskiBrArtikla + '\'' +
                ", nazivArtikla='" + nazivArtikla + '\'' +
                ", opisArtikla='" + opisArtikla + '\'' +
                ", jedinicaMere='" + jedinicaMere + '\'' +
                ", kolicina=" + kolicina +
                ", nabavnaCenaArtikla=" + nabavnaCenaArtikla +
                ", cenaArtikla=" + cenaArtikla +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artikl artikl = (Artikl) o;
        return idArtikla == artikl.idArtikla &&
                Objects.equals(kataloskiBrArtikla, artikl.kataloskiBrArtikla) &&
                Objects.equals(nazivArtikla, artikl.nazivArtikla) &&
                Objects.equals(opisArtikla, artikl.opisArtikla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArtikla, kataloskiBrArtikla, nazivArtikla, opisArtikla);
    }
}