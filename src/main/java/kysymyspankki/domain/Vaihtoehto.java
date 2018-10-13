
package kysymyspankki.domain;

public class Vaihtoehto {
    
    private int id;
    private int kysymys_id;
    private String vastaus;
    private boolean oikein;
    
    public Vaihtoehto(int id, int kysymys_id, String vastaus, boolean oikein){
        this.id = id;
        this.kysymys_id = kysymys_id;
        this.vastaus = vastaus;
        this.oikein = oikein;
    }
    
    public int getId(){
        return this.id;
    }
    
    public int getKysymys_id(){
        return this.kysymys_id;
    }
    
    public String getVastaus(){
        return this.vastaus;
    }
    
    public boolean isOikein(){
        return this.oikein;
    }
}
