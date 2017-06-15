package orem.gill.ibrowthreads.pojoclasses;

/**
 * Created by Dawinder on 16/11/2016.
 */

public class CardPOJO {

    String id,value;

    public CardPOJO(String id,String value){
        this.id=id;
        this.value=value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
