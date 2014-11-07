package ws1415.veranstalterapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ws1415.veranstalterapp.util.LocationUtils;


public class RouteEditorActivity extends Activity implements ActionBar.TabListener {
    private static final String MEMBER_WAYPOINTS = "route_editor_activity_member_waypoints";
    private static final String MEMBER_ROUTE = "route_editor_activity_member_route";

    private static final int MAX_WAYPOINTS = 10;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    private ArrayAdapter<Waypoint> waypointArrayAdapter;
    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_editor);

        waypointArrayAdapter = new WaypointAdapter(
                this,
                R.layout.list_view_item_waypoint,
                R.id.list_view_item_waypoint_name_textview);

        route = null;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MEMBER_WAYPOINTS)) {
                waypointArrayAdapter.addAll((Waypoint[]) savedInstanceState.getParcelableArray(MEMBER_WAYPOINTS));
            }
            if (savedInstanceState.containsKey(MEMBER_ROUTE)) {
                route = (Route) savedInstanceState.getParcelable(MEMBER_ROUTE);
            }
        }

        sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        for(int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2){

            }

            @Override
            public void onPageScrollStateChanged(int arg0){

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Waypoint[] temp = new Waypoint[waypointArrayAdapter.getCount()];
        for (int i = 0; i < waypointArrayAdapter.getCount(); i++) {
            temp[i] = waypointArrayAdapter.getItem(i);
        }
        outState.putParcelableArray(MEMBER_WAYPOINTS, temp);

        if (route != null) {
            outState.putParcelable(MEMBER_ROUTE, route);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(waypointArrayAdapter.getCount() < MAX_WAYPOINTS);
        menu.getItem(0).getIcon().setAlpha(menu.getItem(0).isEnabled() ? 255 : 64);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_add_waypoint) {
            EditorMapFragment mapFragment = (EditorMapFragment) getFragmentByPosition(0);
            GoogleMap map = mapFragment.getMap();
            Waypoint waypoint = Waypoint.create(map.getCameraPosition().target, getString(R.string.route_editor_waypoint_name_format, waypointArrayAdapter.getCount()+1));
            waypointArrayAdapter.add(waypoint);
            mapFragment.updateWaypoint(waypoint);
            invalidateOptionsMenu();
            loadRoute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public ArrayAdapter<Waypoint> getWaypointArrayAdapter() {
        return waypointArrayAdapter;
    }

    public void loadRoute() {
        if (waypointArrayAdapter.getCount() > 1) {
            new RouteLoaderTask().execute(waypointArrayAdapter);
        }
    }

    private void setRoute(Route route) {
        if (this.route != null && this.route.getPolyline() != null) {
            this.route.getPolyline().remove();
        }
        this.route = route;
        EditorMapFragment mapFragment = (EditorMapFragment) getFragmentByPosition(0);
        if (mapFragment == null) {
            Toast.makeText(getApplicationContext(), "mapFragment is null", Toast.LENGTH_LONG).show();
            return;
        }
        mapFragment.updateRoute(route);
    }

    public Route getRoute() {
        return this.route;
    }

    public void showWaypoint(Waypoint waypoint) {
        waypoint.getMarker().showInfoWindow();
        viewPager.setCurrentItem(0);
        EditorMapFragment mapFragment = (EditorMapFragment) getFragmentByPosition(0);
        GoogleMap map = mapFragment.getMap();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(waypoint.getMarkerOptions().getPosition(), 15.0f)));
    }

    private Fragment getFragmentByPosition(int pos) {
        String tag = "android:switcher:" + viewPager.getId() + ":" + pos;
        return getFragmentManager().findFragmentByTag(tag);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Fragment.instantiate(RouteEditorActivity.this, EditorMapFragment.class.getName());
                case 1:
                    return Fragment.instantiate(RouteEditorActivity.this, EditorWaypointsFragment.class.getName());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment_route_editor_map);
                case 1:
                    return getString(R.string.title_fragment_route_editor_waypoints);
            }
            return null;
        }
    }

    public class WaypointAdapter extends ArrayAdapter<Waypoint> {

        public WaypointAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(R.id.list_view_item_waypoint_name_textview);
            textView.setText(getString(R.string.route_editor_waypoint_name_format, (position+1)));
            return view;
        }
    }

    private class RouteLoaderTask extends AsyncTask<ArrayAdapter<Waypoint>, Void, Route> {
        private final String LOG_TAG = RouteLoaderTask.class.getSimpleName();

        @Override
        protected Route doInBackground(ArrayAdapter<Waypoint>... params) {
            if (params.length < 1) return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final ArrayAdapter<Waypoint> waypoints = params[0];

            if (waypoints.getCount() < 2) return null;

            final String BASE_URL = "http://maps.googleapis.com/maps/api/directions/json?";

            final String ORIGIN = positionToString(waypoints.getItem(0).getMarkerOptions().getPosition());
            final String DESTINATION = positionToString(waypoints.getItem(waypoints.getCount()-1).getMarkerOptions().getPosition());
            final String WAYPOINTS = waypointsToString(waypoints);

            Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
            builder.appendQueryParameter("origin", ORIGIN);
            builder.appendQueryParameter("destination", DESTINATION);
            builder.appendQueryParameter("waypoints", WAYPOINTS);
            builder.appendQueryParameter("sensor", "true");

            String jsonString = null;

            try {
                URL url = new URL(builder.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() <= 0) return null;
                jsonString = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error downloading directions.", e);
            }
            finally {
                if (urlConnection != null) urlConnection.disconnect();

                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream.", e);
                    }
                }
            }

            if (jsonString != null) {
                try {
                    return parseJSONString(jsonString);
                }
                catch (JSONException e) {
                    Log.e(LOG_TAG, "Unable to parse JSON string.", e);
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Route route) {
            super.onPostExecute(route);
            if (route == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.route_editor_error_load_route), Toast.LENGTH_SHORT).show();
            }

            setRoute(route);
        }

        private String positionToString(LatLng position) {
            return position.latitude + "," + position.longitude;
        }

        private String waypointsToString(ArrayAdapter<Waypoint> waypoints) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < waypoints.getCount()-1; i++) {
                builder.append(positionToString(waypoints.getItem(i).getMarkerOptions().getPosition()));
                builder.append("|");
            }
            return builder.toString();
        }

        private Route parseJSONString(String jsonString) throws JSONException {
            JSONObject directionsJSON = new JSONObject(jsonString);
            JSONArray routesArray = directionsJSON.getJSONArray("routes");
            if (routesArray.length() <= 0) {
                return null;
            }

            JSONObject route = routesArray.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");

            List<LatLng> line = new ArrayList<LatLng>();
            int distance = 0;

            for (int i = 0; i < legs.length(); i++) {
                JSONObject leg = legs.getJSONObject(i);
                JSONArray steps = leg.getJSONArray("steps");
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = steps.getJSONObject(j);
                    distance += step.getJSONObject("distance").getInt("value");
                    try {
                        line.addAll(LocationUtils.decodePolyline(step.getJSONObject("polyline").getString("points")));
                    }
                    catch (ParseException e) {
                        Log.e(LOG_TAG, "Unable to parse polyline", e);
                        return null;
                    }
                }
            }

            Route out = null;
            try {
                out = new Route(line, distance);
            }
            catch (ParseException e) {
                Log.e(LOG_TAG, "Unable to parse polyline.", e);
                return null;
            }
            return out;
        }
    }

    public static class Route implements Parcelable {
        private PolylineOptions polylineOptions;
        private Polyline polyline;

        private int distance; // Distance in m

        public Route(List<LatLng> line, int distance) throws ParseException {
            this.polylineOptions = new PolylineOptions()
                    .color(Color.BLUE)
                    .width(10.0f);
            this.polylineOptions.addAll(line);

            this.polyline = null;

            this.distance = distance;
        }

        private Route(Parcel in) {
            this.polylineOptions = in.readParcelable(MarkerOptions.class.getClassLoader());
            this.polyline = null;

            this.distance = in.readInt();
        }

        public PolylineOptions getPolylineOptions() {
            return polylineOptions;
        }

        public void setPolyline(Polyline polyline) {
            this.polyline = polyline;
        }

        public Polyline getPolyline() {
            return polyline;
        }

        public String getEncoded() {
            return LocationUtils.encodePolyline(getPolylineOptions().getPoints());
        }

        public int getDistance() {
            return distance;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeParcelable(polylineOptions, flags);
            out.writeInt(distance);
        }

        public static final Parcelable.Creator<Route> CREATOR
                = new Parcelable.Creator<Route>() {
            public Route createFromParcel(Parcel in) {
                return new Route(in);
            }

            public Route[] newArray(int size) {
                return new Route[size];
            }
        };
    }

    public static class Waypoint implements Parcelable {
        private MarkerOptions markerOptions;
        private Marker marker;

        public static Waypoint create(LatLng position, String title) {
            return new Waypoint(
                    new MarkerOptions()
                            .title(title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .position(position)
                            .draggable(true)
            );
        }

        private Waypoint(MarkerOptions options) {
            markerOptions = options;
            marker = null;
        }

        private Waypoint(Parcel in) {
            markerOptions = in.readParcelable(MarkerOptions.class.getClassLoader());
            marker = null;
        }

        public void setMarkerOptions(MarkerOptions markerOptions) {
            this.markerOptions = markerOptions;
        }

        public MarkerOptions getMarkerOptions() {
            return markerOptions;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }

        public Marker getMarker() {
            return marker;
        }

        public String toString() {
            return markerOptions.getPosition().toString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeParcelable(markerOptions, flags);
        }

        public static final Parcelable.Creator<Waypoint> CREATOR
                = new Parcelable.Creator<Waypoint>() {
            public Waypoint createFromParcel(Parcel in) {
                return new Waypoint(in);
            }

            public Waypoint[] newArray(int size) {
                return new Waypoint[size];
            }
        };
    }
}