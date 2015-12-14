package otk.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Tim on 12/3/2015.
 */
public class StreetView extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama streetViewPanorama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                         .findFragmentById(R.id.street_view_panorama);
        //                .findFragmentById(R.id.locStreetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(new LatLng(42.0, -83.0));
    }

    public boolean onTouch(View v, MotionEvent motionEvent) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        Log.e("StreetView", "TouchEvent");
        return true;
    }
}
