package com.example.movie.transport;

import com.example.movie.activity.bean.Trailer;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;
@Data
public class TrailerTransportBean {
    int id;
    @SerializedName("results")
    List<Trailer> trailers;
}
