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

package com.adkdevelopment.moviesdb.model.person;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karataev on 8/3/16.
 */
public class PersonPopular implements Parcelable {
    @SerializedName("page")
    @Expose
    private long page;
    @SerializedName("results")
    @Expose
    private List<PersonPopularResult> results = new ArrayList<>();
    @SerializedName("total_pages")
    @Expose
    private long totalPages;
    @SerializedName("total_results")
    @Expose
    private long totalResults;

    /**
     *
     * @return
     * The page
     */
    public long getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(long page) {
        this.page = page;
    }

    /**
     *
     * @return
     * The results
     */
    public List<PersonPopularResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<PersonPopularResult> results) {
        this.results = results;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public long getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The total_pages
     */
    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    /**
     *
     * @return
     * The totalResults
     */
    public long getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     * The total_results
     */
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.page);
        dest.writeTypedList(this.results);
        dest.writeLong(this.totalPages);
        dest.writeLong(this.totalResults);
    }

    public PersonPopular() {
    }

    protected PersonPopular(Parcel in) {
        this.page = in.readLong();
        this.results = in.createTypedArrayList(PersonPopularResult.CREATOR);
        this.totalPages = in.readLong();
        this.totalResults = in.readLong();
    }

    public static final Parcelable.Creator<PersonPopular> CREATOR = new Parcelable.Creator<PersonPopular>() {
        @Override
        public PersonPopular createFromParcel(Parcel source) {
            return new PersonPopular(source);
        }

        @Override
        public PersonPopular[] newArray(int size) {
            return new PersonPopular[size];
        }
    };
}
