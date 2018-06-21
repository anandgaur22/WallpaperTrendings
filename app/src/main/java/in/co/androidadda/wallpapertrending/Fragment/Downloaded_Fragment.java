package in.co.androidadda.wallpapertrending.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import in.co.androidadda.wallpapertrending.Activities.AboutActivity;
import in.co.androidadda.wallpapertrending.Adapter.DownloadFragmentAdapter;
import in.co.androidadda.wallpapertrending.R;
import in.co.androidadda.wallpapertrending.Model.RecyclerItemDecoration;

/**
 * Created by Anand
 */

public class Downloaded_Fragment extends Fragment {

    //GridView gridView;
    RecyclerView recyclerView;
    Context context;
    GridLayoutManager gridLayoutManager;
    DownloadFragmentAdapter downloadFragmentAdapter;

    //Directories
    private ArrayList<String> filePaths;
    private ArrayList<String> fileNames;
    private File[] files;

    ImageView noDownloadsImage;
    TextView noDownloadsText;
    Button downloadNow;
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        context = getActivity().getApplicationContext();

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter("Refresh"));

        View view = inflater.inflate(R.layout.downloaded_fragment, null);

        viewPager = getActivity().findViewById(R.id.view_pager);
        noDownloadsImage = view.findViewById(R.id.noDownloads);
        noDownloadsText = view.findViewById(R.id.no_download_text);
        downloadNow = view.findViewById(R.id.download_now);
        downloadNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2, true);
            }
        });

        gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView = view.findViewById(R.id.downloaded_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(2));

        try {
            File imageDir = new File(Environment.getExternalStorageDirectory().toString() + getActivity().getResources().getString(R.string.downloadLocation));
            Log.i("DIR", imageDir.toString());
            if (imageDir.exists()) {
                files = imageDir.listFiles();
                Log.i("FILES", String.valueOf(files.length));
                filePaths = new ArrayList<>();
                fileNames = new ArrayList<>();

                if (files.length > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noDownloadsText.setVisibility(View.GONE);
                    noDownloadsImage.setVisibility(View.GONE);
                    for (int i = 0; i < files.length; i++) {
                        // Get the path of the image file
                        filePaths.add(i, files[i].getAbsolutePath());
                        Log.i("FILES", filePaths.get(i));
                        // Get the name image file
                        fileNames.add(i, files[i].getAbsolutePath());
                        Log.i("FILENAMES", fileNames.get(i));
                    }
                    Log.i("SIZE  ", String.valueOf(filePaths.size()));
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDownloadsText.setVisibility(View.VISIBLE);
                    noDownloadsImage.setVisibility(View.VISIBLE);
                }
            }
            downloadFragmentAdapter = new DownloadFragmentAdapter(getActivity().getApplicationContext(), filePaths, fileNames, getActivity());
            recyclerView.setAdapter(downloadFragmentAdapter);
        } catch (Exception e) {
            Log.i("EXCEPTION ", e.toString());
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public class refreshDownloads extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                File imageDir = new File(Environment.getExternalStorageDirectory().toString() + getActivity().getResources().getString(R.string.downloadLocation));
                Log.i("DIR", imageDir.toString());
                if (imageDir.exists()) {
                    files = imageDir.listFiles();
                    Log.i("FILES", String.valueOf(files.length));
                    if (files.length > 0) {
                        filePaths.clear();
                        fileNames.clear();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.VISIBLE);
                                noDownloadsText.setVisibility(View.GONE);
                                noDownloadsImage.setVisibility(View.GONE);
                            }
                        });

                        for (int i = 0; i < files.length; i++) {
                            // Get the path of the image file
                            filePaths.add(i, files[i].getAbsolutePath());
                            Log.i("FILES", filePaths.get(i));
                            // Get the name image file
                            fileNames.add(i, files[i].getAbsolutePath());
                            Log.i("FILENAMES", fileNames.get(i));
                        }
                        Log.i("SIZE  ", String.valueOf(filePaths.size()));
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.GONE);
                                noDownloadsText.setVisibility(View.VISIBLE);
                                noDownloadsImage.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }
            } catch (Exception e) {
                Log.i("EXCEPTION", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("REFRESHED", "True!");
            downloadFragmentAdapter.notifyDataSetChanged();
            Log.i("ITEM RANGE CHANGED", "ITEM SET CHANGED");
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new refreshDownloads().execute();
        }
    };
}

