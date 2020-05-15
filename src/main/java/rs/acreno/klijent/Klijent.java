package rs.acreno.klijent;

import java.util.Objects;

public class Klijent {

    private int IdKlijenta;
    private String imePrezime;
    private String mesto;
    private String postanskiBroj;
    private String ulicaBroj;
    private String brLicneKarte;
    private String maticniBroj;
    private String ostaliDetalji;
    private String email;
    private String telefonMobilni;
    private String telefonFiksni;
    private String web;
    private String brojRacuna;
    private String banka;

    public Klijent() {
    }

    public Klijent(String imePrezime, String mesto, String ulicaBroj) {
        this.imePrezime = imePrezime;
        this.mesto = mesto;
        this.ulicaBroj = ulicaBroj;
    }

    public int getIdKlijenta() {
        return IdKlijenta;
    }

    public void setIdKlijenta(int idKlijenta) {
        IdKlijenta = idKlijenta;
    }

    public String getImePrezime() {
        return imePrezime;
    }

    public void setImePrezime(String imePrezime) {
        this.imePrezime = imePrezime;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public String getPostanskiBroj() {
        return postanskiBroj;
    }

    public void setPostanskiBroj(String postanskiBroj) {
        this.postanskiBroj = postanskiBroj;
    }

    public String getUlicaBroj() {
        return ulicaBroj;
    }

    public void setUlicaBroj(String ulicaBroj) {
        this.ulicaBroj = ulicaBroj;
    }

    public String getBrLicneKarte() {
        return brLicneKarte;
    }

    public void setBrLicneKarte(String brLicneKarte) {
        this.brLicneKarte = brLicneKarte;
    }

    public String getMaticniBroj() {
        return maticniBroj;
    }

    public void setMaticniBroj(String maticniBroj) {
        this.maticniBroj = maticniBroj;
    }

    public String getOstaliDetalji() {
        return ostaliDetalji;
    }

    public void setOstaliDetalji(String ostaliDetalji) {
        this.ostaliDetalji = ostaliDetalji;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonMobilni() {
        return telefonMobilni;
    }

    public void setTelefonMobilni(String telefonMobilni) {
        this.telefonMobilni = telefonMobilni;
    }

    public String getTelefonFiksni() {
        return telefonFiksni;
    }

    public void setTelefonFiksni(String telefonFiksni) {
        this.telefonFiksni = telefonFiksni;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getBrojRacuna() {
        return brojRacuna;
    }

    public void setBrojRacuna(String brojRacuna) {
        this.brojRacuna = brojRacuna;
    }

    public String getBanka() {
        return banka;
    }

    public void setBanka(String banka) {
        this.banka = banka;
    }

    @Override
    public String toString() {
        return "Klijent{" +
                "IdKlijenta=" + IdKlijenta +
                ", imePrezime='" + imePrezime + '\'' +
                ", mesto='" + mesto + '\'' +
                ", postanskiBroj=" + postanskiBroj +
                ", ulicaBroj='" + ulicaBroj + '\'' +
                ", brLicneKarte='" + brLicneKarte + '\'' +
                ", maticniBroj=" + maticniBroj +
                ", ostaliDetalji='" + ostaliDetalji + '\'' +
                ", email='" + email + '\'' +
                ", telefonMobilni='" + telefonMobilni + '\'' +
                ", telefonFiksni='" + telefonFiksni + '\'' +
                ", web='" + web + '\'' +
                ", brojRacuna='" + brojRacuna + '\'' +
                ", banka='" + banka + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klijent klijent = (Klijent) o;
        return IdKlijenta == klijent.IdKlijenta &&
                imePrezime.equals(klijent.imePrezime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IdKlijenta, imePrezime);
    }
}
