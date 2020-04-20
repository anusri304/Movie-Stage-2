package com.example.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movie.R;
import com.example.movie.activity.bean.Review;

import java.util.List;

@SuppressWarnings("ALL")
public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewViewHolder> {
    final List<Review> mValues;
    final Context mContext;


    public ReviewRecyclerViewAdapter(Context context, List<Review> values) {
        mValues = values;
        mContext = context;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView authorTextView;
        private TextView reviewTextView;
        Review review;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.textview_author);
            reviewTextView = itemView.findViewById(R.id.textview_content);
        }

        void bind(int position) {
            review = mValues.get(position);
            authorTextView.setText(review.getAuthor());
            reviewTextView.setText(review.getContent());

        }
    }

    @NonNull
    @Override
    public ReviewRecyclerViewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_review, viewGroup, false);

        return new ReviewRecyclerViewAdapter.ReviewViewHolder(view);


    }

    @Override
    public void onBindViewHolder(ReviewRecyclerViewAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}


