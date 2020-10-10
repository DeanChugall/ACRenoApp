package rs.acreno.system.config;

import java.io.Serializable;
import java.util.Objects;

/**
 * Config ACReno POJO klasa koja je predvidjena za izmenu i unos konfiguracionih parametara preko GUIa
 *
 * @author Dejan Čugalj (dejan@acreno.rs)
 */
public class  ConfigAcreno implements Serializable {

    private static final long serialVersionUID = 7755070825881292183L;

    private String adresaFirme;
    private String gradFirme;
    private String ziroRacunFirme;
    private String telefonFirme;
    private String sajtFirme;
    private String emailFirme;

    public String getAdresaFirme() {
        return adresaFirme;
    }

    public void setAdresaFirme(String adresaFirme) {
        this.adresaFirme = adresaFirme;
    }

    public String getGradFirme() {
        return gradFirme;
    }

    public void setGradFirme(String gradFirme) {
        this.gradFirme = gradFirme;
    }

    public String getZiroRacunFirme() {
        return ziroRacunFirme;
    }

    public void setZiroRacunFirme(String ziroRacunFirme) {
        this.ziroRacunFirme = ziroRacunFirme;
    }

    public String getTelefonFirme() {
        return telefonFirme;
    }

    public void setTelefonFirme(String telefonFirme) {
        this.telefonFirme = telefonFirme;
    }

    public String getSajtFirme() {
        return sajtFirme;
    }

    public void setSajtFirme(String sajtFirme) {
        this.sajtFirme = sajtFirme;
    }

    public String getEmailFirme() {
        return emailFirme;
    }

    public void setEmailFirme(String emailFirme) {
        this.emailFirme = emailFirme;
    }


    @Override public String toString() {
        return "ConfigAcreno{" +
                "adresaFirme='" + adresaFirme + '\'' +
                ", gradFirme='" + gradFirme + '\'' +
                ", ziroRacunFirme='" + ziroRacunFirme + '\'' +
                ", telefonFirme='" + telefonFirme + '\'' +
                ", sajtFirme='" + sajtFirme + '\'' +
                ", emailFirme='" + emailFirme + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigAcreno that = (ConfigAcreno) o;
        return Objects.equals(adresaFirme, that.adresaFirme) &&
                Objects.equals(gradFirme, that.gradFirme) &&
                Objects.equals(ziroRacunFirme, that.ziroRacunFirme) &&
                Objects.equals(telefonFirme, that.telefonFirme) &&
                Objects.equals(sajtFirme, that.sajtFirme) &&
                Objects.equals(emailFirme, that.emailFirme);
    }

    @Override public int hashCode() {
        return Objects.hash(adresaFirme, gradFirme, ziroRacunFirme, telefonFirme, sajtFirme, emailFirme);
    }
}
