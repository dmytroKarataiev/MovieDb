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

package com.adkdevelopment.moviesdb.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karataev on 6/3/16.
 */
public class Backdrops {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("backdrops")
    @Expose
    private List<Backdrop> backdrops = new ArrayList<>();
    @SerializedName("posters")
    @Expose
    private List<Poster> posters = new ArrayList<>();

    /**
     *
     * @return
     * The id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The backdrops
     */
    public List<Backdrop> getBackdrops() {
        return backdrops;
    }

    /**
     *
     * @param backdrops
     * The backdrops
     */
    public void setBackdrops(List<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }

    /**
     *
     * @return
     * The posters
     */
    public List<Poster> getPosters() {
        return posters;
    }

    /**
     *
     * @param posters
     * The posters
     */
    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }

}
