package rs.acreno.defektaza;

import java.util.Objects;

public class Defektaza {

    private int idDefektaze;
    private int idAuta;
    private String kilometraza;
    private String opisDefektaze;
    private String datumDefektaze;
    private String vreme;
    private String ostaliDetaljiDefektaze;

    public Defektaza() {
    }

    public int getIdDefektaze() {
        return idDefektaze;
    }

    public void setIdDefektaze(int idDefektaze) {
        this.idDefektaze = idDefektaze;
    }

    public int getIdAuta() {
        return idAuta;
    }

    public void setIdAuta(int idAuta) {
        this.idAuta = idAuta;
    }

    public String getKilometraza() {
        return kilometraza;
    }

    public void setKilometraza(String kilometraza) {
        this.kilometraza = kilometraza;
    }

    public String getOpisDefektaze() {
        return opisDefektaze;
    }

    public void setOpisDefektaze(String opisDefektaze) {
        this.opisDefektaze = opisDefektaze;
    }

    public String getDatumDefektaze() {
        return datumDefektaze;
    }

    public void setDatumDefektaze(String datumDefektaze) {
        this.datumDefektaze = datumDefektaze;
    }

    public String getVreme() {
        return vreme;
    }

    public void setVreme(String vreme) {
        this.vreme = vreme;
    }

    public String getOstaliDetaljiDefektaze() {
        return ostaliDetaljiDefektaze;
    }

    public void setOstaliDetaljiDefektaze(String ostaliDetaljiDefektaze) {
        this.ostaliDetaljiDefektaze = ostaliDetaljiDefektaze;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Defektaza defektaza = (Defektaza) o;
        return idAuta == defektaza.idAuta &&
                Objects.equals(opisDefektaze, defektaza.opisDefektaze) &&
                Objects.equals(datumDefektaze, defektaza.datumDefektaze) &&
                Objects.equals(ostaliDetaljiDefektaze, defektaza.ostaliDetaljiDefektaze);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAuta, opisDefektaze, datumDefektaze, ostaliDetaljiDefektaze);
    }
}
