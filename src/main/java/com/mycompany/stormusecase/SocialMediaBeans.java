/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.stormusecase;

import java.util.Date;

/**
 *
 * @author impadmin
 */
public class SocialMediaBeans {
    String hashTag;
    Date date;
    double count;
    String media;
    
    public SocialMediaBeans(String hashTag,double count,String media){
        this.count=count;
        this.hashTag=hashTag;
        date=new Date();
        this.media=media;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
    
    
    
    
}
