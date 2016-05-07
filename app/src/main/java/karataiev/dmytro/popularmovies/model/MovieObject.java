package karataiev.dmytro.popularmovies.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * Class for movies fetched from movieDB API
 * Created by karataev on 12/14/15.
 */
public class MovieObject implements Parcelable {

    public static final String MOVIE_OBJECT = "movie";

    private String adult;
    private String backdrop_path;
    private ArrayList<String> genre_ids;
    private String id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private String popularity;
    private String title;
    private String video;
    private String vote_average;
    private String vote_count;
    private String full_poster_path;
    private ArrayList<String> key;
    private String trailer_path;

    public MovieObject() { }

    private MovieObject(Parcel in) {
        adult = in.readString();
        backdrop_path = in.readString();
        genre_ids = (ArrayList<String>) in.readSerializable();
        id = in.readString();
        original_language = in.readString();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        popularity = in.readString();
        title = in.readString();
        video = in.readString();
        vote_average = in.readString();
        vote_count = in.readString();
        full_poster_path = in.readString();
        key = (ArrayList<String>) in.readSerializable();
        trailer_path = in.readString();
    }

    public void makeNice(Context context) {

        final String[] POSTER_SIZE = Utility.posterSize(context);

        // Creates to links to the posters: one for main window, one for the detailed view
        this.full_poster_path = "http://image.tmdb.org/t/p/" + POSTER_SIZE[1] + "/" + this.poster_path;
        this.poster_path = "http://image.tmdb.org/t/p/" + POSTER_SIZE[0] + "/" + this.poster_path;
        this.backdrop_path = "https://image.tmdb.org/t/p/" + POSTER_SIZE[1] + this.backdrop_path;
        this.vote_average = Utility.formatRating(vote_average);
        this.release_date = Utility.formatDate(release_date);
        this.vote_count = Utility.formatVotes(vote_count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(adult);
        parcel.writeString(backdrop_path);
        parcel.writeSerializable(genre_ids);
        parcel.writeString(id);
        parcel.writeString(original_language);
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(poster_path);
        parcel.writeString(popularity);
        parcel.writeString(title);
        parcel.writeString(video);
        parcel.writeString(vote_average);
        parcel.writeString(vote_count);
        parcel.writeString(full_poster_path);
        parcel.writeSerializable(key);
        parcel.writeString(trailer_path);

    }

    public static final Creator<MovieObject> CREATOR = new Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel parcel) {
            return new MovieObject(parcel);
        }

        @Override
        public MovieObject[] newArray(int i) {
            return new MovieObject[i];
        }

    };

    // Public functions to get and set objects from outside
    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getVoteCount() {
        return vote_count;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public ArrayList<String> getTrailers() {
        return key;
    }

    public void setKeys(ArrayList<String> trailers) {
        key = trailers;
    }

    public String getTrailerPath() {
        return trailer_path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }

    public void setVoteCount(String vote_count) {
        this.vote_count = vote_count;
    }

    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getId() {
        return id;
    }

    public void setTrailerPath(String trailer_path) {
        this.trailer_path = trailer_path;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdrop_path = backdropPath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.original_language = originalLanguage;
    }

    public void setOriginalTitle(String originalTitle) {
        this.original_title = originalTitle;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setFullPosterPath(String fullPosterPath) {
        this.full_poster_path = fullPosterPath;
    }

    public String getAdult() {
        return adult;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public String getOriginalLanguage() {
        return original_language;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getVideo() {
        return video;
    }

    public String getFullPosterPath() {
        return full_poster_path;
    }


}
