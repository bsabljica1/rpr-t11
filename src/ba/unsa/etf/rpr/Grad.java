package ba.unsa.etf.rpr;

public class Grad {
    private int id;
    private String naziv;
    private int brojStanovnika;
    Drzava drzava;

    public Grad() {}

    public Grad (int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.id=id;
        this.naziv=naziv;
        this.brojStanovnika=brojStanovnika;
        this.drzava=drzava;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
