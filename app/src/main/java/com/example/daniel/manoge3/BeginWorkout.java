package com.example.daniel.manoge3;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class BeginWorkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_workout);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), BeginWorkout.this);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.begin_workout_container);
        mViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_begin_workout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    // -----------------
    class PagerAdapter extends FragmentPagerAdapter {
        String tabTitles[] = new String[] {"Pick Routine", "Pick Exercise"};
        Context context;

        public PagerAdapter(FragmentManager fm, Context context){
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BeginWorkoutTab1Routine();
                case 1:
                    return new BeginWorkoutTab2Exercise();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
