package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection conn;
    private PreparedStatement upit;

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    private void napuniPodacima(ArrayList<Grad> gradovi,ArrayList<Drzava> drzave) {
        gradovi.add(new Grad(1, "Pariz", 2206488, null));
        gradovi.add(new Grad(2, "London", 8825000, null));
        gradovi.add(new Grad(3, "Beč", 1899055, null));
        gradovi.add(new Grad(4, "Manchester", 545500, null));
        gradovi.add(new Grad(5, "Graz", 280200, null));
        drzave.add(new Drzava(1, "Francuska", gradovi.get(0)));
        drzave.add(new Drzava(2, "Velika Britanija", gradovi.get(1)));
        drzave.add(new Drzava(3, "Austrija", gradovi.get(2)));
        gradovi.get(0).setDrzava(drzave.get(0));
        gradovi.get(1).setDrzava(drzave.get(1));
        gradovi.get(2).setDrzava(drzave.get(2));
        gradovi.get(3).setDrzava(drzave.get(1));
        gradovi.get(4).setDrzava(drzave.get(2));
    }

    private GeografijaDAO() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        ArrayList<Drzava> drzave = new ArrayList<>();
        napuniPodacima(gradovi, drzave);
        try {
            String url = "jdbc:sqlite:baza.db";
            conn = DriverManager.getConnection(url);
            boolean gradTabelaPostoji = true;
            try {
                upit = conn.prepareStatement("DELETE FROM grad");
                upit.executeUpdate();
            } catch (Exception ignored) {
                upit = conn.prepareStatement("create table grad (id int constraint grad_id_pk primary key, naziv varchar(50), broj_stanovnika int, drzava int)");
                upit.executeUpdate();
                gradTabelaPostoji = false;
                upit = conn.prepareStatement("create table drzava (id int constraint drzava_id_pk primary key, naziv varchar(50), glavni_grad int constraint gl_grad_fk references grad(id) on delete cascade)");
                upit.executeUpdate();
                if (!gradTabelaPostoji) {
                    upit = conn.prepareStatement("alter table grad add constraint drzava_fk foreign key (drzava) references drzava(id) on delete cascade");
                    upit.executeUpdate();
                }
            }
            upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, NULL)");
            for (var grad : gradovi) {
                try {
                    upit.setInt(1, grad.getId());
                    upit.setString(2, grad.getNaziv());
                    upit.setInt(3, grad.getBrojStanovnika());
                    upit.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
            upit = conn.prepareStatement("INSERT  INTO drzava VALUES(?, ?, ?)");
            for (var drzava : drzave) {
                try {
                    upit.setInt(1, drzava.getId());
                    upit.setString(2, drzava.getNaziv());
                    upit.setInt(3, drzava.getGlavniGrad().getId());
                    upit.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
            upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
            for (var grad : gradovi) {
                try {
                    upit.setInt(1, grad.getDrzava().getId());
                    upit.setInt(2, grad.getId());
                    upit.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public static void removeInstance() {
        instance = null;
    }

    public static GeografijaDAO getInstance() {
        if (instance == null) initialize();
        return instance;
    }

    public void obrisiDrzavu(String drzava) {
        try {
            upit = conn.prepareStatement("SELECT g.id FROM grad g, drzava d WHERE g.drzava = d.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            boolean postoji = false;
            while (result.next()) {
                int idGrad = result.getInt(1);
                PreparedStatement podUpit = conn.prepareStatement("DELETE FROM grad WHERE id = ?");
                podUpit.setInt(1, idGrad);
                podUpit.executeUpdate();
                postoji=true;
            }
            if (postoji == false)
                return;
            upit = conn.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            upit.setString(1, drzava);
            upit.executeUpdate();
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }


    public void obrisiGrad(String grad) {
        try {
            upit = conn.prepareStatement("SELECT d.id FROM grad g, drzava d WHERE d.glavni_grad = g.id AND g.naziv = ?");
            upit.setString(1, grad);
            ResultSet result = upit.executeQuery();
            boolean postoji=false;
            while (result.next()) {
                int idDrzava = result.getInt(1);
                PreparedStatement podUpit = conn.prepareStatement("DELETE FROM drzava WHERE id = ?");
                podUpit.setInt(1, idDrzava);
                podUpit.executeUpdate();
                postoji=true;
            }
            if (postoji == false)
                return;
            upit = conn.prepareStatement("DELETE FROM grad WHERE naziv = ?");
            upit.setString(1, grad);
            upit.executeUpdate();
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        try {
            upit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            ResultSet resultGradovi = upit.executeQuery();
            while (resultGradovi.next()) {
                Grad grad = new Grad();
                int idGrad = resultGradovi.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = resultGradovi.getString(2);
                grad.setNaziv(nazivGrad);
                int brojStanovnika = resultGradovi.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int drzavaId = resultGradovi.getInt(4);
                grad.setDrzava(new Drzava(drzavaId, "", null));
                gradovi.add(grad);
            }
            upit = conn.prepareStatement("SELECT * FROM drzava");
            ResultSet resultDrzave = upit.executeQuery();
            while (resultDrzave.next()) {
                Drzava drzava = new Drzava();
                int idDrzava = resultDrzave.getInt(1);
                drzava.setId(idDrzava);
                String nazivDrzave = resultDrzave.getString(2);
                drzava.setNaziv(nazivDrzave);
                int glavniGradId = resultDrzave.getInt(3);
                for (var grad : gradovi) {
                    if (grad.getDrzava().getId() == drzava.getId()) {
                        grad.setDrzava(drzava);
                    }
                    if (glavniGradId == grad.getId())
                        drzava.setGlavniGrad(grad);
                }
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return gradovi;
    }

    public Grad glavniGrad(String drzava) {
        Grad grad = new Grad();
        try {
            upit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            Drzava drzavaFk = new Drzava();
            grad.setDrzava(drzavaFk);
            drzavaFk.setGlavniGrad(grad);
            boolean postoji=false;
            while (result.next()) {
                int idGrad = result.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = result.getString(2);
                grad.setNaziv(nazivGrad);
                int brojStanovnika = result.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int idDrzava = result.getInt(4);
                drzavaFk.setId(idDrzava);
                String nazivDrzave = result.getString(5);
                drzavaFk.setNaziv(nazivDrzave);
                postoji=true;
            }
            if (postoji==false) {
                System.out.println("Data drzava ne postoji");
                return null;
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return grad;
    }

    public Drzava nadjiDrzavu(String drzava) {
        Drzava drzavaResult = new Drzava();
        try {
            upit = conn.prepareStatement("SELECT d.id, d.naziv, g.id, g.naziv, g.broj_stanovnika FROM drzava d, grad g WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            Grad glavniGrad = new Grad();
            drzavaResult.setGlavniGrad(glavniGrad);
            glavniGrad.setDrzava(drzavaResult);
            while (result.next()) {
                int idDrzava = result.getInt(1);
                drzavaResult.setId(idDrzava);
                String nazivDrzave = result.getString(2);
                drzavaResult.setNaziv(nazivDrzave);
                int idGrad = result.getInt(3);
                glavniGrad.setId(idGrad);
                String nazivGrad = result.getString(4);
                glavniGrad.setNaziv(nazivGrad);
                int brojStanovnika = result.getInt(5);
                glavniGrad.setBrojStanovnika(brojStanovnika);
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
            return null;
        }
        return drzavaResult;
    }

    private int dajSljedeciID(String nazivTabele) throws SQLException {
        upit = conn.prepareStatement("SELECT id FROM " + nazivTabele + " WHERE ROWNUM = 1 ORDER BY id DESC");
        var result = upit.executeQuery();
        int id = 0;
        while (result.next())
            id = result.getInt(1);
        return id + 1;
    }

    private int dajGradIDAkoPostoji(String naziv) throws SQLException {
        upit = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ? AND broj_stanovnika IS NULL");
        upit.setString(1, naziv);
        var result = upit.executeQuery();
        int id = -1;
        while (result.next())
            id = result.getInt(1);
        return id;
    }

    public void dodajGrad(Grad grad) {
        try {
            int idAkoPostoji = dajGradIDAkoPostoji(grad.getNaziv());
            if (idAkoPostoji != -1) {
                grad.setId(idAkoPostoji);
                upit = conn.prepareStatement("SELECT id FROM drzava WHERE glavni_grad = ?");
                upit.setInt(1, idAkoPostoji);
                var result = upit.executeQuery();
                int id = -1;
                while (result.next())
                    id = result.getInt(1);
                Drzava temp = new Drzava();
                temp.setId(id);
                grad.setDrzava(temp);
                izmijeniGrad(grad);
                return;
            }
            upit = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            upit.setString(1, grad.getDrzava().getNaziv());
            ResultSet result = upit.executeQuery();
            int brojac = 0;
            int idDrzave = 0;
            while (result.next()) {
                idDrzave = result.getInt(1);
                brojac++;
            }
            int sljedeciIDGrad = dajSljedeciID("grad");
            upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            upit.setInt(1, sljedeciIDGrad);
            upit.setString(2, grad.getNaziv());
            upit.setInt(3, grad.getBrojStanovnika());
            if (brojac == 0)
                upit.setNull(4, Types.INTEGER);
            else
                upit.setInt(4, idDrzave);
            upit.executeUpdate();
            if (brojac == 0) {
                int sljedeciIDDrzava = dajSljedeciID("drzava");
                upit = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
                upit.setInt(1, sljedeciIDDrzava);
                upit.setString(2, grad.getDrzava().getNaziv());
                upit.setInt(3, sljedeciIDGrad);
                upit.executeUpdate();
                upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
                upit.setInt(1, sljedeciIDDrzava);
                upit.setInt(2, sljedeciIDGrad);
                upit.executeUpdate();
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }


    public void dodajDrzavu(Drzava drzava) {

        try {
            upit = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            upit.setString(1, drzava.getGlavniGrad().getNaziv());
            ResultSet result = upit.executeQuery();
            int brojac = 0;
            int idGrada = 0;
            while (result.next()) {
                idGrada = result.getInt(1);
                brojac++;
            }
            int sljedeciIDDrzava = dajSljedeciID("drzava");
            upit = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
            upit.setInt(1, sljedeciIDDrzava);
            upit.setString(2, drzava.getNaziv());
            if (brojac == 0)
                upit.setNull(3, Types.INTEGER);
            else
                upit.setInt(3, idGrada);
            upit.executeUpdate();
            if (brojac == 0) {
                int sljedeciIDGrad = dajSljedeciID("grad");
                upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, NULL, ?)");
                upit.setInt(1, sljedeciIDGrad);
                upit.setString(2, drzava.getGlavniGrad().getNaziv());
                upit.setInt(3, sljedeciIDDrzava);
                upit.executeUpdate();
                upit = conn.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE id = ?");
                upit.setInt(1, sljedeciIDGrad);
                upit.setInt(2, sljedeciIDDrzava);
                upit.executeUpdate();
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            upit = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE id = ?");
            upit.setString(1, grad.getNaziv());
            upit.setInt(2, grad.getBrojStanovnika());
            upit.setInt(3, grad.getDrzava().getId());
            upit.setInt(4, grad.getId());
            int broj = upit.executeUpdate();
            System.out.println("Uspjesno izmjenjen " + broj + " red");
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void izmijeniDrzava(Drzava drzava) {
        try {
            upit = conn.prepareStatement("UPDATE drzava SET naziv = ?, glavni_grad = ? WHERE id = ?");
            upit.setString(1, drzava.getNaziv());
            upit.setInt(2, drzava.getGlavniGrad().getId());
            upit.setInt(3, drzava.getId());
            int broj = upit.executeUpdate();
            System.out.println("Uspjesno izmjenjen " + broj + " red");
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }
}