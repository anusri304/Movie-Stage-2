package com.example.movie.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;

@Data
public class MoviePresentationBean implements Parcelable {
    private int id;
    private String image;
    private String thumbNailImage;
    private String title;
    private String overview;
    private double voteAverage;
    private String releaseDate;


    public MoviePresentationBean() {

    }

    public static final Creator<MoviePresentationBean> CREATOR = new Creator<MoviePresentationBean>() {
        @Override
        public MoviePresentationBean createFromParcel(Parcel in) {
            return new MoviePresentationBean(in);
        }

        @Override
        public MoviePresentationBean[] newArray(int size) {
            return new MoviePresentationBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(image);
        dest.writeString(thumbNailImage);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);

    }

    private MoviePresentationBean(Parcel in) {
        id = in.readInt();
        image = in.readString();
        thumbNailImage= in.readString();
        title = in.readString();
        overview= in.readString();
        voteAverage = in.readDouble();
        releaseDate= in.readString();
    }
}
