/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package karataiev.dmytro.popularmovies.model.person;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import karataiev.dmytro.popularmovies.interfaces.MoviePerson;

/**
 * Created by karataev on 8/3/16.
 */
public class PersonPopularResult implements Parcelable, MoviePerson {
    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("known_for")
    @Expose
    private List<PersonKnown> knownFor = new ArrayList<>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("popularity")
    @Expose
    private double popularity;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    /**
     *
     * @return
     * The adult
     */
    public boolean isAdult() {
        return adult;
    }

    /**
     *
     * @param adult
     * The adult
     */
    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The knownFor
     */
    public List<PersonKnown> getKnownFor() {
        return knownFor;
    }

    /**
     *
     * @param knownFor
     * The known_for
     */
    public void setKnownFor(List<PersonKnown> knownFor) {
        this.knownFor = knownFor;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The popularity
     */
    public double getPopularity() {
        return popularity;
    }

    /**
     *
     * @param popularity
     * The popularity
     */
    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    /**
     *
     * @return
     * The profilePath
     */
    public String getProfilePath() {
        return profilePath;
    }

    /**
     *
     * @param profilePath
     * The profile_path
     */
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeList(this.knownFor);
        dest.writeString(this.name);
        dest.writeDouble(this.popularity);
        dest.writeString(this.profilePath);
    }

    public PersonPopularResult() {
    }

    protected PersonPopularResult(Parcel in) {
        this.adult = in.readByte() != 0;
        this.id = in.readInt();
        this.knownFor = new ArrayList<PersonKnown>();
        in.readList(this.knownFor, PersonKnown.class.getClassLoader());
        this.name = in.readString();
        this.popularity = in.readDouble();
        this.profilePath = in.readString();
    }

    public static final Parcelable.Creator<PersonPopularResult> CREATOR = new Parcelable.Creator<PersonPopularResult>() {
        @Override
        public PersonPopularResult createFromParcel(Parcel source) {
            return new PersonPopularResult(source);
        }

        @Override
        public PersonPopularResult[] newArray(int size) {
            return new PersonPopularResult[size];
        }
    };
}
