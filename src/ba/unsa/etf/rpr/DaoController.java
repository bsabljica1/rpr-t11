package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class DaoController {
    public Button izmjeniDrzavuBtn;
    public Button dodajDrzavuBtn;
    public Button izmjeniGradBtn;
    public Button dodajGradBtn;
    public TextField prvoPolje;
    public TextField trecePolje;
    public Button obrisiGradBtn;
    public Button obrisiDrzavuBtn;
    public Button ispisiGradoveBtn;
    public GeografijaDAO geo= null;

    public void initialize () {
        geo = GeografijaDAO.getInstance();
    }

    public void izmjeniDrzavu(ActionEvent actionEvent) {
        String pom = prvoPolje.getText();
        Drzava drz=geo.nadjiDrzavu(pom);
        geo.izmijeniDrzava(drz);
    }

    public void dodajDrzavu(ActionEvent actionEvent) {
        String pom=prvoPolje.getText();
        Drzava drz= new Drzava();
        drz.setId(geo.gradovi().size());
        drz.setNaziv(pom);
        drz.setGlavniGrad(null);
        geo.dodajDrzavu(drz);
    }

    public void izmjeniGrad(ActionEvent actionEvent) {
    }

    public void dodajGrad(ActionEvent actionEvent) {
    }

    public void obrisiGrad(ActionEvent actionEvent) {
    }

    public void obrisiDrzavu(ActionEvent actionEvent) {
    }

    public void ispisiGradove(ActionEvent actionEvent) {
       ArrayList<Grad> pom= geo.gradovi();
       for (int i=0; i<pom.size(); i++) System.out.println(pom.get(i).toString());
    }
}
