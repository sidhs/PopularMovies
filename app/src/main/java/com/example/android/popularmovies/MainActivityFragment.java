package com.example.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    ImageAdapter imageAdapter = new ImageAdapter(getActivity());
    String[] resultStrs = new String[4];

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView  = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView;

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(getActivity()));

        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();





        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c){

            mContext = c;
        }

        public int getCount(){

            return mThumbIds.length;
        }

        public Object getItem(int position){

            return null;
        }

        public long getItemId(int position){

            return 0;
        }

        public View getView(int position, View counterView, ViewGroup parent) {

            ImageView imageView;

            if(counterView == null){

                imageView = new ImageView(mContext);


            }else{

                imageView = (ImageView) counterView;
            }


            Picasso.with(getActivity().getApplicationContext()).load("http://image.tmdb.org/t/p/w185/" + resultStrs[position]).into(imageView);


           // imageView.setImageResource(mThumbIds[position]);



            return imageView;

        }
        private Integer[] mThumbIds = {
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
                R.drawable.ic_launcher,
        };


    }

    public class FetchMovieTask extends AsyncTask<Void, Void, String[]>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        private String[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException{

            JSONObject movieJson = new JSONObject(movieJsonStr);

            JSONArray result = movieJson.getJSONArray("results");



            for(int i = 0; i < 4;i++){

                JSONObject movie = result.getJSONObject(i);
               String poster_path =  movie.getString("poster_path");
                resultStrs[i] = poster_path;
                Log.v(LOG_TAG,poster_path);


            }

            return resultStrs;
        }


        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=12af43fb975a79eeadbdff3fdb5f67dc");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG,"Data fetched " + forecastJsonStr);

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

