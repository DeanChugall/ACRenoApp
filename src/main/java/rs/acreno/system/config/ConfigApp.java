package rs.acreno.system.config;

import java.io.Serializable;
import java.util.Objects;

/**
 * ConfigApp POJO klasa koja je predvidjena za izmenu i unos konfiguracionih parametara preko GUIa
 *
 * @author Dejan Čugalj (dejan@acreno.rs)
 */
public class ConfigApp implements Serializable {

    private static final long serialVersionUID = 7755070825881292183L;

    private String imeFirme;
    private String verzijaAplikacije;
    private String licencaAplikacije;
    private String licencaPodnozijaAplikacije;
    private String datumObjaveAplikacije;
    private String putanjaDoBazePodataka;
    private String intervalProvereInternetaAplikacije;
    private String spashScreenDelayAplikacije;
    private String spashScreenAboutDelayAplikacije;
    private String putanjaDoGKalendara;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getImeFirme() {
        return imeFirme;
    }

    public void setImeFirme(String imeFirme) {
        this.imeFirme = imeFirme;
    }

    public String getVerzijaAplikacije() {
        return verzijaAplikacije;
    }

    public void setVerzijaAplikacije(String verzijaAplikacije) {
        this.verzijaAplikacije = verzijaAplikacije;
    }

    public String getLicencaAplikacije() {
        return licencaAplikacije;
    }

    public void setLicencaAplikacije(String licencaAplikacije) {
        this.licencaAplikacije = licencaAplikacije;
    }

    public String getLicencaPodnozijaAplikacije() {
        return licencaPodnozijaAplikacije;
    }

    public void setLicencaPodnozijaAplikacije(String licencaPodnozijaAplikacije) {
        this.licencaPodnozijaAplikacije = licencaPodnozijaAplikacije;
    }

    public String getDatumObjaveAplikacije() {
        return datumObjaveAplikacije;
    }

    public void setDatumObjaveAplikacije(String datumObjaveAplikacije) {
        this.datumObjaveAplikacije = datumObjaveAplikacije;
    }

    public String getPutanjaDoBazePodataka() {
        return putanjaDoBazePodataka;
    }

    public void setPutanjaDoBazePodataka(String putanjaDoBazePodataka) {
        this.putanjaDoBazePodataka = putanjaDoBazePodataka;
    }

    public String getIntervalProvereInternetaAplikacije() {
        return intervalProvereInternetaAplikacije;
    }

    public void setIntervalProvereInternetaAplikacije(String intervalProvereInternetaAplikacije) {
        this.intervalProvereInternetaAplikacije = intervalProvereInternetaAplikacije;
    }

    public String getSpashScreenDelayAplikacije() {
        return spashScreenDelayAplikacije;
    }

    public void setSpashScreenDelayAplikacije(String spashScreenDelayAplikacije) {
        this.spashScreenDelayAplikacije = spashScreenDelayAplikacije;
    }

    public String getSpashScreenAboutDelayAplikacije() {
        return spashScreenAboutDelayAplikacije;
    }

    public void setSpashScreenAboutDelayAplikacije(String spashScreenAboutDelayAplikacije) {
        this.spashScreenAboutDelayAplikacije = spashScreenAboutDelayAplikacije;
    }

    public String getPutanjaDoGKalendara() {
        return putanjaDoGKalendara;
    }

    public void setPutanjaDoGKalendara(String putanjaDoGKalendara) {
        this.putanjaDoGKalendara = putanjaDoGKalendara;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigApp configApp = (ConfigApp) o;
        return Objects.equals(imeFirme, configApp.imeFirme) &&
                Objects.equals(verzijaAplikacije, configApp.verzijaAplikacije) &&
                Objects.equals(licencaAplikacije, configApp.licencaAplikacije) &&
                Objects.equals(licencaPodnozijaAplikacije, configApp.licencaPodnozijaAplikacije) &&
                Objects.equals(datumObjaveAplikacije, configApp.datumObjaveAplikacije) &&
                Objects.equals(putanjaDoBazePodataka, configApp.putanjaDoBazePodataka) &&
                Objects.equals(intervalProvereInternetaAplikacije, configApp.intervalProvereInternetaAplikacije) &&
                Objects.equals(spashScreenDelayAplikacije, configApp.spashScreenDelayAplikacije) &&
                Objects.equals(spashScreenAboutDelayAplikacije, configApp.spashScreenAboutDelayAplikacije) &&
                Objects.equals(putanjaDoGKalendara, configApp.putanjaDoGKalendara);
    }

    @Override public int hashCode() {
        return Objects.hash(imeFirme, verzijaAplikacije, licencaAplikacije, licencaPodnozijaAplikacije,
                datumObjaveAplikacije, putanjaDoBazePodataka, intervalProvereInternetaAplikacije,
                spashScreenDelayAplikacije, spashScreenAboutDelayAplikacije, putanjaDoGKalendara);
    }
}
