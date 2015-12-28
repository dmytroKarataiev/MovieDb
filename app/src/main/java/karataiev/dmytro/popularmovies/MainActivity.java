package karataiev.dmytro.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieObjectAdapter.CallbackFromAdapter, FavoritesActivityFragment.CallbackFromFavorites {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private MainActivityFragment mContent;
    private DetailFragment mDetailFragment;
    private final String FRAGMENT_TAG = "FFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String FAVFRAGMENT_TAG = "FAVFR";

    // Two Pane variable
    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState == null) {
            mContent = new MainActivityFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mContent, FRAGMENT_TAG).commit();
        } else {
            mContent = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                mDetailFragment = new DetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, mDetailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.v(LOG_TAG, "button " + id + " " + item.toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_favorites) {

            if (mTwoPane) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FavoritesActivityFragment(), FAVFRAGMENT_TAG)
                        .commit();

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

            } else {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }

        }

        if (id == android.R.id.home) {

            // Add to Favorites Fragment Back button
            if (mTwoPane) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mContent, FRAGMENT_TAG)
                        .commit();

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onItemSelected(MovieObject movieObject) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("movie", movieObject);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        }  else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("movie", movieObject);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(FAVFRAGMENT_TAG) != null) {
            fm.beginTransaction()
                    .replace(R.id.container, new MainActivityFragment(), FRAGMENT_TAG)
                    .commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            super.onBackPressed();
        }
    }

}
