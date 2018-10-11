
package kysymyspankki;

import spark.Spark;

public class Main {
    
    public static void main(String[] args){
        Spark.get("*", (req, res) -> {
            return "Hei maailma!";
        });

    }
}
