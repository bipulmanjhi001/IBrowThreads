package orem.gill.ibrowthreads.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 4/21/2016.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;
    private ArrayList<TabsPagerPojo> mList;

    public TabsPagerAdapter(FragmentManager fm, Context mContext, ArrayList<TabsPagerPojo> mList) {
        super(fm);
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public static class TabsPagerPojo {
        public Fragment fragment;
        public String title;

        public TabsPagerPojo(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
}
