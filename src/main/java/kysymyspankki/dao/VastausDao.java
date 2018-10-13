package kysymyspankki.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kysymyspankki.database.Database;
import kysymyspankki.domain.Vaihtoehto;
import kysymyspankki.domain.Vastaus;

public class VastausDao implements Dao<Vastaus, Integer>{
    
    private Database database;
    
    public VastausDao(Database database){
        this.database = database;
    }
    
    @Override
    public Vastaus findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Vastaus> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Vastaus> findByKysymys_id(int kysymys_id) throws SQLException {
        List<Vastaus> vastaukset = new ArrayList<>();
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT kysymys_id, vaihtoehto_id FROM Vastaus WHERE kysymys_id = ?");
            stmt.setInt(1, kysymys_id);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                vastaukset.add(new Vastaus(result.getInt("kysymys_id"), result.getInt("vaihtoehto_id")));
            }
            return vastaukset;
        } catch (Exception ex) {
            Logger.getLogger(VastausDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int correctAnswersByKysymys_id(int kysymys_id) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select count(*) AS total from vastaus, kysymys, vaihtoehto where vastaus.kysymys_id=? \n" +
                                                           "AND kysymys.id=vaihtoehto.kysymys_id AND vaihtoehto.id=vastaus.vaihtoehto_id \n" +
                                                           "AND vaihtoehto.oikein=true");
            stmt.setInt(1, kysymys_id);
            ResultSet result = stmt.executeQuery();
            result.next();
            return result.getInt("total");
        } catch (Exception ex) {
            Logger.getLogger(VastausDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int incorrectAnswersByKysymys_id(int kysymys_id) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select count(*) AS total from vastaus, kysymys, vaihtoehto where vastaus.kysymys_id=? \n" +
                                                           "AND kysymys.id=vaihtoehto.kysymys_id AND vaihtoehto.id=vastaus.vaihtoehto_id \n" +
                                                           "AND vaihtoehto.oikein=false");
            stmt.setInt(1, kysymys_id);
            ResultSet result = stmt.executeQuery();
            result.next();
            return result.getInt("total");
        } catch (Exception ex) {
            Logger.getLogger(VastausDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    @Override
    public Vastaus saveOrUpdate(Vastaus object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void save(Vastaus vastaus) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Vastaus (kysymys_id, vaihtoehto_id) VALUES (?, ?)");
            stmt.setInt(1, vastaus.getKysymys_id());
            stmt.setInt(2, vastaus.getVaihtoehto_id());
            stmt.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(VastausDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
