
package kysymyspankki.domain;

public class Kurssi {
    
    private int id;
    private String nimi;
    
    public Kurssi(int id, String nimi){
        this.id = id;
        this.nimi = nimi;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getNimi(){
        return this.nimi;
    }
}
