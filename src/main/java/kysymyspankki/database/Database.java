package kysymyspankki.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            this.databaseAddress = dbUrl;
        }else {
            this.databaseAddress = databaseAddress;
        }
    }

    public Connection getConnection() throws SQLException {
        System.out.println(databaseAddress);
        return DriverManager.getConnection(databaseAddress);
    }
}
