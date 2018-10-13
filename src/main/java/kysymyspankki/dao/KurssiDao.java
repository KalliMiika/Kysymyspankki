package kysymyspankki.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import kysymyspankki.database.Database;
import kysymyspankki.domain.Kurssi;
import kysymyspankki.domain.Kysymys;

public class KurssiDao implements Dao<Kurssi, Integer>{

    private Database database;
    private KysymysDao kysymysDao;
    
    public KurssiDao(Database database, KysymysDao kysymysDao){
        this.database = database;
        this.kysymysDao = kysymysDao;
    }
    
    @Override
    public Kurssi findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Kurssi> findAll() throws SQLException {
        List<Kurssi> kurssit = new ArrayList<>();

        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, nimi FROM Kurssi").executeQuery()) {

            while (result.next()) {
                kurssit.add(new Kurssi(result.getInt("id"), result.getString("nimi")));
            }
        }

        return kurssit;
    }

    @Override
    public Kurssi saveOrUpdate(Kurssi kurssi) throws SQLException {
        Kurssi byName = findByName(kurssi.getNimi());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kurssi (nimi) VALUES (?)");
            stmt.setString(1, kurssi.getNimi());
            stmt.executeUpdate();
        }

        return findByName(kurssi.getNimi());
    }
    
    private Kurssi findByName(String nimi) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM Kurssi WHERE nimi = ?");
            stmt.setString(1, nimi);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Kurssi(result.getInt("id"), result.getString("nimi"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            //poistetaan kaikki kurssiin liittyvät kysymykset;
            for(Kysymys kys : kysymysDao.findByKurssi_id(key)){
                kysymysDao.delete(kys.getId());
            }
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kurssi WHERE id = ?");
            stmt.setInt(1, key);
            stmt.executeUpdate();
        }
    }
}
