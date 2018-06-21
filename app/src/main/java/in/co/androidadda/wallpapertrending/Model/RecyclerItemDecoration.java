package in.co.androidadda.wallpapertrending.Model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Anand
 */

public class RecyclerItemDecoration extends RecyclerView.ItemDecoration{

    int space;

    public RecyclerItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = 0;
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space+2;
        } else {
            outRect.top = space +2;
        }
    }
}
