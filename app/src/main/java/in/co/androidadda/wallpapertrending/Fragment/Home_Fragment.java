package in.co.androidadda.wallpapertrending.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import in.co.androidadda.wallpapertrending.Activities.AboutActivity;
import in.co.androidadda.wallpapertrending.Adapter.HomeFragmentCustomAdapter;
import in.co.androidadda.wallpapertrending.Interface.onLoadMoreListener;
import in.co.androidadda.wallpapertrending.Model.RecyclerItemDecoration;
import in.co.androidadda.wallpapertrending.Model.WallpapersModel;
import in.co.androidadda.wallpapertrending.R;

/**
 * Created by Anand
 */

public class Home_Fragment extends Fragment {

    public static ArrayList<WallpapersModel> wallpapersModelArrayList;
    public static RecyclerView recyclerView;
    HomeFragmentCustomAdapter homeFragmentCustomAdapter;
    ImageView noNetImage;
    TextView noNetText;
    Button connectBtn;

    GridLayoutManager gridLayoutManager;

    boolean isNetworkConnected;

    public static int pageCount = 2;

    protected Handler handler;

    public static int spanCount = 3;

    public static int wallpaperNumber = 0;

    private int numPages;

    ProgressBar progressBar;

    private boolean isLoading = false;

    private static String homeSite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, null);

        spanCount = 3;

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(loadMoreBroadcastReceiver,
                new IntentFilter("LoadMore"));
      // LocalBroadcastManager.getInstance(getContext()).registerReceiver(initDataBroadcastReceiver,
       //        new IntentFilter("InitData"));

        progressBar = view.findViewById(R.id.progressBar);


        noNetImage = view.findViewById(R.id.noNet);
        noNetText = view.findViewById(R.id.noNetText);
        connectBtn = view.findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });


        wallpapersModelArrayList = new ArrayList<>();
        handler = new Handler();

        homeSite = "http://papers.co/android/";
        initData();

        gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView = view.findViewById(R.id.homeFragment_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        homeFragmentCustomAdapter = new HomeFragmentCustomAdapter(wallpapersModelArrayList, getContext(), recyclerView);
        homeFragmentCustomAdapter.setOnLoadMoreListener(new onLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pageCount < numPages) {
                    try{
                        Log.i("CURRENT PAGE ", String.valueOf(pageCount));
                        Log.i("NUMBER OF  PAGE ", String.valueOf(numPages));
                        wallpapersModelArrayList.add(null);
                        Log.i("INSERTED", "NULL");
                        homeFragmentCustomAdapter.notifyItemInserted(wallpapersModelArrayList.size() - 1);

                    }catch (Exception e){
                    }


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                wallpapersModelArrayList.remove(wallpapersModelArrayList.size() - 1);
                                homeFragmentCustomAdapter.notifyItemRemoved(wallpapersModelArrayList.size());
                            }catch (ArrayIndexOutOfBoundsException e){
                                //Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
                            }
                            Log.i("REMOVED", "NULL");
                            //add items one by one
                            Log.i("INIT", "DATA");
                            new loadMore().execute(homeSite + "page/" + pageCount + "/");
                            homeFragmentCustomAdapter.setLoaded();
                            Log.i("INIT", "FINISHED");
                        }
                    }, 900);
                    isLoading = false;
                }
            }
        });

        recyclerView.setAdapter(homeFragmentCustomAdapter);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(2));

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            boolean n = isNetworkAvailable();
            if (n) {
                initData();
            }
        }
    }


    public void initData() {
        isNetworkConnected = isNetworkAvailable();
        if (isNetworkConnected) {
            noNetImage.setVisibility(View.INVISIBLE);
            noNetText.setVisibility(View.INVISIBLE);
            connectBtn.setVisibility(View.GONE);

            loadFromInternet(homeSite);

        } else {

            noNetImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonetwork));
            noNetImage.setVisibility(View.VISIBLE);
            noNetText.setVisibility(View.VISIBLE);
            connectBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No Internet Connected!", Toast.LENGTH_SHORT).show();

        }
    }

    public class loadMore extends AsyncTask<String, Integer, String> {

        List list;
        List id;

        @Override
        protected String doInBackground(String... params) {
            try {
                isLoading = true;
                Document document = Jsoup.connect(params[0]).get();
                Element wall = document.select("ul.postul").first();
                //Log.i("LIST ", wall.toString());
                Elements url = wall.getElementsByAttribute("src");
                list = url.eachAttr("src");

                if(pageCount <= numPages) {

                    for (int i = 0; i < list.size(); i++) {
                        wallpaperNumber++;
                        String string = list.get(i).toString();
                        String sep[] = string.split("http://");
                        sep[1] = sep[1].replace("android/wp-content/uploads", "wallpaper");
                        String septemp[] = sep[1].split("wallpaper-");
                        //Log.i("SEPARATION ", septemp[1]);
                        if(!septemp[1].contains("250x400.jpg")){
                            //Log.i("YES ", "It will be fixed!");
                            septemp[0] = septemp[0] + "wallpaper-" + septemp[1] + "wallpaper.jpg?download=true";
                            sep[1] = "http://" + septemp[0];
                        }
                        else {
                            Log.i("String ", septemp[0]);
                            septemp[0] = septemp[0] + "wallpaper.jpg?download=true";
                            sep[1] = "http://" + septemp[0];
                        }
                        wallpapersModelArrayList.add(wallpapersModelArrayList.size() - 1, new WallpapersModel(
                                string,///
                                sep[1],
                                "jpg",
                                wallpaperNumber
                        ));
                    }
                }
            } catch (Exception e) {
                Log.i("ERROR", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("COUNT ", String.valueOf(count));
            pageCount++;
            homeFragmentCustomAdapter.notifyItemInserted(wallpapersModelArrayList.size());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_share:
                final Intent intentshare = new Intent(Intent.ACTION_SEND);
                intentshare.setType("text/plain");
                intentshare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shareText) + getContext().getPackageName());
                try {
                    startActivity(Intent.createChooser(intentshare, "Select an action"));
                } catch (android.content.ActivityNotFoundException ex) {
                    // (handle error)
                }
                return true;
            case R.id.grid_two:
                spanCount = 2;
                gridLayoutManager.setSpanCount(spanCount);
                recyclerView.setLayoutManager(gridLayoutManager);
                return true;
            case R.id.grid_three:
                spanCount = 3;
                gridLayoutManager.setSpanCount(spanCount);
                recyclerView.setLayoutManager(gridLayoutManager);
                return true;
            case R.id.refresh:
                pageCount = 2;
                if(!isLoading){
                    recyclerView.setVisibility(View.INVISIBLE);
                    wallpapersModelArrayList.clear();
                    initData();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void loadFromInternet(final String url) {

        if (isNetworkConnected) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wallpapersModelArrayList.clear();
                    //new ReadJSON().execute(url);
                    new ReadHTML().execute(url);
                }
            });
        }
    }

    class ReadHTML extends AsyncTask<String, Integer, String> {

        List list;


        @Override
        protected String doInBackground(String... params) {

            try {

                Document document = Jsoup.connect(params[0]).get();
                Element wall = document.select("ul.postul").first();
                //Log.i("LIST ", wall.toString());
                Elements url = wall.getElementsByAttribute("src");
                Element page = document.select("a.page-numbers").last();
                numPages = Integer.parseInt(page.text());
                Log.i("PAGE NUMBER ", String.valueOf(numPages));
                list = url.eachAttr("src");

                for (int i = 0; i < list.size(); i++) {
                    wallpaperNumber++;
                    String string = list.get(i).toString();
                    String sep[] = string.split("http://");
                    sep[1] = sep[1].replace("android/wp-content/uploads", "wallpaper");
                    String septemp[] = sep[1].split("wallpaper-");
                    septemp[0] = septemp[0] + "wallpaper.jpg?download=true";
                    sep[1] = "http://" + septemp[0];
                    Log.i("String ", sep[1]);
                    //Log.i("URL", string);
                    wallpapersModelArrayList.add(new WallpapersModel(
                            string,///
                            sep[1],
                            "jpg",
                            wallpaperNumber,
                            0
                    ));
                }
            } catch (Exception e) {
                Log.i("ERROR LOADING WEBSITE ", e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Loading Wallpapers!", Toast.LENGTH_SHORT).show();
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("COUNT ", String.valueOf(count));
            homeFragmentCustomAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private static String readURL(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            // create a url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private BroadcastReceiver loadMoreBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new loadMore().execute("http://papers.co/android/page/" + pageCount + "/");
            Log.i("BroadCast LoadMore", "RECEIVED");
            homeFragmentCustomAdapter.notifyDataSetChanged();
            Intent intentView = new Intent("UpdateHomeViewPager");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentView);
//            Log.i("Update View Pager", "SENT");
        }
    };
}
