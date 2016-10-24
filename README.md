# The Movie Database Client
<a href="https://play.google.com/store/apps/details?id=com.adkdevelopment.moviesdb"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="185" height="60"/></a><br>

![Animation of a current state](movies.gif)

A fast and easy to use client to [The Movie Database API](https://www.themoviedb.org) with data persistence, youtube trailers inside the app, sharing functionality.

## Functionality
* You can sort data by: rating, popularity, number of votes, revenue and name in ascending and descending orders.
* You can easily share info about chosen movie.
* Scrolling is continuous.
* You can watch trailers inside of the app (YouTube API).
* You can save your favorite movies.
* Search by movie titles.
* Tablet Design.

## Development plans
* Popular TV Series.
* Popular Actors.
* Design improvements.
* Actors Activity: Photos, List of Movies.

## Used Technologies
* RxAndroid, RxJava (EditText observable).
* Retrolambda, Butterknife to simplify life.
* RecyclerView, RecyclerAdapter.
* AsyncTasks.
* Fragments.
* Content Provider, Database.
* CursorAdapter.

## API Keys
For this app to work you have to acquire API keys and put them into gradle.properties file.
Following lines should be added to the file: <br>
movieDbApiKey = "**YOUR API KEY**" <br>
youtubeApiKey = "**YOUR API KEY**"

## Content from the internet and other sources
* Some technics and ideas were taken from the Udacity Course [Developing Android Apps](https://www.udacity.com/course/viewer#!/c-ud853-nd)
* And useful WebCasts from Android NanoDegree: https://plus.google.com/u/0/107950612876685287140/posts

## Additional Info
* App uses great image library Picasso: http://square.github.io/picasso/
* OkHttpClient performs all network work: http://square.github.io/okhttp/
* GSON library effectively parses json: https://github.com/google/gson
* YouTube Player API: https://developers.google.com/youtube/android/player/downloads/
* How to get YouTube API key: https://developers.google.com/youtube/v3/getting-started

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