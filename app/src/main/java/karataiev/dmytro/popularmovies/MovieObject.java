package karataiev.dmytro.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for movies fetched from movieDB API
 * Created by karataev on 12/14/15.
 */
public class MovieObject implements Parcelable {
    String name;
    String pathToImage;
    String pathToDetailImage;
    String description;
    String rating;
    String year;

    public MovieObject(String name, String pathToImage, String pathToDetailImage, String description, String rating, String year) {
        this.name = name;
        this.pathToImage = pathToImage;
        this.pathToDetailImage = pathToDetailImage;
        this.description = description;
        this.rating = rating + "/10";
        this.year = year.substring(0, 4);
    }

    private MovieObject(Parcel in) {
        name = in.readString();
        pathToImage = in.readString();
        pathToDetailImage = in.readString();
        description = in.readString();
        rating = in.readString();
        year = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(pathToImage);
        parcel.writeString(pathToDetailImage);
        parcel.writeString(description);
        parcel.writeString(rating);
        parcel.writeString(year);
    }

    public static final Parcelable.Creator<MovieObject> CREATOR = new Parcelable.Creator<MovieObject>() {
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
