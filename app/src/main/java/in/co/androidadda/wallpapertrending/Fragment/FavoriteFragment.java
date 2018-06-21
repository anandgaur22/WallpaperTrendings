package in.co.androidadda.wallpapertrending.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import java.util.ArrayList;

import in.co.androidadda.wallpapertrending.Activities.AboutActivity;
import in.co.androidadda.wallpapertrending.Adapter.FavoriteFragmentAdapter;
import in.co.androidadda.wallpapertrending.Activities.MainActivity;
import in.co.androidadda.wallpapertrending.R;
import in.co.androidadda.wallpapertrending.Model.RecyclerItemDecoration;
import in.co.androidadda.wallpapertrending.Model.WallpapersModel;

/**
 * Created by Anand
 */

public class FavoriteFragment extends Fragment {

    Button add_fav;
    ViewPager viewPager;
    ImageView brokenHeart;
    TextView noFav, doubleTap;

    public static ArrayList<WallpapersModel> arrayList;

    RecyclerView recyclerView;
    Context context;
    GridLayoutManager gridLayoutManager;
    FavoriteFragmentAdapter favoriteFragmentAdapter;

    int size;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        context = getContext();

        View view = inflater.inflate(R.layout.favorite_fragment, null);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                new IntentFilter("ReadDatabase"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(deleteEntryReceiver,
                new IntentFilter("DeleteEntry"));

        arrayList = new ArrayList<>();


        add_fav = view.findViewById(R.id.add_fav);
        viewPager = getActivity().findViewById(R.id.view_pager);
        brokenHeart = view.findViewById(R.id.broken_heart);
        noFav = view.findViewById(R.id.no_fav_text);
        doubleTap = view.findViewById(R.id.doubleTap);

        initArrayList();
        Log.i("FAORITES SIZE ", String.valueOf(size));

        toggleUi(size);

        favoriteFragmentAdapter = new FavoriteFragmentAdapter(getContext(), arrayList);
        gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView = view.findViewById(R.id.favorites_rv);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(2));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(favoriteFragmentAdapter);

        return view;
    }

    private void toggleUi(int size) {
        if (size == 0) {
            add_fav.setVisibility(View.VISIBLE);
            brokenHeart.setVisibility(View.VISIBLE);
            noFav.setVisibility(View.VISIBLE);
            add_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(2, true);
                }
            });
            doubleTap.setVisibility(View.VISIBLE);
        } else {
            add_fav.setVisibility(View.INVISIBLE);
            brokenHeart.setVisibility(View.INVISIBLE);
            noFav.setVisibility(View.INVISIBLE);
            doubleTap.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fav_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_database:
                if (size != 0) {
                    Log.i("Size IS", String.valueOf(size));
                    Log.i("INSIDE", "OPTIONS");
                    try {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                        builder.setCancelable(true);
                        builder.setTitle("Warning!");
                        builder.setMessage("This will delete all your Favorites!");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.favDatabaseHelper.deleteAllFavs();
                                deleteEntryReceiver.onReceive(context, new Intent("ReadDatabase"));
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.show();
                    } catch (Exception e) {
                        Log.i("DELETE EXCEPTION", e.toString());
                        Toast.makeText(this.context, "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.menu_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BroadCast READ DATABASE", "RECEIVED");
            Cursor cursor = MainActivity.favDatabaseHelper.readLastFromFav();
            while (cursor.moveToNext()) {
                Log.i("SIZE", String.valueOf(cursor.getCount()));
                Log.i("small", cursor.getString(0));
                Log.i("full", cursor.getString(1));
                Log.i("type", cursor.getString(2));
                Log.i("id", String.valueOf(cursor.getString(3)));
                arrayList.add(new WallpapersModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        Integer.parseInt(cursor.getString(3))
                ));
                Log.i("ARRAYLIST UPDATED SIZE ", String.valueOf(arrayList.size()));
                toggleUi(arrayList.size());
                size = arrayList.size();
                favoriteFragmentAdapter.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver deleteEntryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BroadCast DELETE ENTRY", "RECEIVED");
            initArrayList();
            favoriteFragmentAdapter.notifyDataSetChanged();
        }
    };


    private void initArrayList() {
        arrayList.clear();
        Cursor cursor = MainActivity.favDatabaseHelper.readFavFromDatabase();
        while (cursor.moveToNext()) {
            arrayList.add(new WallpapersModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Integer.parseInt(cursor.getString(3))
            ));
        }
        toggleUi(arrayList.size());
        size = arrayList.size();
        Log.i("SIZE AFTER DELETION", String.valueOf(arrayList.size()));
    }
}
