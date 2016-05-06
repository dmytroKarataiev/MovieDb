package karataiev.dmytro.popularmovies.remote;

/**
 * Interface to pass status of downloading to prevent skipping of pages
 * Created by karataev on 12/27/15.
 */
public interface TaskCompleted {

    void onAsyncProgress(boolean progress);
}
