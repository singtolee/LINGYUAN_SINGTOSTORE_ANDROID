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

    public static class RecyPadding extends RecyclerView.ItemDecoration {
        private int topPadding, rightPadding, bottomPadding, leftPadding;

        public RecyPadding(int top, int right, int bottom, int left) {
            this.topPadding = top;
            this.rightPadding = right;
            this.bottomPadding = bottom;
            this.leftPadding = left;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = topPadding;
            outRect.left = leftPadding;
            outRect.right = rightPadding;
            outRect.bottom = bottomPadding;
        }
    }
}
