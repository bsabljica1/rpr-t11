package ba.unsa.etf.rpr;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance=null;
    private Connection conn;
    private PreparedStatement query;
    private ArrayList<Grad> gradovi;
    private ArrayList<Drzava> drzave;

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    public static GeografijaDAO getInstance() {
        initialize();
        return instance;
    }

    public Grad glavniGrad (String drzava) {
        try {
            query = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.glavni_grad = g.id AND d.naziv = "+ drzava);
            ResultSet result = query.executeQuery();
            Grad grad = new Grad();
            Drzava nadjenaDrzava = new Drzava();
            grad.setDrzava(nadjenaDrzava);
            nadjenaDrzava.setGlavniGrad(grad);
            boolean imaDrzave = false;
            while (result.next()) {
                imaDrzave = true;
                int idGrada = result.getInt(1);
                String nazivGrada = result.getString(2);
                int brojStanovnika = result.getInt(3);
                int idDrzave = result.getInt(4);
                String nazivDrzave = result.getString(5);
                grad.setId(idGrada);
                grad.setNaziv(nazivGrada);
                grad.setBrojStanovnika(brojStanovnika);
                nadjenaDrzava.setId(idDrzave);
                nadjenaDrzava.setNaziv(nazivDrzave);
            }
            if (imaDrzave) {
                return grad;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public void obrisiDrzavu(String drzava) {
        try {
            int idDrzave = 0;
            query = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            query.setString(1, drzava);
            ResultSet result = query.executeQuery();
            boolean imaDrzave = false;
            while (result.next()) {
                imaDrzave = true;
                idDrzave = result.getInt(1);
            }
            if (!imaDrzave) return;
            query = conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
            query.setInt(1, idDrzave);
            query.execute();
            query= conn.prepareStatement("DELETE FROM drzava WHERE id=?");
            query.setInt(1, idDrzave);
            query.execute();
        } catch (SQLException ignored) {
            System.out.println("Ne postoji drzava!");
        }
    }



    public Drzava nadjiDrzavu(String nazivDrzave) {
        Drzava trazenaDrzava = new Drzava();
        try {
            query = conn.prepareStatement("SELECT G.id, G.naziv, G.broj_stanovnika, D.id, D.naziv FROM grad G, drzava D WHERE  G.id = D.glavni_grad  AND D.naziv = " + nazivDrzave);
            ResultSet result = query.executeQuery();
            Grad glavniGrad = new Grad();
            trazenaDrzava.setGlavniGrad(glavniGrad);
            glavniGrad.setDrzava(trazenaDrzava);
            while (result.next()) {
                int drzavaId = result.getInt(1);
                trazenaDrzava.setId(drzavaId);
                String drzavaNaziv = result.getString(2);
                trazenaDrzava.setNaziv(drzavaNaziv);
                int gradId = result.getInt(3);
                glavniGrad.setId(gradId);
                String gradNaziv = result.getString(4);
                glavniGrad.setNaziv(gradNaziv);
                int gradBrojStanovnika = result.getInt(5);
                glavniGrad.setBrojStanovnika(gradBrojStanovnika);
            }
            result.close();
        } catch (SQLException ignored) {
            System.out.println("Data drzava ne postoji");
            return null;
        }
        return trazenaDrzava;
    }

    private void izmijeniGrad( Grad g ) {

        try {
            query = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE id = ?");
            query.setString(1, g.getNaziv() );
            query.setInt(2, g.getBrojStanovnika() );
            query.setInt(3, g.getDrzava().getId() );
            query.setInt(4, g.getId() );
            int redniBrojReda = query.executeUpdate();
            System.out.println( "Uspjesno izmjenjen " +  redniBrojReda + " red" );
        } catch (SQLException ignored) {
            System.out.println( "Dati grad ne postoji" );
        }
    }

    public void dodajGrad(Grad grad) {
        try {
            int idAP = -1;
            query = conn.prepareStatement("SELECT id FROM grad WHERE broj_stanovnika IS NULL AND naziv = ? ");
            query.setString(1, grad.getNaziv());
            ResultSet result = query.executeQuery();
            while ( result.next() )
                idAP = result.getInt(1);
            if (idAP != -1) {
                grad.setId(idAP);
                query = conn.prepareStatement("SELECT id FROM drzava WHERE glavni_grad = ?");
                query.setInt(1, idAP);
                ResultSet resultOne = query.executeQuery();
                int id = -1;
                while (resultOne.next())
                    id = resultOne.getInt(1);
                Drzava tempState = new Drzava();
                tempState.setId( id );
                grad.setDrzava( tempState) ;
                izmijeniGrad( grad );
                resultOne.close();
                return;
            }
            query = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            query.setString(1, grad.getDrzava().getNaziv());
            ResultSet resultTwo = query.executeQuery();
            int stateCounter = 0;
            int idDrzave = 0;
            while (resultTwo.next()) {
                idDrzave = resultTwo.getInt(1);
                stateCounter++;
            }
            int sljedeciIDGrad = 0;
            query = conn.prepareStatement("SELECT id FROM grad ORDER BY id DESC LIMIT 1");
            ResultSet tempResult = query.executeQuery();
            while (tempResult.next())
                sljedeciIDGrad = tempResult.getInt(1);
            sljedeciIDGrad++;
            query = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            query.setInt(1, sljedeciIDGrad);
            query.setString(2, grad.getNaziv());
            query.setInt(3, grad.getBrojStanovnika());
            if (stateCounter == 0)
                query.setNull(4, Types.INTEGER);
            else
                query.setInt(4, idDrzave);
            query.executeUpdate();
            if ( stateCounter == 0) {
                int sljedeciIDDrzava = 0;
                query = conn.prepareStatement("SELECT id FROM drzava ORDER BY id DESC LIMIT 1");
                ResultSet tempResultTwo = query.executeQuery();
                while (tempResultTwo.next())
                    sljedeciIDDrzava = tempResultTwo.getInt(1);
                sljedeciIDDrzava++;
                query = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
                query.setInt(1, sljedeciIDDrzava);
                query.setString(2, grad.getDrzava().getNaziv());
                query.setInt(3, sljedeciIDGrad);
                query.executeUpdate();
            }
            result.close();
            resultTwo.close();
            tempResult.close();
        } catch (SQLException ignored) {
            System.out.println("Greska");
        }
    }

    public void dodajDrzavu( Drzava d ) {
        try{
            query = conn.prepareStatement("SELECT id FROM drzava WHERE id = " + d.getId() );
            ResultSet redovi = query.executeQuery();
            if( redovi.getFetchSize() != 0 ) return; //Drzava vec postoji
            Grad dGlavniGrad = d.getGlavniGrad();
            query = conn.prepareStatement("SELECT id FROM grad WHERE id = " + dGlavniGrad.getId() );
            redovi = query.executeQuery();
            if( redovi.getFetchSize() == 0 ){
                query = conn.prepareStatement("INSERT INTO grad VALUES (?,?,?,NULL)");
                query.setInt(1, dGlavniGrad.getId() );
                query.setString(2, dGlavniGrad.getNaziv() );
                query.setInt(3, dGlavniGrad.getBrojStanovnika() );
                query.executeQuery();
                query = conn.prepareStatement("INSERT INTO drzava VALUES (?,?,?)");
                query.setInt(1,d.getId() );
                query.setString(1,d.getNaziv() );
                query.setInt(3, dGlavniGrad.getId() );
                query.executeQuery();
                query = conn.prepareStatement("UPDATE grad SET id = " + d.getId() );
            }
            else{
                query = conn.prepareStatement("SELECT id FROM grad WHERE id = " + dGlavniGrad.getId() );
                redovi = query.executeQuery();
                int drzavaId = -1;
                while( redovi.next() ){
                    drzavaId = redovi.getInt(1);
                }
                query = conn.prepareStatement("INSERT INTO drzava VALUES (?,?,?)");
                query.setInt(1,d.getId() );
                query.setString(2,d.getNaziv() );
                query.setInt(3,drzavaId );
                query.executeQuery();
            }
        }catch ( SQLException e ){
            System.out.println( e.getMessage() );
        }
    }

    ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradoviUBazi = new ArrayList<>();
        try {
            query = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            ResultSet result = query.executeQuery();
            while (result.next()) {
                Grad grad = new Grad();
                Drzava d = new Drzava();
                int idGrada = result.getInt(1);
                String nazivGrada = result.getString(2);
                int brojStanovnika = result.getInt(3);
                int idDrzave = result.getInt(4);
                grad.setId(idGrada);
                grad.setNaziv(nazivGrada);
                grad.setBrojStanovnika(brojStanovnika);
                d.setId(idDrzave); //ostali podaci za drzavu su nebitni sad
                grad.setDrzava(d);
                gradoviUBazi.add(grad);
            }
            query = conn.prepareStatement("SELECT * FROM drzava");
            result = query.executeQuery();
            while (result.next()) {
                Drzava d = new Drzava();
                int idDrzave = result.getInt(1);
                String nazivDrzave = result.getString(2);
                d.setId(idDrzave);
                d.setNaziv(nazivDrzave);
                int idGlavnogGrada = result.getInt(3);
                for (Grad grad : gradoviUBazi) {
                    if (grad.getDrzava().getId() == d.getId()) {
                        grad.setDrzava(d);
                    }
                    if (idGlavnogGrada == grad.getId()) {
                        d.setGlavniGrad(grad);
                    }
                }
            }
        } catch (SQLException ignored) {
            return null;
        }
        return gradoviUBazi;
    }


}
