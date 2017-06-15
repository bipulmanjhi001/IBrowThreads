package orem.gill.ibrowthreads.pojoclasses;

/**
 * Created by Dawinder on 14/11/2016.
 */

public class ServicesPOJO {

    String id,name,des,image,webUrl;

    public ServicesPOJO(String id,String name,String des,String image,String webUrl){
        this.id=id;
        this.name=name;
        this.des=des;
        this.image=image;
        this.webUrl=webUrl;
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

    public String getImage() {
        return image;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
