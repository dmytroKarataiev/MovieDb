# The Movie Database Client [![Travis CI](https://travis-ci.org/dmytroKarataiev/MovieDb.svg?branch=master)](https://travis-ci.org/dmytroKarataiev/MovieDb)
<a href="https://play.google.com/store/apps/details?id=com.adkdevelopment.moviesdb"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="185" height="60"/></a><br>

![Animation of a current state](movies.gif)

A fast and easy to use client to [The Movie Database](https://www.themoviedb.org) with data persistence, youtube trailers inside the app, sharing functionality.

## Functionality
* Shows tabs with movies, series, your favorites and actors.
* You can sort data by: rating, popularity, number of votes, revenue and name in ascending and descending orders.
* You can easily share info about a chosen movie.
* Scrolling is continuous.
* You can watch trailers inside the app (YouTube API).
* You can add movies to favorites.
* Search by movie titles.

## Development plans
* Design improvements.
* MVP architecture.

## Used Technologies
* MVP architecture.
* RxAndroid, RxJava (EditText observable), Retrolambda.
* Content Provider, Database, LoaderManagers.
* Fabric analytics, Travis CI.

## Used Libraries
* Picasso for image loading.
* Retrofit for all REST-realted work.
* LeakCanary to test for memory leaks.
* YouTube Player API: https://developers.google.com/youtube/android/player/downloads/

## API Keys
For this app to work you have to acquire API keys and put them into gradle.properties file.
Following lines should be added to the file: <br>
movieDbApiKey = "**YOUR API KEY**" <br>
youtubeApiKey = "**YOUR API KEY**"

License
-------

	The MIT License (MIT)

	Copyright (c) 2016 Dmytro Karataiev

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
