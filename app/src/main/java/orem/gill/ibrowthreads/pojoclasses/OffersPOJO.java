package orem.gill.ibrowthreads.pojoclasses;

/**
 * Created by Dawinder on 14/11/2016.
 */

public class OffersPOJO {

    String id,name,des,stamp,image;

    public OffersPOJO(String id,String name,String des,String stamp,String image){
        this.id=id;
        this.name=name;
        this.des=des;
        this.stamp=stamp;
        this.image=image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }

    public String getStamp() {
        return stamp;
    }

    public String getImage() {
        return image;
    }
}
