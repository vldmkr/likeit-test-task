package org.best.taskboard;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;

import org.best.taskboard.fragments.CardsFragment;
import org.best.taskboard.models.Card;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private ActionBarDrawerToggle mDrawerToggle;
    private PagerAdapter mPagerAdapter;
    private AlertDialog.Builder mAlertDialog;

    final CardsFragment todo = new CardsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);

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

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());


        todo.getCards().add(new Card("HELLO"));
        mPagerAdapter.addFragment("TODO", todo);
        mPagerAdapter.addFragment("OLOLO", new CardsFragment());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);

        mTabLayout.setupWithViewPager(mViewPager);

        mCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateCard();
            }
        });
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
                            todo.getCards().add(new Card(cardDescription.getText().toString()));
                            todo.notifyDataSetChanged();

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

    static class PagerAdapter extends FragmentPagerAdapter {
        private final LinkedIterMap<String, CardsFragment> mFragments = new LinkedIterMap<>();

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(String title, CardsFragment fragment) {
            mFragments.put(title, fragment);
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
