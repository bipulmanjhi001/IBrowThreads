package orem.gill.ibrowthreads.pojoclasses;

/**
 * Created by Dawinder on 14/11/2016.
 */

public class ShopsPOJO {

    String id,name,email,des,address,contact,lat,lng,image,sunTime,monTime,tuesTime,wedTime,thurTime,friTime,satTime;

    public ShopsPOJO(String id,String name,String email,String des,String address,String contact,String lat,String lng,String image,
                     String sunTime,String monTime,String tuesTime,String wedTime,String thurTime,String friTime,String satTime){
        this.id=id;
        this.name=name;
        this.email=email;
        this.des=des;
        this.address=address;
        this.contact=contact;
        this.lat=lat;
        this.lng=lng;
        this.image=image;
        this.sunTime=sunTime;
        this.monTime=monTime;
        this.tuesTime=tuesTime;
        this.wedTime=wedTime;
        this.thurTime=thurTime;
        this.friTime=friTime;
        this.satTime=satTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDes() {
        return des;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getImage() {
        return image;
    }

    public String getSunTime() {
        return sunTime;
    }

    public String getMonTime() {
        return monTime;
    }

    public String getTuesTime() {
        return tuesTime;
    }

    public String getWedTime() {
        return wedTime;
    }

    public String getThurTime() {
        return thurTime;
    }

    public String getFriTime() {
        return friTime;
    }

    public String getSatTime() {
        return satTime;
    }
}
