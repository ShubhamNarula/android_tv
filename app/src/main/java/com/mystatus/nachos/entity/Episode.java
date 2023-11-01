package com.mystatus.nachos.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Episode {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("downloadas")
    @Expose
    private String downloadas;


    @SerializedName("playas")
    @Expose
    private String playas;

    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("mili")
    @Expose
    private String mili;

    @SerializedName("percentage")
    @Expose
    private String percentage;


//    @SerializedName("last_watch")
//    @Expose
//    private String last_watch;


    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("sources")
    @Expose
    private List<Source> sources = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public String getDownloadas() {
        return downloadas;
    }

    public void setDownloadas(String downloadas) {
        this.downloadas = downloadas;
    }

    public String getPlayas() {
        return playas;
    }

    public void setPlayas(String playas) {
        this.playas = playas;
    }

    public String getDuration() { return duration; }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMili() {return mili;}

    public void setMili(String mili) {
        this.mili = mili;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

//    public String getLast_watch() {
//        return last_watch;
//    }
//
//    public void setLast_watch(String last_watch) {
//        this.last_watch = last_watch;
//    }

}