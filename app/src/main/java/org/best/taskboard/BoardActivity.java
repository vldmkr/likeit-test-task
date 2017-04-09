package org.best.taskboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.best.taskboard.adapters.BoardsAdapter;
import org.best.taskboard.fragments.CardsFragment;
import org.best.taskboard.models.Board;
import org.best.taskboard.models.Cache;
import org.best.taskboard.models.Card;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class BoardActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.fab_create_card)
    FloatingActionButton mCardFab;
    @BindView(R.id.fab_create_board)
    FloatingActionButton mBoardFab;
    @BindView(R.id.boards_recycler)
    RecyclerView mBoardsRecycler;
    @BindView(R.id.image_send)
    ImageView mSendImage;
    @BindView(R.id.message_text)
    EditText mMessageEdit;
    @BindView(R.id.drawer_header_name)
    TextView mNameText;
    @BindView(R.id.drawer_header_address)
    TextView mAddressText;

    private ActionBarDrawerToggle mDrawerToggle;
    private PagerAdapter mPagerAdapter;
    private AlertDialog.Builder mAlertDialog;

    final CardsFragment mCategoryNew = new CardsFragment();
    final CardsFragment mCategoryInProgress = new CardsFragment();
    final CardsFragment mCategoryDone = new CardsFragment();
    final CardsFragment mCategoryCanceled = new CardsFragment();

    final Handler mHandler = new Handler();
    CoapClient client;
    CoapEndpoint endpoint = null;

    final List<Board> mBoards = new ArrayList<>();
    String mUserName = "---";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            if (name != null) {
                mUserName = name;
            }
        }

        mNameText.setText(mUserName);

        mAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogLightTheme);
        mAlertDialog.setTitle(getResources().getString(R.string.error_dialog_title));
        mAlertDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        try {
            mAddressText.setText(InetUtils.getIpAddress(InetUtils.getNetworkInterface()).toString());
        } catch (Exception ignored) {
            mAlertDialog.setMessage("Connect to WiFi network first! App will be closed.")
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finishAffinity();
                        }
                    })
            .show();
            return;
        }

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        createCategories();
        mViewPager.setCurrentItem(0);

        mTabLayout.setupWithViewPager(mViewPager);

        mCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateCard();
            }
        });

        mBoardsRecycler.setItemAnimator(new SlideInLeftAnimator());
        mBoardsRecycler.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        final BoardsAdapter boardsAdapter = new BoardsAdapter(mBoards);

        mBoardsRecycler.setAdapter(boardsAdapter);
        mBoardsRecycler.setLayoutManager(new LinearLayoutManager(this));

        mSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mMessageEdit.getText().toString());
                mMessageEdit.setText("");
            }
        });
        if (client == null) {
            client = new CoapClient(new CoapClient.Listener() {
                @Override
                public void onResponse(final Map<InetAddress, String> clients) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mBoards.clear();
                            for (Map.Entry<InetAddress, String> val : clients.entrySet()) {
                                mBoards.add(new Board(val.getValue(), val.getKey()));
                            }
                            boardsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
        if (endpoint == null) {
            endpoint = new CoapEndpoint(mUserName, new CoapEndpoint.Listener() {
                @Override
                public void onReceiveBroadcast(final String value) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final CardsFragment cards = mPagerAdapter.getItem(0);
                            cards.getCards().add(new Card(value, false));
                            cards.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onReceive(final InetAddress address, final String value) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String clientName = client.getClientName(address);
                            if (clientName != null) {
                                CardsFragment cards = mPagerAdapter.getFragment(clientName);
                                if (cards == null) {
                                    cards = selectBoard(clientName, address);
                                }
                                cards.getCards().add(new Card(value, false));
                                cards.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }
    }

    public void sendMessage(String message) {
        int idx = mViewPager.getCurrentItem();
        final CardsFragment cards = mPagerAdapter.getItem(idx);
        if (!message.trim().isEmpty()) {
            if (idx == 0) {
                synchronized (endpoint.usedIds) {
                    endpoint.usedIds.add(client.send(message));
                }
                cards.getCards().add(new Card(message));
                cards.notifyDataSetChanged();
            } else {
                cards.send(message);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void createCategories() {
//        mCategoryInProgress.setColor(ContextCompat.getColor(this, R.color.inprogressColor));
//        mCategoryDone.setColor(ContextCompat.getColor(this, R.color.doneColor));
//        mCategoryCanceled.setColor(ContextCompat.getColor(this, R.color.canceledColor));

        mPagerAdapter.addFragment("Broadcast", mCategoryNew);
//        mPagerAdapter.addFragment(getResources().getString(R.string.cat_inprogress), mCategoryInProgress);
//        mPagerAdapter.addFragment(getResources().getString(R.string.cat_done), mCategoryDone);
//        mPagerAdapter.addFragment(getResources().getString(R.string.cat_canceled), mCategoryCanceled);
        mViewPager.setAdapter(mPagerAdapter);
    }

    public void dialogCreateCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogLightTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogCreateCardView = inflater.inflate(R.layout.dialog_create_card, null);
        final EditText cardDescription = (EditText) dialogCreateCardView.findViewById(R.id.edit_card_description);
        builder.setView(dialogCreateCardView)
                .setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (cardDescription.getText().toString().trim().isEmpty()) {
                            mAlertDialog.setMessage(getResources().getString(R.string.card_description_empty));
                            mAlertDialog.show();
                        } else {
                            mCategoryNew.getCards().add(new Card(cardDescription.getText().toString()));
                            mCategoryNew.notifyDataSetChanged();
                            mViewPager.setCurrentItem(0);

                            Snackbar.make(mDrawerLayout, getResources().getString(R.string.card_created), Snackbar.LENGTH_LONG)
                                    .setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Dismiss
                                        }
                                    })
                                    .show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel
                    }
                })
                .setTitle(getResources().getString(R.string.create_card_dialog_title))
                .setMessage(getResources().getString(R.string.create_card_dialog_content))
                .show();
    }

    public void dialogResendMsg(final Card card) {
        AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogLightTheme);
        deleteAlertDialog.setTitle(getResources().getString(R.string.resend_card_dialog_title));
        deleteAlertDialog.setMessage(getResources().getString(R.string.resend_card_dialog_content));
        deleteAlertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteAlertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (card.isMine()) {
                    sendMessage(card.getContent());
                    Snackbar.make(mDrawerLayout, getResources().getString(R.string.success_resend), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Dismiss
                                }
                            })
                            .show();
                } else {
                    Snackbar.make(mDrawerLayout, getResources().getString(R.string.cannot_resend), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Dismiss
                                }
                            })
                            .show();
                }

            }
        });
        deleteAlertDialog.show();
    }

    public void dialogDeleteMsg(final Card card) {
        AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogLightTheme);
        deleteAlertDialog.setTitle(getResources().getString(R.string.delete_card_dialog_title));
        deleteAlertDialog.setMessage(getResources().getString(R.string.delete_card_dialog_content));
        deleteAlertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteAlertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPagerAdapter.deleteCard(card);
                Snackbar.make(mDrawerLayout, getResources().getString(R.string.card_removed), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Dismiss
                            }
                        })
                        .show();

            }
        });
        deleteAlertDialog.show();
    }

    public void dialogEditMsg(final Card card) {
        mMessageEdit.setText(card.getContent());
        mMessageEdit.setSelection(mMessageEdit.getText().length());
    }

    public CardsFragment selectBoard(String name, InetAddress address) {
//        if (!mPagerAdapter.removeFragment(name)) {
        CardsFragment fragment = (CardsFragment) Cache.getInstance().getOrPut(name, new CardsFragment());
        if (fragment.connect(address)) {
            fragment.setColor(randomColor());
            mPagerAdapter.addFragment(name, fragment);
//        }
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mPagerAdapter.getCount() - 1);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return fragment;
    }

    public int randomColor() {
        final Random random = new Random(System.currentTimeMillis());
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (int) ((baseRed + random.nextInt(256)) / 1.8);
        final int green = (int) ((baseGreen + random.nextInt(256)) / 1.8);
        final int blue = (int) ((baseBlue + random.nextInt(256)) / 1.8);

        return Color.rgb(red, green, blue);
    }

    static class PagerAdapter extends FragmentPagerAdapter {
        private final LinkedIterMap<String, CardsFragment> mFragments = new LinkedIterMap<>();

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(String title, CardsFragment fragment) {
            mFragments.put(title, fragment);
        }

        boolean removeFragment(String title) {
            return mFragments.remove(title) != null;
        }

        CardsFragment getFragment(String title) {
            return mFragments.get(title);
        }

        @Override
        public CardsFragment getItem(int position) {
            return mFragments.getValue(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.getKey(position);
        }

        void deleteCard(Card card) {
            for (Map.Entry<String, CardsFragment> val : mFragments.entrySet()) {
                if (val.getValue().getCards().remove(card)) {
                    val.getValue().notifyDataSetChanged();
                }
            }
        }

        void update() {
            for (Map.Entry<String, CardsFragment> val : mFragments.entrySet()) {
                val.getValue().notifyDataSetChanged();
            }
        }

        void moveCard(Card card, int idx) {
            deleteCard(card);
            CardsFragment fragment = mFragments.getValue(idx);
            fragment.getCards().add(card);
            fragment.notifyDataSetChanged();
        }

        class LinkedIterMap<K, V> extends LinkedHashMap<K, V> {
            V getValue(int i) {
                Map.Entry<K, V> entry = this.getEntry(i);
                if (entry != null) {
                    return entry.getValue();
                }
                return null;
            }

            K getKey(int i) {
                Map.Entry<K, V> entry = this.getEntry(i);
                if (entry != null) {
                    return entry.getKey();
                }
                return null;
            }

            Map.Entry<K, V> getEntry(int i) {
                Set<Entry<K, V>> entries = entrySet();
                int j = 0;
                for (Map.Entry<K, V> entry : entries) {
                    if (j++ == i) {
                        return entry;
                    }
                }
                return null;
            }
        }
    }
}
