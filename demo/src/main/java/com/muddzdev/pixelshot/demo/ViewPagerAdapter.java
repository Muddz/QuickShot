package com.muddzdev.pixelshot.demo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.muddzdev.pixelshot.demo.fragments.SurfaceViewFragment;
import com.muddzdev.pixelshot.demo.fragments.TextureViewFragment;
import com.muddzdev.pixelshot.demo.fragments.ViewFragment;

/**
 * Created by Muddz on 23-08-2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String titles[] = {"View", "SurfaceView", "TextureView"};
    private Fragment fragments[] = {new ViewFragment(), new SurfaceViewFragment(), new TextureViewFragment()};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }


}
