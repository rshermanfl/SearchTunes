package com.android.searchtunes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.NumberFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder>
{

    private Result[] data_set;
    private Context context;

    public SongsAdapter(Result[] RS, Context cntx) {
        data_set = RS;
        context = cntx;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView albumArtist;
        private final TextView albumSong;
        private final TextView songDetails;
        private final ImageView albumArt;

        public ViewHolder(View view) {
            super(view);
            // click listener maybe later for detail drill down
            albumArtist = view.findViewById(R.id.album_artist);
            albumSong = view.findViewById(R.id.song);
            albumArt = view.findViewById(R.id.album_img);
            songDetails = view.findViewById(R.id.song_detials);
        }

        public ImageView getAlbumArt() {
            return albumArt;
        }

         public TextView getAlbumArtist() {
           return albumArtist;
         }

         public TextView getAlbumSong() {
            return albumSong;
         }

         public TextView getSongDetails() { return songDetails; }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String AlbumArtist = data_set[position].artistName
                .concat(" - ")
                .concat(data_set[position].collectionName);
        holder.getAlbumArtist().setText(AlbumArtist);

        String AlbumTrack = data_set[position].trackNumber.toString()
                .concat(" - ")
                .concat(data_set[position].trackName);
        holder.getAlbumSong().setText(AlbumTrack);

        String SongDetails = "";
        String date=data_set[position].releaseDate;
        if (date.length() > 0) {
            date = date.split("T")[0];
            String[] d = date.split("-");
            date = d[1].concat("/").concat(d[2]).concat("/").concat(d[0]);
        } else {
            date = "N/A";
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        Date relDate;

        String currency;

        try {
            currency = format.format(data_set[position].trackPrice + 0.0d);

            SongDetails = data_set[position].primaryGenreName
                    .concat(" - ")
                    .concat(date)
                    .concat("\t\t").concat(currency);
        }
        catch(Exception e) {
                e.printStackTrace();
        }
        holder.getSongDetails().setText(SongDetails);

        Picasso.with(context).load(data_set[position].artworkUrl100).into(holder.getAlbumArt());
    }

    @Override
    public int getItemCount() {
        return data_set.length;
    }


}
