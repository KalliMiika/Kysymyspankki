
package kysymyspankki.domain;

public class Kysymys {
    
    private int id;
    private int kurssi_id;
    private String aihe;
    private String kysymys;
    
    public Kysymys(int id, int kurssi_id, String aihe, String kysymys){
        this.id = id;
        this.kurssi_id = kurssi_id;
        this.aihe = aihe;
        this.kysymys = kysymys;
    }
    
    public int getId(){
        return this.id;
    }
    
    public int getKurssi_id(){
        return this.kurssi_id;
    }
    
    public String getAihe(){
        return this.aihe;
    }
    
    public String getKysymys(){
        return this.kysymys;
    }
}
