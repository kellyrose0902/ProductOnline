package com.uwp.kelly.productonline;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kelly on 11/13/2015.
 */
public class Product {
     int id;
     String title;
     Float price;
     int quantity;
     String photoPath = null;
     String dateString;
     Date dateTime;
     Bitmap bitmap = null;

    public Product(int id,String title, Float price, int quantity, String dateString){
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.dateString = dateString;
        this.dateTime = formatTime(dateString);

    }

    public Product(String title, Float price, int quantity){

        this.title = title;
        this.price = price;
        this.quantity = quantity;

    }

    public Date formatTime(String dateString){
        Date dateObj = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateObj = null;
        try{
            dateObj = df.parse(dateString);
            if(dateObj == null){
                dateObj = df.parse("2015-11-13 22:18:00");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  dateObj;
    }
    public void setPhotoPath(String photoPath){
        this.photoPath = photoPath;
    }
    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}
}
