package org.best.taskboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.best.taskboard.R;
import org.best.taskboard.adapters.CardsAdapter;
import org.best.taskboard.models.Card;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class CardsFragment extends Fragment {
    private final List<Card> mCards = new ArrayList<>();
    private final CardsAdapter mAdapter = new CardsAdapter(mCards);
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_card_list, container, false);
        setupRecyclerView(mRecyclerView);
        return mRecyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    public List<Card> getCards() {
        return mCards;
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(mCards.size() - 1);
        }
    }

    public void setColor(int color) {
        mAdapter.setColor(color);
    }
}