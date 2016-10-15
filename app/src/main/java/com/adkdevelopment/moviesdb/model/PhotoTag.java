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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karataev on 5/29/16.
 */
public class PhotoTag {

    @SerializedName("aspect_ratio")
    @Expose
    private double aspectRatio;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    @SerializedName("height")
    @Expose
    private long height;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("iso_639_1")
    @Expose
    private Object iso6391;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private long voteCount;
    @SerializedName("width")
    @Expose
    private long width;
    @SerializedName("image_type")
    @Expose
    private String imageType;
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("media_type")
    @Expose
    private String mediaType;

    /**
     *
     * @return
     * The aspectRatio
     */
    public double getAspectRatio() {
        return aspectRatio;
    }

    /**
     *
     * @param aspectRatio
     * The aspect_ratio
     */
    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    /**
     *
     * @return
     * The filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     *
     * @param filePath
     * The file_path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     *
     * @return
     * The height
     */
    public long getHeight() {
        return height;
    }

    /**
     *
     * @param height
     * The height
     */
    public void setHeight(long height) {
        this.height = height;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The iso6391
     */
    public Object getIso6391() {
        return iso6391;
    }

    /**
     *
     * @param iso6391
     * The iso_639_1
     */
    public void setIso6391(Object iso6391) {
        this.iso6391 = iso6391;
    }

    /**
     *
     * @return
     * The voteAverage
     */
    public double getVoteAverage() {
        return voteAverage;
    }

    /**
     *
     * @param voteAverage
     * The vote_average
     */
    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     *
     * @return
     * The voteCount
     */
    public long getVoteCount() {
        return voteCount;
    }

    /**
     *
     * @param voteCount
     * The vote_count
     */
    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    /**
     *
     * @return
     * The width
     */
    public long getWidth() {
        return width;
    }

    /**
     *
     * @param width
     * The width
     */
    public void setWidth(long width) {
        this.width = width;
    }

    /**
     *
     * @return
     * The imageType
     */
    public String getImageType() {
        return imageType;
    }

    /**
     *
     * @param imageType
     * The image_type
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /**
     *
     * @return
     * The media
     */
    public Media getMedia() {
        return media;
    }

    /**
     *
     * @param media
     * The media
     */
    public void setMedia(Media media) {
        this.media = media;
    }

    /**
     *
     * @return
     * The mediaType
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     *
     * @param mediaType
     * The media_type
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

}