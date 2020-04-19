package com.example.movie.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movie.R;
import com.example.movie.activity.bean.Trailer;

import java.util.List;

import static com.example.movie.utils.ApplicationConstants.NO_TRAILER_MESSAGE;


public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder> {

    final List<Trailer> mValues;
    final Context mContext;


    public TrailerRecyclerViewAdapter(Context context, List<Trailer> values) {
        mValues = values;
        mContext = context;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton imageButton;
        private TextView trailerTextView;
        Button shareURLButton;
        Trailer trailer;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.playButton);
            trailerTextView = itemView.findViewById(R.id.trailerNumber);
            shareURLButton = itemView.findViewById(R.id.shareButton);
        }

        void bind(int position) {
            trailer = mValues.get(position);
            trailerTextView.setText(trailer.getName());

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    try {
                        mContext.startActivity(webIntent);
                    } catch (Exception e) {
                        Toast.makeText(mContext, NO_TRAILER_MESSAGE, Toast.LENGTH_LONG).show();
                    }
                }
            });
            initShareURLButton(trailer.getKey());
        }

        private void initShareURLButton(String key) {
            shareURLButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareCompat.IntentBuilder
                            /* The from method specifies the Context from which this share is coming from */
                            .from((Activity) mContext)
                            .setType("text/plain")
                            .setChooserTitle(R.string.sharing_youtube_trailer_url)
                            .setText("http://www.youtube.com/watch?v=" + key)
                            .startChooser();
                }
            });


        }

    }


    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_trailer, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Log.d("position", "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        Log.d("Anandhi size", "#" + mValues.size());
        return mValues.size();
    }
}