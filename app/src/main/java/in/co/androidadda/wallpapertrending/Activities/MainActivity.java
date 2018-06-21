package in.co.androidadda.wallpapertrending.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import in.co.androidadda.wallpapertrending.Utils.FavDatabaseHelper;
import in.co.androidadda.wallpapertrending.Fragment.Categories_Fragment;
import in.co.androidadda.wallpapertrending.Fragment.Downloaded_Fragment;
import in.co.androidadda.wallpapertrending.Fragment.FavoriteFragment;
import in.co.androidadda.wallpapertrending.Fragment.Home_Fragment;
import in.co.androidadda.wallpapertrending.R;
import in.co.androidadda.wallpapertrending.Fragment.Search_Fragment;

import static in.co.androidadda.wallpapertrending.Utils.FavDatabaseHelper.DATABASE_NAME;

public class MainActivity extends AppCompatActivity {

    BottomBar bottomBar;
    public ViewPager viewPager;
    Toolbar toolbar;
    Categories_Fragment categories_fragment;
    Search_Fragment search_fragment;
    Downloaded_Fragment downloaded_fragment;
    Home_Fragment home_fragment;
    FavoriteFragment favorite_fragment;

    Context context;

    public static FavDatabaseHelper favDatabaseHelper;

    public static String DATABASE_FULL_PATH = null;

//    public static  String wallpaperSite1 = "NULL", wallpaperSite2 = "NULL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        String sdCard= Environment.getExternalStorageDirectory().toString();
        File myDir = new File(sdCard + context.getResources().getString(R.string.downloadLocation));

            /*  if specified not exist create new */
        if(!myDir.exists())
        {
            Log.i("CREATED DIR ", myDir.toString());
            myDir.mkdirs();
        }

        try{
            AppRate.with(MainActivity.this)
                .setInstallDays(0) // default 10, 0 means install day.
                    .setLaunchTimes(5) // default 10
                    .setRemindInterval(2) // default 1
                    .setShowLaterButton(true) // default true
                    .setDebug(false) // default false
                    .setCancelable(false) // default false.
                    .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                        @Override
                        public void onClickButton(int which) {
                            Log.d(MainActivity.class.getName(), Integer.toString(which));
                        }
                    })
                    .monitor();

            // Show a dialog if meets conditions
            AppRate.showRateDialogIfMeetsConditions(MainActivity.this);
        }catch (Exception e){
            Log.i("PlayStore Exception", e.toString());
            Toast.makeText(MainActivity.this, "Error Opening PlayStore!", Toast.LENGTH_SHORT).show();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Explore");

        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.preferencesName), MODE_PRIVATE);
//        wallpaperSite1 = preferences.getString("wallpaperSite1", "");
//        wallpaperSite2 = preferences.getString("wallpaperSite2", "");
//        Log.i("WALLPAPER SITE 1", wallpaperSite1);
//        Log.i("WALLPAPER SITE 2", wallpaperSite2);

        boolean firstRun = preferences.getBoolean("firstRun", true);
        if (firstRun) {
            Log.i("Preferences", String.valueOf(preferences.getBoolean("firstRun", true)));
            // here run your first-time instructions, for example :
            Intent intro = new Intent(MainActivity.this, IntroActivity.class);
            startActivityForResult(intro, 1);

        }

        try {
            DATABASE_FULL_PATH = MainActivity.this.getDatabasePath(DATABASE_NAME).toString();
            Log.i("PATH", DATABASE_FULL_PATH);
        } catch (Exception e) {

        }
        boolean checkDb = checkDataBase();

        favDatabaseHelper = new FavDatabaseHelper(MainActivity.this);

        if (!checkDb) {
            Log.i("DATABASE ", "DOES'T EXIST");
            favDatabaseHelper = new FavDatabaseHelper(MainActivity.this);
            Log.i("DATABASE ", "CREATED");
        }

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new setViewAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(2);
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setActionBarTitle("Search");
                    toolbar.setVisibility(View.GONE);
                    bottomBar.selectTabAtPosition(0, true);
                } else if (position == 1) {
                    setActionBarTitle("Categories");
                    toolbar.setVisibility(View.VISIBLE);
                    bottomBar.selectTabAtPosition(1, true);
                } else if (position == 2) {
                    setActionBarTitle("Explore");
                    toolbar.setVisibility(View.VISIBLE);
                    bottomBar.selectTabAtPosition(2, true);
                } else if (position == 3) {
                    setActionBarTitle("Favorites");
                    toolbar.setVisibility(View.VISIBLE);
                    bottomBar.selectTabAtPosition(3, true);
                } else if (position == 4) {
                    setActionBarTitle("Downloaded");
                    toolbar.setVisibility(View.VISIBLE);
                    bottomBar.selectTabAtPosition(4, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setDefaultTab(R.id.tab_home);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_search:
                        viewPager.setCurrentItem(0, true);
                        return;
                    case R.id.tab_categories:
                        viewPager.setCurrentItem(1, true);
                        return;
                    case R.id.tab_home:
                        viewPager.setCurrentItem(2, true);
                        return;
                    case R.id.tab_fav:
                        viewPager.setCurrentItem(3, true);
                        return;
                    case R.id.tab_downloaded:
                        viewPager.setCurrentItem(4, true);
                        return;
                }
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("In Result ", "TRUE");
        if (requestCode == 1) {
            Log.i("In Result REQUEST CODE", "TRUE");
        }
    }


    public class setViewAdapter extends FragmentPagerAdapter {
        public setViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (search_fragment == null) {
                    search_fragment = new Search_Fragment();
                    return search_fragment;
                }
                return search_fragment;
            } else if (position == 1) {
                if (categories_fragment == null) {
                    categories_fragment = new Categories_Fragment();
                    return categories_fragment;
                }
                return categories_fragment;
            } else if (position == 2) {
                if (home_fragment == null) {
                    home_fragment = new Home_Fragment();
                    return home_fragment;
                }
                return home_fragment;
            } else if (position == 3) {
                if (favorite_fragment == null) {
                    favorite_fragment = new FavoriteFragment();
                    return favorite_fragment;
                }
                return favorite_fragment;
            } else if (position == 4) {
                if (downloaded_fragment == null) {
                    downloaded_fragment = new Downloaded_Fragment();
                    return downloaded_fragment;
                }
                return new Downloaded_Fragment();
            } else
                return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("Request COde ", String.valueOf(requestCode));
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DATABASE_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
            Log.i("DATABASE", "EXISTS");
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            Log.i("DATABASE", "DOES'T EXISTS");
        }
        return checkDB != null;
    }


    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(R.string.backpresed_cancel, null)
                .setPositiveButton(R.string.backpresed_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }

                }).create().show();
    }
}
