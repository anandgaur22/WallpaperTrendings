package in.co.androidadda.wallpapertrending.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import in.co.androidadda.wallpapertrending.FullWallpaperViewActivity;
import in.co.androidadda.wallpapertrending.Activities.MainActivity;
import in.co.androidadda.wallpapertrending.R;
import in.co.androidadda.wallpapertrending.Model.WallpapersModel;

/**
 * Created by Anand
 */

public class FavoriteFragmentAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<WallpapersModel> wallpapersModels;
    Animation animation;

    public FavoriteFragmentAdapter(Context context, ArrayList<WallpapersModel> wallpapersModels) {
        this.context = context;
        this.wallpapersModels = wallpapersModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        viewHolder = new FavoriteFragmentHolder(v, context, wallpapersModels);
        animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (wallpapersModels != null) {

            Uri uri = Uri.parse(wallpapersModels.get(position).getWallpaperURL());
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setLowResImageRequest(ImageRequest.fromUri(wallpapersModels.get(position).getWallpaperURL()))
                    .build();
            ((FavoriteFragmentHolder)holder).downloadedImage.setController(controller);

            ((FavoriteFragmentHolder) holder).deleteDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.favDatabaseHelper.deleteEntry(wallpapersModels.get(position).getWallpaperURL());
                    Intent intent = new Intent("DeleteEntry");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return wallpapersModels.size();
    }

    public static class FavoriteFragmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView downloadedImage;
        ImageView deleteDownload;
        View view;
        Context context;
        ArrayList<WallpapersModel> wallpapersModels;

        public FavoriteFragmentHolder(View itemView, Context context, ArrayList<WallpapersModel> wallpapersModels) {
            super(itemView);
            this.context = context;
            downloadedImage = itemView.findViewById(R.id.downloadImage);
            this.wallpapersModels = wallpapersModels;
            itemView.setOnClickListener(this);
            this.deleteDownload = itemView.findViewById(R.id.deleteDownload);
            this.view = itemView.findViewById(R.id.viewBar);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context, FullWallpaperViewActivity.class);
            intent.putExtra("caller", "Favorites");
            intent.putExtra("position", position);
            intent.putExtra("id", wallpapersModels.get(position).getWallId());
            intent.putExtra("fullUrl", wallpapersModels.get(position).getWallpaperFullURL());
            intent.putParcelableArrayListExtra("array", wallpapersModels);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);

        }
    }
}
