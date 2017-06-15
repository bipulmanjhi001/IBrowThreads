package orem.gill.ibrowthreads.pojoclasses;

/**
 * Created by Dawinder on 14/11/2016.
 */

public class NotificationsPOJO {

    String id,title,des,date;

    public NotificationsPOJO(String id,String title,String des,String date){
        this.id=id;
        this.title=title;
        this.des=des;
        this.date=date;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDes() {
        return des;
    }

    public String getDate() {
        return date;
    }
}
