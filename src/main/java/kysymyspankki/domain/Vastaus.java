package kysymyspankki.domain;

public class Vastaus {
    
    private int kysymys_id;
    private int vaihtoehto_id;
    
    public Vastaus(int kysymys_id, int vaihtoehto_id){
        this.kysymys_id = kysymys_id;
        this.vaihtoehto_id = vaihtoehto_id;
    }
    
    public int getKysymys_id(){
        return this.kysymys_id;
    }
    
    public int getVaihtoehto_id(){
        return this.vaihtoehto_id;
    }
}
