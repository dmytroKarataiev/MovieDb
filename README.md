# PopularMovies
App uses [The Movie Database API](https://www.themoviedb.org) to fetch data and show movies <br>
App has been published on Google Play: [Popular Movies - The Movie DB Client](https://play.google.com/store/apps/details?id=karataiev.dmytro.popularmovies)

<img src="movies.gif">

## Functionality
* You can sort data by: rating, popularity, number of votes, revenue and name in ascending and descending orders.
* You can easily share info about chosen movie.
* Scrolling is continuous 
* You can watch trailers inside of the app (YouTube API)
* You can save your favorite movies
* Search by movie titles
* Tablet Design

## Development plans
* Design improvements

## API Keys
For this app to work you have to acquire API keys and put them into gradle.properties file. 
Following lines should be added to the file: <br>
movieDbApiKey = "**YOUR API KEY**" <br>
youtubeApiKey = "**YOUR API KEY**"


## Content from the internet and other sources
* Star in the app: http://cliparts.co/cliparts/6Tp/opb/6Tpopbaac.png
* App icon: http://www.veryicon.com/icon/png/System/Agua/Movies.png
* Some technics and ideas were taken from Udacity Course [Developing Android Apps](https://www.udacity.com/course/viewer#!/c-ud853-nd)
* And useful WebCasts from Android NanoDegree: https://plus.google.com/u/0/107950612876685287140/posts 

## Additional Info
* App uses great image library Picasso: http://square.github.io/picasso/
* OkHttpClient performs all network work: http://square.github.io/okhttp/
* GSON library effectively parses json: https://github.com/google/gson
* YouTube Player API: https://developers.google.com/youtube/android/player/downloads/
* How to get YouTube API key you can read here: https://developers.google.com/youtube/v3/getting-started

## Used Android Technologies
* RecyclerView, RecyclerAdapter
* AsyncTask
* Fragments
* Content Provider, Database
* CursorAdapter
