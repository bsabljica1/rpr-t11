package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static GeografijaDAO dao = GeografijaDAO.getInstance();

    public static String ispisiGradove() {
        ArrayList<Grad> gradovi = dao.gradovi();
        String s = "";
        for (Grad grad : gradovi)
            s += grad.toString();
        return s;
    }

    public static void glavniGrad() {
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.toString();
        Grad grad = dao.glavniGrad(drzava);
        if (grad != null)
            System.out.println("Glavni grad države " + grad.getDrzava().getNaziv() + " je " + grad.getNaziv());
        else
            System.out.println("Nepostojeća država");
    }

    public static void main(String[] args) {
        dao.obrisiDrzavu("Austrija");
        ArrayList<Grad> gradovi = dao.gradovi();
      if  (gradovi.size()==3) System.out.println("dobro");
      else System.out.println(gradovi.size());
       // ("London", gradovi.get(0).getNaziv());
       // ("Pariz", gradovi.get(1).getNaziv());
       // ("Manchester", gradovi.get(2).getNaziv());
    }
}
