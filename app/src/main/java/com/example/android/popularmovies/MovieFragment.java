package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    public MovieFragment() {
    }

    private ImageAdapter movieAdapter;
    GridView gridView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        gridView = (GridView) rootView.findViewById(R.id.gridview);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                startActivity(intent);
            }
        });

        return rootView;
    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private String[] posterPath;

        public ImageAdapter(Context c, String[] semiPosterPath) {

            mContext = c;
            posterPath = semiPosterPath;

        }

        public int getCount() {

            return posterPath.length;
        }

        public Object getItem(int position) {

            return null;
        }

        public long getItemId(int position) {

            return 0;
        }

        public View getView(int position, View counterView, ViewGroup parent) {

            ImageView imageView;

            if (counterView == null) {

                imageView = new ImageView(mContext);


            } else {

                imageView = (ImageView) counterView;
            }

            String finalPath = "http://image.tmdb.org/t/p/w342/" + posterPath[position];

            Picasso.with(getActivity().getApplicationContext()).load(finalPath).into(imageView);

            return imageView;

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();

    }

    public class FetchMovieTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            JSONObject movieJson = new JSONObject(movieJsonStr);

            JSONArray result = movieJson.getJSONArray("results");

            String[] resultStrs = new String[result.length()];

            for (int i = 0; i < resultStrs.length; i++) {

                JSONObject movie = result.getJSONObject(i);
                String poster_path = movie.getString("poster_path");
                resultStrs[i] = poster_path;
                Log.v(LOG_TAG, poster_path);


            }

            return resultStrs;
        }

        @Override
        public void onPostExecute(String[] results) {

            movieAdapter = new ImageAdapter(getActivity(), results);
            gridView.setAdapter(movieAdapter);


        }


        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=12af43fb975a79eeadbdff3fdb5f67dc");


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Data fetched " + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

    }
}

