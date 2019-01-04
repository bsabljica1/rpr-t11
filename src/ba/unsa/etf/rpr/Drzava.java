package ba.unsa.etf.rpr;

public class Drzava {
    private int id;
    private String naziv;
    Grad grad;

    public Drzava() {}

    public Drzava (int id, String naziv, Grad grad) {
        this.id=id;
        this.grad=grad;
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

    public void setGlavniGrad(Grad grad) {
        this.grad = grad;
    }

    public Grad getGlavniGrad() {
        return grad;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
