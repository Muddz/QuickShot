package muddzdev.pixelshot;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import muddzdev.pixelshot.fragments.BaseFragment;

public class MainActivity extends AppCompatActivity implements PixelShot.PixelShotListener {

    private NonSwipeViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
        setupToolbar();
        setupViewPager();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_save_black_24dp));
        setSupportActionBar(toolbar);
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_jpg:
                PixelShot.of(getTargetView()).setResultListener(this).save();
                break;
            case R.id.menu_pgn:
                PixelShot.of(getTargetView()).setResultListener(this).toPNG().save();
                break;
            case R.id.menu_nomedia:
                PixelShot.of(getTargetView()).setResultListener(this).toNomedia().save();
                break;
        }
        return true;
    }

    private View getTargetView() {
        int currentItem = viewPager.getCurrentItem();
        BaseFragment fragment = (BaseFragment) viewPagerAdapter.getItem(currentItem);
        return fragment.getTargetView();
    }


    @Override
    public void onPixelShotSuccess(String path) {
        Toast.makeText(this, "Image saved at: " + path, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPixelShotFailed() {
        Toast.makeText(this, "Couldn't save image", Toast.LENGTH_LONG).show();
    }


    private void askPermissions() {
        int requestCode = 232;
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, requestCode);
            }
        }
    }
}
