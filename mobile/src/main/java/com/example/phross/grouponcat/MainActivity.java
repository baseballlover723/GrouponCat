package com.example.phross.grouponcat;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.phross.grouponcat.data.Deal;
import com.example.phross.grouponcat.data.SettingValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LocationListener {
    private static final String TAG = "Main Activity";

    private LocationManager locationManager;

    private ProgressBar progressBar;
    private DealAdapter dealAdapter;
    private ListView listView;
    private TextView noDealsFoundView;
    private TextView lookingForDealsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000* SettingValues.refreshRate, 35, this);

        progressBar = new ProgressBar(this);

        setMainScreen();

        dealAdapter = new DealAdapter(this);

        listView.setAdapter(dealAdapter);
        listView.setOnItemClickListener(this);

//        test();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.groupon.com/dispatch/us/deal/" + dealAdapter.getItem(position).id));

        startActivity(intent);
    }

    @Override
    public void onLocationChanged(final Location location) {

        AsyncTask<Void, Void, List<Deal>> getDeals = new AsyncTask<Void, Void, List<Deal>>() {
            @Override
            protected void onPreExecute() {
                Log.d(TAG, "in pre execute");
                setLoading();
            }

            @Override
            protected List<Deal> doInBackground(Void... params) {
                List<Deal> deals = new ArrayList<Deal>();
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                deals = GrouponDeals.getDeals(location);
//                for (String id : ids) {
//                    deals.add(GrouponDeals.getDeal(id, location));
//                }
                Log.d(TAG, "got all the deals");
                return deals;
            }

            @Override
            protected void onPostExecute(List<Deal> result) {
                Log.d(TAG, "in post execute");
                Collections.sort(result, new Comparator<Deal>() {
                    @Override
                    public int compare(Deal lhs, Deal rhs) {
                        return (int) (lhs.distance - rhs.distance);
                    }
                });
                setAfterQuery(result.size() != 0);
                dealAdapter.setData(result);
            }
        };

        getDeals.execute();
        Log.d(TAG, "done");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "status changed");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "provider enabled");
        setMainScreen();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled");
        setNoGPSScreen();
    }

    public void test() {
//        final Location location = locationClient.getLastLocation();
        final Location location = new Location("us");
        location.setLatitude(41.897607);
        location.setLongitude(-87.624062);
        final String[] ids = {"ba984176-47db-fd4b-693a-5b7a507676a0",
                "daf41a19-2d44-b30e-a5ed-dca6eab6c818"};

    }


    public void setMainScreen() {
        setContentView(R.layout.activity_main);
        addContentView(progressBar, new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        listView = (ListView) findViewById(R.id.ListView1);
        noDealsFoundView = (TextView) findViewById(R.id.noDealsView);
        lookingForDealsView = (TextView) findViewById(R.id.lookingForDealsView);

        noDealsFoundView.setVisibility(TextView.INVISIBLE);
        lookingForDealsView.setVisibility(TextView.INVISIBLE);
        listView.setVisibility(ListView.INVISIBLE);
    }

    public void setNoGPSScreen() {
        setContentView(R.layout.gps_off);
    }

    public void setLoading() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        listView.setVisibility(ListView.INVISIBLE);
        noDealsFoundView.setVisibility(TextView.INVISIBLE);
        lookingForDealsView.setVisibility(TextView.VISIBLE);
    }

    public void setAfterQuery(boolean foundDeals) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        lookingForDealsView.setVisibility(TextView.INVISIBLE);
        if (foundDeals) {
            listView.setVisibility(ListView.VISIBLE);
        } else {
            noDealsFoundView.setVisibility(TextView.VISIBLE);
        }
    }
}
