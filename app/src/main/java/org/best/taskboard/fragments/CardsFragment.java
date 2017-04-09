package org.best.taskboard.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.best.taskboard.CoapPeerClient;
import org.best.taskboard.R;
import org.best.taskboard.adapters.CardsAdapter;
import org.best.taskboard.models.Card;
import org.ws4d.coap.core.enumerations.CoapRequestCode;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class CardsFragment extends Fragment {
    private final List<Card> mCards = new ArrayList<>();
    private final CardsAdapter mAdapter = new CardsAdapter(mCards);
    RecyclerView mRecyclerView;
    private CoapPeerClient mClient;
    private Handler mHandler = new Handler();
    private CommandParser mParser = new CommandParser();

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_card_list, container, false);
        setupRecyclerView(mRecyclerView);
        return mRecyclerView;
    }

    public boolean connect(InetAddress address) {
        if (mClient == null) {
            mClient = new CoapPeerClient(address, new CoapPeerClient.Listener() {
                @Override
                public void onResponse(final String msg) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mParser.mIsLastCommand) {
                                getCards().add(new Card(msg, false));
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    public void send(String msg) {
        mParser.parse(msg);
        if (mParser.mIsLastCommand) {
            mClient.send(mParser.mMethod, mParser.mPath, mParser.mPayload);
        } else {
            mClient.send(msg);
        }
        getCards().add(new Card(msg));
        notifyDataSetChanged();
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

    public class CommandParser {
        boolean mIsLastCommand = false;
        CoapRequestCode mMethod = CoapRequestCode.GET;
        String mPath = "/dev/null";
        String mPayload = "";

        void parse(String msg) {
            mIsLastCommand = msg.getBytes()[0] == '/';
            if (mIsLastCommand) {
                msg = msg.substring(1);
                String[] parts = msg.split("\\.");
                try {
                    mMethod = parseMethod(parts[0]);
                    mPath = parts[1];
                    mPayload = parts[2];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        CoapRequestCode parseMethod(String code) {
            return CoapRequestCode.valueOf(code.toUpperCase());
        }
    }
}