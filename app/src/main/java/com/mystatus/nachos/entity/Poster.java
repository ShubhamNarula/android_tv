package com.mystatus.nachos.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Poster implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_title")
    @Expose
    private String poster_title;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("imdb")
    @Expose
    private String imdb;

    @SerializedName("downloadas")
    @Expose
    private String downloadas;

    @SerializedName("comment")
    @Expose
    private Boolean comment;

    @SerializedName("playas")
    @Expose
    private String playas;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("classification")
    @Expose
    private String classification;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("mili")
    @Expose
    private String mili;

    @SerializedName("percentage")
    @Expose
    private Integer percentage;


    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("rating")
    @Expose
    private Float rating;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("cover")
    @Expose
    private String cover;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();

    @SerializedName("sources")
    @Expose
    private List<Source> sources = new ArrayList<>();


    @SerializedName("trailer")
    @Expose
    private Source trailer ;

    private int typeView = 1;

    public Poster() {
    }


    protected Poster(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            percentage = null;
        } else {
            percentage = in.readInt();
        }
        mili=in.readString();
        title = in.readString();
        type = in.readString();
        imdb = in.readString();
        downloadas = in.readString();
        byte tmpComment = in.readByte();
        comment = tmpComment == 0 ? null : tmpComment == 1;
        playas = in.readString();
        description = in.readString();
        classification = in.readString();
        year = in.readString();
        duration = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readFloat();
        }
        image = in.readString();
        cover = in.readString();
        genres = in.createTypedArrayList(Genre.CREATOR);
        sources = in.createTypedArrayList(Source.CREATOR);
        trailer = in.readParcelable(Source.class.getClassLoader());
        typeView = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (percentage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(percentage);
        }
        dest.writeString(mili);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(imdb);
        dest.writeString(downloadas);
        dest.writeByte((byte) (comment == null ? 0 : comment ? 1 : 2));
        dest.writeString(playas);
        dest.writeString(description);
        dest.writeString(classification);
        dest.writeString(year);
        dest.writeString(duration);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(rating);
        }
        dest.writeString(image);
        dest.writeString(cover);
        dest.writeTypedList(genres);
        dest.writeTypedList(sources);
        dest.writeParcelable(trailer, flags);
        dest.writeInt(typeView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_title() {
        return poster_title;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getClassification() {
        return classification;
    }

    public String getYear() {
        return year;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
    public List<Source> getSources() {
        return sources;
    }

    public Source getTrailer() {
        return trailer;
    }

    public void setTrailer(Source trailer) {
        this.trailer = trailer;
    }

    public int getTypeView() {
        return typeView;
    }

    public Poster setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

    public String getImdb() {
        if (this.imdb != null) {
            double d = Double.parseDouble(this.imdb);
            DecimalFormat f = new DecimalFormat("##.0");
            return f.format(d);
        }
        return "0";
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public Integer getPercentage() { return percentage; }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getMili() {
        return mili;
    }

    public void setMili(String mili) {
        this.mili = mili;
    }
}


