package ba.unsa.etf.rpr;

public class Drzava {
    private int id;
    private String naziv;
    Grad glavniGrad;

    public Drzava() {}

    public Drzava (int id, String naziv, Grad glavniGrad) {
        this.id=id;
        this.glavniGrad=glavniGrad;
        this.naziv=naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }

    public Grad getGlavniGrad() {
        return glavniGrad;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
