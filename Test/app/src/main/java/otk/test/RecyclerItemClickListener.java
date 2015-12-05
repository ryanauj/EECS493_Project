package otk.test;

/**
 * Created by rj on 12/4/15.
 * Referenced from Jacob Tabak's answer on stack overflow
 *  article http://stackoverflow.com/questions/24471109/recyclerview-onclick/26196831#26196831
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener itemListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector itemGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        itemListener = listener;
        itemGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && itemListener != null && itemGestureDetector.onTouchEvent(e)) {
            itemListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}
