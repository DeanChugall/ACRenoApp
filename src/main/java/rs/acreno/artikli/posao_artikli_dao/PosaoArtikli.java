package rs.acreno.artikli.posao_artikli_dao;

import java.util.Objects;

public class PosaoArtikli {
    private int idPosaoArtikli;
    private int idRacuna;
    private int idArtikla;
    private double cena;
    private double nabavnaCena;
    private int kolicina;
    private String jedinicaMere;
    private int popust;
    private String opisPosaoArtiklli;
    private String detaljiPosaoArtikli;

    public PosaoArtikli() {
    }

    public PosaoArtikli(int idPosaoArtikli, int idRacuna, int idArtikla, double cena, double nabavnaCena,
                        int kolicina, String jedinicaMere, int popust, String opisPosaoArtiklli,
                        String detaljiPosaoArtikli) {
        this.idPosaoArtikli = idPosaoArtikli;
        this.idRacuna = idRacuna;
        this.idArtikla = idArtikla;
        this.cena = cena;
        this.nabavnaCena = nabavnaCena;
        this.kolicina = kolicina;
        this.jedinicaMere = jedinicaMere;
        this.popust = popust;
        this.opisPosaoArtiklli = opisPosaoArtiklli;
        this.detaljiPosaoArtikli = detaljiPosaoArtikli;
    }

    public int getIdPosaoArtikli() {
        return idPosaoArtikli;
    }

    public void setIdPosaoArtikli(int idPosaoArtikli) {
        this.idPosaoArtikli = idPosaoArtikli;
    }

    public int getIdRacuna() {
        return idRacuna;
    }

    public void setIdRacuna(int idRacuna) {
        this.idRacuna = idRacuna;
    }

    public int getIdArtikla() {
        return idArtikla;
    }

    public void setIdArtikla(int idArtikla) {
        this.idArtikla = idArtikla;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public double getNabavnaCena() {
        return nabavnaCena;
    }

    public void setNabavnaCena(double nabavnaCena) {
        this.nabavnaCena = nabavnaCena;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public String getJedinicaMere() {
        return jedinicaMere;
    }

    public void setJedinicaMere(String jedinicaMere) {
        this.jedinicaMere = jedinicaMere;
    }

    public int getPopust() {
        return popust;
    }

    public void setPopust(int popust) {
        this.popust = popust;
    }

    public String getOpisPosaoArtiklli() {
        return opisPosaoArtiklli;
    }

    public void setOpisPosaoArtiklli(String opisPosaoArtiklli) {
        this.opisPosaoArtiklli = opisPosaoArtiklli;
    }

    public String getDetaljiPosaoArtikli() {
        return detaljiPosaoArtikli;
    }

    public void setDetaljiPosaoArtikli(String detaljiPosaoArtikli) {
        this.detaljiPosaoArtikli = detaljiPosaoArtikli;
    }

    @Override
    public String toString() {
        return "PosaoArtikli{" +
                "idPosaoArtikli=" + idPosaoArtikli +
                ", idRacuna=" + idRacuna +
                ", idArtikla=" + idArtikla +
                ", cena=" + cena +
                ", nabavnaCena=" + nabavnaCena +
                ", kolicina=" + kolicina +
                ", jedinicaMere='" + jedinicaMere + '\'' +
                ", popust=" + popust +
                ", opisPosaoArtiklli='" + opisPosaoArtiklli + '\'' +
                ", detaljiPosaoArtikli='" + detaljiPosaoArtikli + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosaoArtikli that = (PosaoArtikli) o;
        return idRacuna == that.idRacuna &&
                idArtikla == that.idArtikla &&
                Objects.equals(opisPosaoArtiklli, that.opisPosaoArtiklli) &&
                Objects.equals(detaljiPosaoArtikli, that.detaljiPosaoArtikli);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRacuna, idArtikla, opisPosaoArtiklli, detaljiPosaoArtikli);
    }
}
