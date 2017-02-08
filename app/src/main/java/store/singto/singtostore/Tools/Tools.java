package store.singto.singtostore.Tools;
import android.graphics.Rect;
import android.icu.text.DateFormat;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Singto on 1/5/2017 AD.
 */

public class Tools {
    public static boolean isEmail(String email){
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }else {
            return true;
        }
    }

    public static String getDateOnly(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return format.format(date);
    }

    public static String getTimeOnly(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return format.format(date);

    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
            outRect.left = space;
            outRect.right = space/2;
            outRect.bottom = space/2;
        }
    }

    public static class CellItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public CellItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space/2;
        }
    }
}
