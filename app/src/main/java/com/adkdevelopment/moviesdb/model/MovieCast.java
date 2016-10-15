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

package com.adkdevelopment.moviesdb.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.adkdevelopment.moviesdb.interfaces.MoviePerson;

public class MovieCast implements Parcelable, MoviePerson {

    @SerializedName("cast_id")
    @Expose
    private int castId;
    @SerializedName("character")
    @Expose
    private String character;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("order")
    @Expose
    private int order;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;

    /**
     *
     * @return
     * The castId
     */
    public int getCastId() {
        return castId;
    }

    /**
     *
     * @param castId
     * The cast_id
     */
    public void setCastId(int castId) {
        this.castId = castId;
    }

    /**
     *
     * @return
     * The character
     */
    public String getCharacter() {
        return character;
    }

    /**
     *
     * @param character
     * The character
     */
    public void setCharacter(String character) {
        this.character = character;
    }

    /**
     *
     * @return
     * The creditId
     */
    public String getCreditId() {
        return creditId;
    }

    /**
     *
     * @param creditId
     * The credit_id
     */
    public void setCreditId(String creditId) {
        this.creditId = creditId;
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
     * The order
     */
    public int getOrder() {
        return order;
    }

    /**
     *
     * @param order
     * The order
     */
    public void setOrder(int order) {
        this.order = order;
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
        dest.writeInt(this.castId);
        dest.writeString(this.character);
        dest.writeString(this.creditId);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.order);
        dest.writeString(this.profilePath);
    }

    public MovieCast() {
    }

    protected MovieCast(Parcel in) {
        this.castId = in.readInt();
        this.character = in.readString();
        this.creditId = in.readString();
        this.id = in.readInt();
        this.name = in.readString();
        this.order = in.readInt();
        this.profilePath = in.readString();
    }

    public static final Parcelable.Creator<MovieCast> CREATOR = new Parcelable.Creator<MovieCast>() {
        @Override
        public MovieCast createFromParcel(Parcel source) {
            return new MovieCast(source);
        }

        @Override
        public MovieCast[] newArray(int size) {
            return new MovieCast[size];
        }
    };
}