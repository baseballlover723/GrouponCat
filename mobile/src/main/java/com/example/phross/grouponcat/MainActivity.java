package com.example.phross.grouponcat;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "Main Activity";
    private ProgressBar progressBar;
    private DealAdapter dealAdapter;
    private ListView listView;
    private TextView noDealsFoundView;
    private TextView lookingForDealsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = new ProgressBar(this);
        addContentView(progressBar, new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        dealAdapter = new DealAdapter(this);

        listView = (ListView) findViewById(R.id.ListView1);
        noDealsFoundView = (TextView) findViewById(R.id.noDealsView);
        lookingForDealsView = (TextView) findViewById(R.id.lookingForDealsView);

        noDealsFoundView.setVisibility(TextView.INVISIBLE);
        lookingForDealsView.setVisibility(TextView.INVISIBLE);
        listView.setVisibility(ListView.INVISIBLE);
        listView.setAdapter(dealAdapter);
        listView.setOnItemClickListener(this);

        test();

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

    public void test() {
//        final Location location = locationClient.getLastLocation();
        final Location location = new Location("us");
        location.setLatitude(41.897607);
        location.setLongitude(-87.624062);
        final String[] ids = {"ba984176-47db-fd4b-693a-5b7a507676a0",
                "daf41a19-2d44-b30e-a5ed-dca6eab6c818"};

        AsyncTask<Void, Void, List<Deal>> getDeals = new AsyncTask<Void, Void, List<Deal>>() {

            @Override
            protected void onPreExecute() {
                Log.d(TAG, "in pre execute");
                progressBar.setVisibility(ProgressBar.VISIBLE);
                listView.setVisibility(ListView.INVISIBLE);
                noDealsFoundView.setVisibility(TextView.INVISIBLE);
                lookingForDealsView.setVisibility(TextView.VISIBLE);
            }

            @Override
            protected List<Deal> doInBackground(Void... params) {
                List<Deal> deals = new ArrayList<Deal>();
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                deals = GrouponDeals.getDeals(location, 5000);
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
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                lookingForDealsView.setVisibility(TextView.INVISIBLE);
//                progressBar.setVisibility(View.GONE);
                if (result.size() != 0) {
                    listView.setVisibility(ListView.VISIBLE);
                } else {
                    noDealsFoundView.setVisibility(TextView.VISIBLE);
                }
                dealAdapter.setData(result);
            }
        };

        getDeals.execute();
        Log.d(TAG, "done");
    }
//    private void getDeal(String id, Location location) {
//        Log.d(TAG, "Get deal");
//
//        Deal deal = null;
//
////        try {
//        String url = "http://api.groupon.com/v2/deals/" + id + ".json?client_id=" + CLIENT_ID;
//        AsyncHttpClient client = new AsyncHttpClient();
//        final RequestParams params = new RequestParams();
//        params.put("location", location);
//        client.get(url, params, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                // called before request is started
//                Log.d(TAG, "start");
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // called when response HTTP status is "200 OK"
//                Log.d(TAG, "success");
//                Log.d(TAG, response.toString());
//                ArrayList<Deal> list = new ArrayList<Deal>();
//
//                list.add(doSomethingWithDeal(response, ));
////                setData(list);
//            }
//
//            @Override
//            public void onRetry(int retryNo) {
//                Log.d(TAG, "retry");
//                // called when request is retried
//            }
//        });
//    }
}
