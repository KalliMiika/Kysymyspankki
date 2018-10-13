
package kysymyspankki;

import spark.*;
import java.sql.*;
import java.util.*;
import kysymyspankki.database.Database;
import spark.template.thymeleaf.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = getDatabase();
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        Spark.get("/*", (req, res) -> {
            HashMap map = new HashMap<>();

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

    }

    public static Database getDatabase() throws Exception {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            return new Database(dbUrl);
        }
        return new Database("jdbc:sqlite:kysymyspankki.db");
    }
}
