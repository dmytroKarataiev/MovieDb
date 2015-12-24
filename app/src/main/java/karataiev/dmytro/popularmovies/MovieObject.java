package karataiev.dmytro.popularmovies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class for movies fetched from movieDB API
 * Created by karataev on 12/14/15.
 */
public class MovieObject implements Parcelable {
    private String adult;
    private String backdrop_path;
    ArrayList<String> genre_ids;
    String id;
    private String original_language;
    private String original_title;
    String overview;
    String release_date;
    String poster_path;
    private String popularity;
    String title;
    private String video;
    String vote_average;
    String vote_count;
    String full_poster_path;
    ArrayList<String> key;
    String trailer_path;

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
}
