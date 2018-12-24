package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static GeografijaDAO gDao = GeografijaDAO.getInstance();

    public static String ispisiGradove() {
        ArrayList<Grad> gradovi = gDao.gradovi();
        String s = "";
        for (Grad grad : gradovi)
            s += grad.toString();
        return s;
    }

    public static void glavniGrad() {
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.nextLine();
        Grad grad = gDao.glavniGrad(drzava);
        if (grad != null)
            System.out.println("Glavni grad države " + grad.getDrzava().getNaziv() + " je " + grad.getNaziv());
        else
            System.out.println("Nepostojeća država");
    }

    public static void main(String[] args) {
        GeografijaDAO dao= GeografijaDAO.getInstance();
    }
}
