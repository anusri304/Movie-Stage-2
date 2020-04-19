package com.example.movie.transport;

import com.example.movie.activity.bean.Review;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ReviewTransportBean {
    int id;
    @SerializedName("results")
    List<Review> reviews;
}
