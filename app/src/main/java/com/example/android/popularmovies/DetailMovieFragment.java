package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailMovieFragment extends Fragment {

    public DetailMovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){

            String movieJsonStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            String movieID = intent.getStringExtra("intId");
          //  Toast.makeText(getActivity(),movieID,Toast.LENGTH_LONG).show();
            int id = Integer.parseInt(movieID);

            try {
                getMovieDataFromJson(movieJsonStr,id);
            } catch (JSONException e) {

            }


        }


        return rootView;
    }

    private void getMovieDataFromJson(String movieStr, int id)
    throws JSONException {

        String LOG_TAG = DetailMovieFragment.class.getSimpleName();


        JSONObject movieJson = new JSONObject(movieStr);
        JSONArray result = movieJson.getJSONArray("results");


        JSONObject movie = result.getJSONObject(id);
        String poster_path = movie.getString("poster_path");
        Log.v(LOG_TAG, poster_path);

    }


}
