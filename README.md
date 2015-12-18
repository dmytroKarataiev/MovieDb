# PopularMovies
App uses [The Movie Database API](https://www.themoviedb.org) to fetch data and show popular movies


## API Key
For this app to work you have to acquire API key and put it into gradle.properties file. 

Following line should be added to the file:

movieDbApiKey = "**YOUR API KEY**"

## Functionality
* You can sort data by: rating, popularity, number of votes, revenue and name in ascending and descending orders.
* You can easily share info about chosen movie.
* Scrolling is continuous 

## Content from the internet and other sources
* Star in the app: http://cliparts.co/cliparts/6Tp/opb/6Tpopbaac.png
* App icon: http://www.veryicon.com/icon/png/System/Agua/Movies.png
* Some technics and ideas were taken from Udacity Course [Developing Android Apps](https://www.udacity.com/course/viewer#!/c-ud853-nd)
* And useful WebCasts from Android NanoDegree: https://plus.google.com/u/0/107950612876685287140/posts 

## Additional Info
* App uses great image library: http://square.github.io/picasso/

Update: as it turned out not so great, it can't save Target (recycles it) and sometimes you can get a funny behavior when scroll very fast.
* OkHttpClient performs all network work: http://square.github.io/okhttp/
* GSON library effectively parses json: https://github.com/google/gson

