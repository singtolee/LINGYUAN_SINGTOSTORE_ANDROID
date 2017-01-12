package store.singto.singtostore.Tools;

import android.widget.GridView;
import android.content.Context;
import android.util.AttributeSet;
/**
 * Created by Singto on 1/12/2017 AD.
 */

public class MyGridView extends GridView {
    public MyGridView(Context context){
        super(context);
    }
    public MyGridView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        if(getLayoutParams().height == LayoutParams.WRAP_CONTENT){
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }else {
            heightSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
