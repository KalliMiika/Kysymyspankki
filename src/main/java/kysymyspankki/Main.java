
package kysymyspankki;

import spark.*;
import java.util.*;
import kysymyspankki.dao.KurssiDao;
import kysymyspankki.dao.KysymysDao;
import kysymyspankki.dao.VaihtoehtoDao;
import kysymyspankki.dao.VastausDao;
import kysymyspankki.database.Database;
import kysymyspankki.domain.Kurssi;
import kysymyspankki.domain.Kysymys;
import kysymyspankki.domain.Vaihtoehto;
import kysymyspankki.domain.Vastaus;
import spark.template.thymeleaf.*;


public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        Database database = new Database("jdbc:sqlite:kysymyspankki.db");
        VaihtoehtoDao vaihtoehtoDao = new VaihtoehtoDao(database);
        VastausDao vastausDao = new VastausDao(database);
        KysymysDao kysymysDao = new KysymysDao(database, vaihtoehtoDao);
        KurssiDao kurssiDao = new KurssiDao(database, kysymysDao);
        
        //Spark.Get
        Spark.get("/ohjuspaneeli", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kurssit", kurssiDao.findAll());
            map.put("kysymykset", kysymysDao.findAll());
            return new ModelAndView(map, "ohjuspaneeli");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/kurssihallinta", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kurssit", kurssiDao.findAll());
            return new ModelAndView(map, "kurssihallinta");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/kysymyshallinta/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kysymys", kysymysDao.findOne(Integer.parseInt(req.params(":id"))));
            map.put("vastaukset", vaihtoehtoDao.findByKysymys_id(Integer.parseInt(req.params(":id"))));
            return new ModelAndView(map, "kysymyshallinta");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/kysymys/:id/tulokset", (req, res) -> {
            HashMap map = new HashMap<>();
            int kysymys_id = Integer.parseInt(req.params(":id"));
            List<Vastaus> vastaukset = vastausDao.findByKysymys_id(kysymys_id);
            int correct = vastausDao.correctAnswersByKysymys_id(kysymys_id);
            int incorrect = vastausDao.incorrectAnswersByKysymys_id(kysymys_id);
            int total = correct + incorrect;
            double cPer = (double)correct/total;
            double incPer = (double)incorrect/total;
            cPer = cPer*100;
            incPer = incPer*100;
            map.put("correct", (int)cPer);
            map.put("incorrect", (int)incPer);
            map.put("kysymys_id", kysymys_id);
            return new ModelAndView(map, "tulokset");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/kysymys/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kysymys", kysymysDao.findOne(Integer.parseInt(req.params(":id"))));
            map.put("vaihtoehdot", vaihtoehtoDao.findByKysymys_id(Integer.parseInt(req.params(":id"))));
            return new ModelAndView(map, "kysymys");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/*", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kurssit", kurssiDao.findAll());
            map.put("kysymykset", kysymysDao.findAll());
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        //Spark.post
        Spark.post("/kurssi/poista", (req, res) -> {
            kurssiDao.delete(Integer.parseInt(req.queryParams("nimi")));
            res.redirect("/kurssihallinta");
            return "";
        });
        Spark.post("/kysymys/poista", (req, res) -> {
            kysymysDao.delete(Integer.parseInt(req.queryParams("nimi")));
            res.redirect("/ohjuspaneeli");
            return "";
        });
        Spark.post("/kysymyshallinta/:id/poista", (req, res) -> {
            int kysymys_id = Integer.parseInt(req.params(":id"));
            vaihtoehtoDao.delete(Integer.parseInt(req.queryParams("nimi")));
            res.redirect("/kysymyshallinta/"+kysymys_id);
            return "";
        });
        Spark.post("/kurssi", (req, res) -> {
            if(req.queryParams("nimi").length() > 0){
                kurssiDao.saveOrUpdate(new Kurssi(-1, req.queryParams("nimi")));
            }
            res.redirect("/kurssihallinta");
            return "";
        });
        Spark.post("/ohjuspaneeli", (req, res) -> {
            String aihe = req.queryParams("aihe");
            String kysymys = req.queryParams("kysymys");
            int kurssi_id = Integer.parseInt(req.queryParams("kurssi"));
            if(aihe.length()>0 && kysymys.length()>0){
                kysymysDao.saveOrUpdate(new Kysymys(-1, kurssi_id, aihe, kysymys));
            }
            res.redirect("/ohjuspaneeli");
            return "";
        });
        Spark.post("/kysymyshallinta/:id", (req, res) -> {
            int kysymys_id = Integer.parseInt(req.params(":id"));
            String vastaus = req.queryParams("vastaus");
            boolean oikein = Boolean.parseBoolean(req.queryParams("oikein"));
            if(vastaus.length()>0){
                vaihtoehtoDao.saveOrUpdate(new Vaihtoehto(-1, kysymys_id, vastaus, oikein));
            }
            res.redirect("/kysymyshallinta/"+kysymys_id);
            return "";
        });
        Spark.post("/kysymys/:id", (req, res) -> {
            int kysymys_id = Integer.parseInt(req.params(":id"));
            int vaihtoehto_id = Integer.parseInt(req.queryParams("vastaus"));
            vastausDao.save(new Vastaus(kysymys_id, vaihtoehto_id));
            res.redirect("/kysymys/"+kysymys_id);
            return "";
        });
    }
}
