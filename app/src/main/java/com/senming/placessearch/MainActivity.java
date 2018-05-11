package com.senming.placessearch;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.senming.placessearch.Adapter.TopFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TopFragmentPagerAdapter mAdapter = new TopFragmentPagerAdapter(
                this.getSupportFragmentManager(), this );

        mTabLayout = (TabLayout) findViewById(R.id.top_tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);

        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
        linearLayout.setDividerPadding(30);

    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setCustomView(R.layout.tab_top_search);
        mTabLayout.getTabAt(1).setCustomView(R.layout.tab_top_favorites);
    }
}
