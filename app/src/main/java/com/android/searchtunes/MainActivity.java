package com.android.searchtunes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    OkHttpClient client;
    ArrayList<Result> results;
    SongsAdapter songsAdapter;

    SearchView artistSearch;
    RecyclerView searchResults;

    final String iURL = "https://itunes.apple.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        artistSearch = findViewById(R.id.artist_search);
        searchResults = findViewById(R.id.search_results);
        results = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        searchResults.setLayoutManager(llm);
        searchResults.setAdapter(null);

        artistSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(
                        getRequest(artistSearch.getQuery().toString())
                );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                return false;
            }
        });
    }

    private Request getRequest(String artist) {
        String srch = null;
        try {
            HttpUrl.Builder urlBuilder = HttpUrl
                    .parse(iURL)
                    .newBuilder();
            urlBuilder.addQueryParameter("term", artist);
            srch = urlBuilder.build().toString();
            Log.d("SearchTunes >> ", srch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new Request.Builder().url(srch).build();
    }

    private void doSearch(Request r) {
        client.newCall(r).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                JSONObject envelope;
                JSONArray songs;
                try {
                    if (results != null && !results.isEmpty()) {
                        results.clear();
                    }

                    envelope = new JSONObject(myResponse);
                    int resultCount = envelope.getInt("resultCount");

                    Log.d("resultCount >> ", String.valueOf(resultCount));
                    songs = envelope.getJSONArray("results");
                    // mapping
                    for (int idx = 0; idx < resultCount; idx++ ) {
                        JSONObject song = songs.getJSONObject(idx);
                        results.add(mapResult(song));
                    }

                    runOnUiThread(() ->{
                        songsAdapter = new SongsAdapter(
                                results.toArray(new Result[0]),
                                MainActivity.this);
                        searchResults.setAdapter(songsAdapter);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    private Result mapResult(JSONObject song) {
        Result reslt = new Result();
            reslt.wrapperType = song.optString("wrapperType","");
            reslt.kind = song.optString("kind","");
            reslt.artistId = song.optLong("artistId", 0);
            reslt.collectionId = song.optLong("collectionId",0);
            reslt.trackId = song.optLong("trackId",0);
            reslt.artistName = song.optString("artistName", "");
            reslt.collectionName = song.optString("collectionName","");
            reslt.trackName = song.optString("trackName","");
            reslt.collectionCensoredName = song.optString("collectionCensoredName","");
            reslt.trackCensoredName = song.optString("trackCensoredName","");
            reslt.artistViewUrl = song.optString("artistViewUrl","");
            reslt.collectionViewUrl = song.optString("collectionViewUrl","");
            reslt.trackViewUrl = song.optString("trackViewUrl","");
            reslt.previewUrl = song.optString("previewUrl","");
            reslt.artworkUrl30 = song.optString("artworkUrl30","");
            reslt.artworkUrl60 = song.optString("artworkUrl60","");
            reslt.artworkUrl100 = song.optString("artworkUrl100","");
            reslt.collectionPrice = song.optDouble("collectionPrice", 0.0d);
            reslt.trackPrice = song.optDouble("trackPrice", 0.0d);
            reslt.releaseDate = song.optString("releaseDate","");
            reslt.collectionExplicitness = song.optString("collectionExplicitness","");
            reslt.trackExplicitness = song.optString("trackExplicitness","");
            reslt.discCount = song.optInt("discCount", 0);
            reslt.discNumber = song.optInt("discNumber",0);
            reslt.trackCount = song.optInt("trackCount",0);
            reslt.trackNumber = song.optInt("trackNumber",0);
            reslt.trackTimeMillis = song.optLong("trackTimeMillis",0l);
            reslt.country = song.optString("country", "");
            reslt.currency = song.optString("currency", "");
            reslt.primaryGenreName = song.optString("primaryGenreName", "");
            reslt.isStreamable = song.optBoolean("isStreamable", false);
        return reslt;
    }

}