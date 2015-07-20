package com.example.phross.grouponcat;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.phross.grouponcat.data.Deal;
import com.example.phross.grouponcat.data.DealLocation;
import com.example.phross.grouponcat.data.GetDealData;
import com.example.phross.grouponcat.data.Option;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by phross on 7/20/15.
 */
public class GrouponDeals {
    public static final String CLIENT_ID = "1D62CAE756A99D4783B724D2EFC4796F";
    private static final String TAG = "GrouponDeals";

    static boolean servicesConnected(Context context) {
        return ConnectionResult.SUCCESS == GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
    }

//    public static void checkNew(List<Deal> current, Context context) {
//        DatabaseHelper dbHelper = new DatabaseHelper(context);
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        DealMap seen = new DealMap();
//        seen.load(db);
//
//        Iterator<Map.Entry<String, Long>> iterator = seen.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            if (new Date().getTime() - iterator.next().getValue() > 1 * 60 * 1000)
//                iterator.remove();
//        }
//
//        NotificationManager notificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        List<Deal> newDeals = new ArrayList<Deal>();
//
//        List<String> newDealsIds = new ArrayList<String>();
//
//        for (Deal deal : current) {
//            if (!seen.containsKey(deal.id)) {
//                newDeals.add(deal);
//                newDealsIds.add(deal.id);
//            }
//
//            seen.put(deal.id, new Date().getTime());
//        }
//
//        if (newDeals.size() > 0) {
//            if (newDeals.size() == 1) {
//                Deal deal = newDeals.get(0);
//
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("http://www.groupon.com/dispatch/us/deal/"
//                                + deal.id));
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(
//                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.BigTextStyle notificationBuilder = new NotificationCompat.BigTextStyle(
//                        new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_status)
//                                .setContentTitle(deal.shortAnnouncementTitle)
//                                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.meow))
//                                .setContentText(deal.announcementTitle)
//                                .setContentInfo((int) deal.distance + "m")
//                                .setContentIntent(pendingIntent)).bigText(deal.announcementTitle);
//
//                notificationManager.notify(0, notificationBuilder.build());
//
//                final Intent pebbleIntent = new Intent(
//                        "com.getpebble.action.SEND_NOTIFICATION");
//
//                final Map<String, String> data = new HashMap<String, String>();
//
//                data.put("title", deal.shortAnnouncementTitle);
//                data.put("body", deal.announcementTitle);
//
//                final JSONObject jsonData = new JSONObject(data);
//                final String notificationData = new JSONArray().put(jsonData)
//                        .toString();
//
//                pebbleIntent.putExtra("messageType", "PEBBLE_ALERT");
//                pebbleIntent.putExtra("sender", "Groupon Alert");
//                pebbleIntent.putExtra("notificationData", notificationData);
//
//                context.sendBroadcast(pebbleIntent);
//            } else {
//                StringBuilder deals = new StringBuilder();
//
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.putExtra("deals", newDealsIds.toArray(new String[0]));
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(
//                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.InboxStyle inboxBuilder = new NotificationCompat.InboxStyle(
//                        new NotificationCompat.Builder(context)
//                                .setSmallIcon(R.drawable.ic_status)
//                                .setWhen(new Date().getTime())
//                                .setContentIntent(pendingIntent)
//                                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.meow))
//                                .setContentTitle(
//                                        newDeals.size() + " deals nearby")
//                                .setTicker(newDeals.size() + " deals nearby"));
//
//                for (Deal deal : newDeals) {
//                    SpannableStringBuilder line = new SpannableStringBuilder();
//
//                    line.append(deal.shortAnnouncementTitle).append(" ").append(String.format("(%dm)", (int) deal.distance));
//                    line.setSpan(new StyleSpan(Typeface.BOLD), 0, deal.shortAnnouncementTitle.length(),
//                            Spannable.SPAN_MARK_MARK);
//
//                    deals.append(deal.shortAnnouncementTitle + "\n");
//
//                    inboxBuilder.addLine(line);
//                }
//
//                notificationManager.notify(0, inboxBuilder.build());
//
//                final Intent pebbleIntent = new Intent(
//                        "com.getpebble.action.SEND_NOTIFICATION");
//
//                final Map<String, String> data = new HashMap<String, String>();
//
//                data.put("title", newDeals.size() + " deals nearby");
//                data.put("body", deals.toString());
//
//                final JSONObject jsonData = new JSONObject(data);
//                final String notificationData = new JSONArray().put(jsonData)
//                        .toString();
//
//                pebbleIntent.putExtra("messageType", "PEBBLE_ALERT");
//                pebbleIntent.putExtra("sender", "Groupon Alert");
//                pebbleIntent.putExtra("notificationData", notificationData);
//
//                context.sendBroadcast(pebbleIntent);
//            }
//        }
//
//        seen.save(db);
//
//        db.close();
//    }

    public static Deal getDeal(String id, Location location) {
        Log.d(TAG, "Get deal");
        Gson gson = new Gson();

        Deal deal = null;

//        try {
        String url = "http://api.groupon.com/v2/deals/" + id + ".json?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d(TAG, "start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "success");
                Log.d(TAG, response.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d(TAG, "retry");
                // called when request is retried
            }
        });
//            URL url = new URL("http://api.groupon.com/v2/deals/" + id
//                    + ".json?client_id=" + CLIENT_ID);
//            BufferedReader in;
//            try {
//                in = new BufferedReader(new InputStreamReader(url.openStream()));
//
//                GetDealData data = gson.fromJson(in, GetDealData.class);
//
//                deal = data.deal;
//
//                for (Option option : deal.options) {
//                    for (DealLocation dealLocation : option.dealLocations) {
//                        Location rLocation = new Location("Groupon");
//
//                        rLocation.setLatitude(dealLocation.lat);
//                        rLocation.setLongitude(dealLocation.lng);
//                        Log.d(TAG, deal.toString());
//                        if (deal.distance == 0
//                                || location.distanceTo(rLocation) < deal.distance) {
//                            deal.address = dealLocation.streetAddress1;
//                            deal.distance = location.distanceTo(rLocation);
//                        }
//                    }
//                }
//
//                in.close();
//            } catch (IOException exception) {
////                 TODO Auto-generated catch-block stub.
//                exception.printStackTrace();
//            }
//        } catch (MalformedURLException exception) {
////             TODO Auto-generated catch-block stub.
//            exception.printStackTrace();
//        }

        return deal;
    }

//    public static List<Deal> getDeals(Location location, int radius) {
//        List<Deal> nearby = new ArrayList<Deal>();
//
//        Gson gson = new Gson();
//
//        try {
//            URL url = new URL("http://api.groupon.com/v2/deals.json?client_id="
//                    + CLIENT_ID + "&lat=" + location.getLatitude() + "&lng="
//                    + location.getLongitude());
//            BufferedReader in;
//            try {
//                in = new BufferedReader(new InputStreamReader(url.openStream()));
//
//                getDealsData data = gson.fromJson(in, getDealsData.class);
//
//                for (Deal deal : data.deals) {
//                    boolean isNear = false;
//
//                    for (Option option : deal.options) {
//                        for (RedemptionLocation redemptionLocation : option.redemptionLocations) {
//                            Location rLocation = new Location("Groupon");
//
//                            rLocation.setLatitude(redemptionLocation.lat);
//                            rLocation.setLongitude(redemptionLocation.lng);
//
//                            if (location.distanceTo(rLocation) < radius
//                                    && !deal.isSoldOut) {
//                                isNear = true;
//
//                                deal.address = redemptionLocation.streetAddress1;
//                                deal.distance = location.distanceTo(rLocation);
//
//                                break;
//                            }
//                        }
//
//                        if (isNear)
//                            break;
//                    }
//
//                    if (isNear)
//                        nearby.add(deal);
//                }
//
//                in.close();
//            } catch (IOException exception) {
//                 //TODO Auto-generated catch-block stub.
//                exception.printStackTrace();
//            }
//        } catch (MalformedURLException exception) {
//             //TODO Auto-generated catch-block stub.
//            exception.printStackTrace();
//        }
//
//        return nearby;
//    }
}