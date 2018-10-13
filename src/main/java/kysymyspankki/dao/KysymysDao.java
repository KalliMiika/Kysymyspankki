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
import kysymyspankki.domain.Vaihtoehto;

public class KysymysDao implements Dao<Kysymys, Integer>{
    
    private Database database;
    private VaihtoehtoDao vaihtoehtoDao;
    
    public KysymysDao(Database database, VaihtoehtoDao vaihtoehtoDao){
        this.database = database;
        this.vaihtoehtoDao = vaihtoehtoDao;
    }
    
    @Override
    public Kysymys findOne(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kurssi_id, aihe, kysymys FROM Kysymys WHERE id = ?");
            stmt.setInt(1, key);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Kysymys(result.getInt("id"), result.getInt("kurssi_id"), result.getString("aihe"), result.getString("kysymys"));
        }
    }

    public List<Kysymys> findByKurssi_id(int kurssi_id) throws SQLException {
        List<Kysymys> kysymykset = new ArrayList<>();
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kurssi_id, aihe, kysymys FROM Kysymys WHERE kurssi_id = ?");
            stmt.setInt(1, kurssi_id);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                kysymykset.add(new Kysymys(result.getInt("id"), result.getInt("kurssi_id"), result.getString("aihe"), result.getString("kysymys")));
            }
            return kysymykset;
        }
    }

    @Override
    public List<Kysymys> findAll() throws SQLException {
        List<Kysymys> kysymykset = new ArrayList<>();

        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, kurssi_id, aihe, kysymys FROM Kysymys ORDER BY kurssi_id").executeQuery()) {

            while (result.next()) {
                kysymykset.add(new Kysymys(result.getInt("id"), result.getInt("kurssi_id"), result.getString("aihe"), result.getString("kysymys")));
            }
        }

        return kysymykset;
    }

    @Override
    public Kysymys saveOrUpdate(Kysymys kysymys) throws SQLException {
        Kysymys byName = findByAiheAndKysymys(kysymys.getAihe(), kysymys.getKysymys());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kysymys (kurssi_id, aihe, kysymys) VALUES (?, ?, ?)");
            stmt.setInt(1, kysymys.getKurssi_id());
            stmt.setString(2, kysymys.getAihe());
            stmt.setString(3, kysymys.getKysymys());
            stmt.executeUpdate();
        }

        return findByAiheAndKysymys(kysymys.getAihe(), kysymys.getKysymys());
    }

    private Kysymys findByAiheAndKysymys(String aihe, String kysymys) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kurssi_id, aihe, kysymys FROM Kysymys WHERE aihe = ? AND kysymys = ?");
            stmt.setString(1, aihe);
            stmt.setString(2, kysymys);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Kysymys(result.getInt("id"), result.getInt("kurssi_id"), result.getString("aihe"), result.getString("kysymys"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            //poistetaan kaikki kysymykseen liittyvät vastausvaihtoehdot;
            for(Vaihtoehto vai : vaihtoehtoDao.findByKysymys_id(key)){
                vaihtoehtoDao.delete(vai.getId());
            }
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kysymys WHERE id = ?");
            stmt.setInt(1, key);
            stmt.executeUpdate();
        }
    }

}
