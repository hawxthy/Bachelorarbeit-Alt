package ws1415.ps1415.useCases;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import ws1415.ps1415.Activities.ShowInformationActivity;
import ws1415.ps1415.Activities.ShowRouteActivity;
import ws1415.ps1415.R;
import ws1415.ps1415.ServiceProvider;

/**
 * Testet den Use Case "Anzeigen der aktuellen Position".
 *
 * @author Tristan
 */
public class ShowCurrentPositionTest  extends ActivityInstrumentationTestCase2<ShowRouteActivity> {

    private ShowRouteActivity mActivity;
    private LocationManager mLocationManager;

    public ShowCurrentPositionTest() {
        // Bezug auf die ShowInformationActivity
        super(ShowRouteActivity.class);
    }

    @Override
    /**
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();
        mLocationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Prüft, ob GPS aktiviert ist.
     */
    public void testPreConditions() {
        assertTrue(mLocationManager.isProviderEnabled(mLocationManager.GPS_PROVIDER));
    }

    /**
     * Prüft, ob eine Position ermittelt werden kann.
     */
    public void testCurrentPosition() {
        Criteria criteria = new Criteria();
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));
        assertNotNull("location could not be requested", location);
    }

}




















