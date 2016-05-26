/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package karataiev.dmytro.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import karataiev.dmytro.popularmovies.model.Consts;
import karataiev.dmytro.popularmovies.model.Actor;
import karataiev.dmytro.popularmovies.model.MovieCast;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActorFragment extends Fragment {

    @BindView(R.id.actor_id)
    TextView mTextId;
    @BindView(R.id.actor_photo)
    ImageView mImagePhoto;
    @BindView(R.id.actor_name)
    TextView mTextName;
    @BindView(R.id.actor_bioghraphy)
    TextView mTextBio;
    @BindView(R.id.actor_birthday)
    TextView mTextBirthday;
    @BindView(R.id.actor_deathday)
    TextView mTextDeathday;
    @BindView(R.id.actor_homepage)
    TextView mTextHomepage;
    @BindView(R.id.actor_birthplace)
    TextView mTextBirthplace;

    private Unbinder mUnbinder;
    private Subscription _subscription;

    private OnFragmentInteraction mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteraction) {
            mListener = (OnFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement " + OnFragmentInteraction.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (_subscription != null && !_subscription.isUnsubscribed()) {
            _subscription.unsubscribe();
        }
    }

    public ActorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actor, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity().getIntent() != null) {
            setData(getActivity().getIntent()
                    .getParcelableExtra(Consts.ACTOR_EXTRA));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    public void setData(MovieCast movieCast) {

        _subscription = App.getApiManager().getMoviesService()
                .getActor(String.valueOf(movieCast.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Actor>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Actor actor) {
                        mTextBio.setText(actor.getBiography());
                        mTextBirthday.setText(actor.getBirthday());
                        mTextDeathday.setText(actor.getDeathday());
                        mTextHomepage.setText(actor.getHomepage());
                        mTextBirthplace.setText(actor.getPlaceOfBirth());
                    }
                });

        Picasso.with(getContext())
                .load(Consts.IMAGE_URL + Consts.ACTOR_THUMB + movieCast.getProfilePath())
                .into(mImagePhoto);

        mTextName.setText(movieCast.getName());
        mTextId.setText(String.valueOf(movieCast.getId()));

        // send a title to an activity's actionBar
        if (mListener != null) {
            mListener.onDataReceive(movieCast.getName());
        }
    }

    public interface OnFragmentInteraction {
        void onDataReceive(String title);
    }
}
