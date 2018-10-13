package kysymyspankki.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import kysymyspankki.database.Database;
import kysymyspankki.domain.Kysymys;
import kysymyspankki.domain.Vaihtoehto;

public class VaihtoehtoDao implements Dao<Vaihtoehto, Integer>{
    
    private Database database;
    
    public VaihtoehtoDao(Database database){
        this.database = database;
    }
    
    @Override
    public Vaihtoehto findOne(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kysymys_id, vastaus, oikein FROM Vaihtoehto WHERE id = ?");
            stmt.setInt(1, key);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Vaihtoehto(result.getInt("id"), result.getInt("kysymys_id"), result.getString("vastaus"), result.getBoolean("oikein"));
        }

    }
    
    public List<Vaihtoehto> findByKysymys_id(int kysymys_id) throws SQLException {
        List<Vaihtoehto> vaihtoehdot = new ArrayList<>();
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kysymys_id, vastaus, oikein FROM Vaihtoehto WHERE kysymys_id = ?");
            stmt.setInt(1, kysymys_id);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                vaihtoehdot.add(new Vaihtoehto(result.getInt("id"), result.getInt("kysymys_id"), result.getString("vastaus"), result.getBoolean("oikein")));
            }
            return vaihtoehdot;
        }
    }


    @Override
    public List<Vaihtoehto> findAll() throws SQLException {
        List<Vaihtoehto> vaihtoehdot = new ArrayList<>();

        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, kysymys_id, vastaus, oikein FROM Vaihtoehto").executeQuery()) {

            while (result.next()) {
                vaihtoehdot.add(new Vaihtoehto(result.getInt("id"), result.getInt("kysymys_id"), result.getString("vastaus"), result.getBoolean("oikein")));
            }
        }

        return vaihtoehdot;
    }

    @Override
    public Vaihtoehto saveOrUpdate(Vaihtoehto vaihtoehto) throws SQLException {
        Vaihtoehto byName = findByVastausAndKysymys_id(vaihtoehto.getVastaus(), vaihtoehto.getKysymys_id());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Vaihtoehto (kysymys_id, vastaus, oikein) VALUES (?, ?, ?)");
            stmt.setInt(1, vaihtoehto.getKysymys_id());
            stmt.setString(2, vaihtoehto.getVastaus());
            stmt.setBoolean(3, vaihtoehto.isOikein());
            stmt.executeUpdate();
        }

        return findByVastausAndKysymys_id(vaihtoehto.getVastaus(), vaihtoehto.getKysymys_id());
    }

    private Vaihtoehto findByVastausAndKysymys_id(String vastaus, int kysymys_id) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, kysymys_id, vastaus, oikein FROM Vaihtoehto WHERE vastaus = ? AND kysymys_id = ?");
            stmt.setString(1, vastaus);
            stmt.setInt(2, kysymys_id);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Vaihtoehto(result.getInt("id"), result.getInt("kysymys_id"), result.getString("vastaus"), result.getBoolean("oikein"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            //Poistetaan kaikki vastausvaihtoehtoon liittyvät vastaukset
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Vastaus WHERE vaihtoehto_id = ?");
            stmt.setInt(1, key);
            stmt.executeUpdate();
            stmt = conn.prepareStatement("DELETE FROM Vaihtoehto WHERE id = ?");
            stmt.setInt(1, key);
            stmt.executeUpdate();
        }
    }
    
}
