package org.cinema.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Movie {

    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Poster")
    private String poster;

    @SerializedName("Plot")
    private String plot;

    @SerializedName("Response")
    private String response;

    @SerializedName("Genre")
    private String genre;

    @SerializedName("Director")
    private String director;

    @SerializedName("Actors")
    private String actors;

    @SerializedName("imdbRating")
    private String imdbRating;

    @SerializedName("Runtime")
    private String runtime;
}
