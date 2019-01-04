package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {
    private static GeografijaDAO geo = GeografijaDAO.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dao.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("GeografijaDAO");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 760, 190));
        primaryStage.show();
    }

    public static String ispisiGradove() {
        var gradovi = geo.gradovi();
        String result = "";
        for (var grad : gradovi)
            result += grad.toString() + "\n";
        return result;
    }

    public static void glavniGrad() {
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.nextLine();
        Grad grad = geo.glavniGrad(drzava);
        if (grad != null)
            System.out.println("Glavni grad države " + grad.getDrzava().getNaziv() + " je " + grad.getNaziv());
        else
            System.out.println("Nepostojeća država");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
