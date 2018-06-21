package in.co.androidadda.wallpapertrending;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.co.androidadda.wallpapertrending.Fragment.CategoryDetailsFragment;

/**
 * Created by Anand
 */

public class Categories_Adapter extends RecyclerView.Adapter<Categories_Adapter.ViewHolder>{

    ArrayList<Categories_Model_Class> categoriesArray;
    Context context;

    public Categories_Adapter(Context context, ArrayList<Categories_Model_Class> categoriesArray) {
        this.categoriesArray = categoriesArray;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return categoriesArray.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_cardview_model, parent, false);
        return new ViewHolder(view, context, categoriesArray);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Picasso.with(context).load(categoriesArray.get(position).getCategory_image_id()).into(holder.image);
        holder.name.setText(categoriesArray.get(position).getCategory_name());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pair<View, String> pair1 = Pair.create((View) holder.image, holder.image.getTransitionName());
                Intent intent = new Intent(context, CategoryDetailsFragment.class);
                intent.putExtra("Image", categoriesArray.get(position).getCategory_image_id());
                intent.putExtra("categoryName", categoriesArray.get(position).category_name);
                context.startActivity(intent);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView image;
        Context context;
        ArrayList<Categories_Model_Class> arrayList;

        public ViewHolder(View view, Context context, ArrayList<Categories_Model_Class> arrayList) {
            super(view);
            name = view.findViewById(R.id.category_name);
            image = view.findViewById(R.id.category_image);
            this.context = context;
            this.arrayList = arrayList;
        }
    }
}
