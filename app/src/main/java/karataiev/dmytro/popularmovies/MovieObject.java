package karataiev.dmytro.popularmovies;

/**
 * Class for movies fetched from movieDB API
 * Created by karataev on 12/14/15.
 */
public class MovieObject {
    String name;
    String pathToImage;

    public MovieObject(String name, String pathToImage) {
        this.name = name;
        this.pathToImage = pathToImage;
    }
}
