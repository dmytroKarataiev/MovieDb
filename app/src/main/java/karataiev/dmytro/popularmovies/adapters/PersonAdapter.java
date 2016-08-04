/*
 * MIT License
 *
 * Copyright (c) 2016. Dmytro Karataiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package karataiev.dmytro.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import karataiev.dmytro.popularmovies.R;
import karataiev.dmytro.popularmovies.interfaces.ItemClickListener;
import karataiev.dmytro.popularmovies.model.Consts;
import karataiev.dmytro.popularmovies.model.person.PersonPopularResult;
import karataiev.dmytro.popularmovies.utils.Utility;

/**
 * Adapter which shows actors and their names in a recyclerview
 * Created by karataev on 5/8/16.
 */
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    private static final String TAG = PersonAdapter.class.getSimpleName();

    private List<PersonPopularResult> mPopularResults;
    private Context mContext;
    private ItemClickListener<PersonPopularResult, View> mListener;

    public void setData(ItemClickListener<PersonPopularResult, View> listener,
                        List<PersonPopularResult> popularResults) {
        mListener = listener;
        if (mPopularResults != null) {
            mPopularResults.addAll(popularResults);
        } else {
            mPopularResults = popularResults;
        }
        notifyDataSetChanged();
    }

    public PersonAdapter(Context context, List<PersonPopularResult> popularResults) {
        super();
        mContext = context;
        this.mPopularResults = popularResults;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster) ImageView mImageActor;
        @BindView(R.id.movie_poster_text) TextView mTextActor;
        @BindView(R.id.movie_item_spinner) ProgressBar mProgressSpinner;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonPopularResult popularResult = mPopularResults.get(position);

        // TODO: 6/13/16 cache link
        Picasso.with(mContext)
                .load(Consts.IMAGE_URL
                        + Utility.posterSize(holder.itemView.getContext())[0]
                        + popularResult.getProfilePath())
                .into(holder.mImageActor, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mProgressSpinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.mProgressSpinner.setVisibility(View.GONE);
                    }
                });
        holder.mTextActor.setText(popularResult.getName());

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClicked(popularResult, holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPopularResults != null ? mPopularResults.size() : 0;
    }

}
