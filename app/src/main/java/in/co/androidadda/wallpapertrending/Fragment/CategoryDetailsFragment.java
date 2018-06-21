package in.co.androidadda.wallpapertrending.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import in.co.androidadda.wallpapertrending.Adapter.CategoryDetailsFragmentAdapter;
import in.co.androidadda.wallpapertrending.Interface.onLoadMoreListener;
import in.co.androidadda.wallpapertrending.Model.RecyclerItemDecoration;
import in.co.androidadda.wallpapertrending.Model.WallpapersModel;
import in.co.androidadda.wallpapertrending.R;

/**
 * Created by Anand
 */

public class CategoryDetailsFragment extends Activity {

    Toolbar toolbar;
    String categoryName;
    public static RecyclerView recyclerView;
    CategoryDetailsFragmentAdapter categoryDetailsFragmentAdapter;
    GridLayoutManager gridLayoutManager;
    ArrayList<WallpapersModel> wallpapersModels;

    private Handler handler;
    private int pageCount = 1;

    private String categorySearchUrl;

    Context context;

    AdView bannerAd;
    CardView disableAdBlock;

    ProgressBar progressBar;

    private static int numPages, currPg = 1;

    private static String categorySite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_details_fragment);

        disableAdBlock = findViewById(R.id.disableAdBlock);

        wallpapersModels = new ArrayList<>();
        handler = new Handler();
        context = getApplicationContext();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = findViewById(R.id.progressBar);

        categorySite = "https://mobile.alphacoders.com/by-resolution/1/";
        categoryName = getIntent().getStringExtra("categoryName");
        toolbar.setTitle(categoryName);
        switch (categoryName) {
            case "Abstract":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Abstract/1080x1920-Wallpapers/?page=";
                break;
            case "Animal":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Animal/1080x1920-Wallpapers/?page=";
                break;
            case "Anime":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Anime/1080x1920-Wallpapers/?page=";
                break;
            case "Cityscape":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Man-Made/1080x1920-Wallpapers/?page=";
                break;
            case "Comics":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Comics/1080x1920-Wallpapers/?page=";
                break;
            case "Nature":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Earth/1080x1920-Wallpapers/?page=";
                break;
            case "Patterns":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Pattern/1080x1920-Wallpapers/?page=";
                break;
            case "Vehicles":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Vehicles/1080x1920-Wallpapers/?page=";
                break;
            case "3D":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "3D/1080x1920-Wallpapers/?page=";
                break;
            case "Sci-Fi":
                numPages =0;
                currPg = 0;
                categorySearchUrl = categorySite + "Sci-Fi/1080x1920-Wallpapers/?page=";
                break;
        }
        getCategoryWallpaper(categorySearchUrl + currPg);

        recyclerView = findViewById(R.id.detailsCategory_rv);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        categoryDetailsFragmentAdapter = new CategoryDetailsFragmentAdapter(wallpapersModels, getApplicationContext(), recyclerView);
        categoryDetailsFragmentAdapter.setOnLoadMoreListener(new onLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (currPg <= numPages){
                    pageCount++;
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    wallpapersModels.add(null);
                    Log.i("INSERTED", "NULL");
                    categoryDetailsFragmentAdapter.notifyItemInserted(wallpapersModels.size() - 1);
                    //Log.i("SIZE ", String.valueOf(wallpapersModelArrayList.size()));

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wallpapersModels.remove(wallpapersModels.size() - 1);
                            categoryDetailsFragmentAdapter.notifyItemRemoved(wallpapersModels.size());
                            Log.i("REMOVED", "NULL");
                            //add items one by one
                            Log.i("INIT", "DATA");
                            new loadMore().execute(categorySearchUrl + pageCount);
                            categoryDetailsFragmentAdapter.setLoaded();
                            Log.i("INIT", "FINISHED");
                        }
                    }, 700);
                }
            }
        });

        recyclerView.setAdapter(categoryDetailsFragmentAdapter);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(2));
        MobileAds.initialize(context, getResources().getString(R.string.CATEGORY_BANNED_ID));
        bannerAd = new AdView(context);
        bannerAd = findViewById(R.id.bannerAdView);
        AdRequest adRequest = new AdRequest.Builder()
               /* .addTestDevice("02147518DD550E863FFAA08EA49B5F41")
                .addTestDevice("4F18060E4B4A11E00C6E6C3B8EEF6353")*/
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        bannerAd.loadAd(adRequest);
        bannerAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                Log.i("I KI VALUE ", String.valueOf(i));
                if(i == 3){
                    disableAdBlock.setVisibility(View.INVISIBLE);
                }else{
                    disableAdBlock.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                disableAdBlock.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onPause() {
        if (bannerAd != null) {
            bannerAd.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerAd != null) {
            bannerAd.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (bannerAd != null) {
            bannerAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class loadMore extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
                Element wall = document.select("div.thumb-container").first();
                Elements img = wall.getElementsByAttribute("src");
                Elements widList = wall.getElementsByAttribute("alt");
                List list = img.eachAttr("src");
                List id = widList.eachAttr("alt");

                if (currPg <= numPages) {
                    for (int i = 0; i < list.size(); i++) {
                        String wallUrl = list.get(i).toString();
                        String wallId = id.get(i).toString();
                        String sep[] = wallId.split("Wallpaper ");
                        wallpapersModels.add(wallpapersModels.size() - 1, new WallpapersModel(
                                wallUrl,///
                                wallUrl.replace("thumb-", ""),
                                "jpg",
                                Integer.valueOf(sep[1])
                        ));
                    }
                }
                else return null;
            } catch (Exception e) {
                Log.i("ERROR 2", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            categoryDetailsFragmentAdapter.notifyItemInserted(wallpapersModels.size());
            currPg++;
        }
    }

    private void getCategoryWallpaper(String url) {
        new Read().execute(url);
    }

    class Read extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
                Element wall = document.select("div.thumb-container").first();
                Elements img = wall.getElementsByAttribute("src");
                Elements widList = wall.getElementsByAttribute("alt");
                Element page = document.select("ul.pagination.pagination").first();
                Elements pageNum = page.getElementsByAttribute("href");
                List pageText = pageNum.eachText();
                String temp = pageText.get(pageText.size() - 2).toString();
                numPages = Integer.parseInt(temp);
                Log.i("PAGINATION", pageText.get(pageText.size() - 2).toString());
                List list = img.eachAttr("src");
                List id = widList.eachAttr("alt");

                for (int i = 0; i < list.size(); i++) {
                    String wallUrl = list.get(i).toString();
                    String wallId = id.get(i).toString();
                    String sep[] = wallId.split("Wallpaper ");
                    wallpapersModels.add(new WallpapersModel(
                            wallUrl,///
                            wallUrl.replace("thumb-", ""),
                            "jpg",
                            Integer.valueOf(sep[1])
                    ));
                }
            } catch (Exception e) {
                Log.i("ERROR 1", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            categoryDetailsFragmentAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            currPg++;
        }
    }
}
