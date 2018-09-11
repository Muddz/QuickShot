package muddzdev.pixelshot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import muddzdev.pixelshot.fragments.SurfaceViewFragment;
import muddzdev.pixelshot.fragments.TextureViewFragment;
import muddzdev.pixelshot.fragments.ViewFragment;

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
