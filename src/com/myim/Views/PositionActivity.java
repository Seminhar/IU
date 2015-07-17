package com.myim.Views;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.location.Location;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.example.IU.R;

import com.myim.Beans.NearByUser;
import com.myim.Game.DazzlingMenuActivity;
import com.myim.Game.GameNumberActivity;
import com.myim.Game.game1tipsActivity;
import com.myim.NetService.Constant;
import com.myim.NetService.HttpConnet;
import com.myim.Util.NearByUserUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.widget.Toast.makeText;


public class PositionActivity extends Activity implements AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, LocationSource, AMap.OnMarkerClickListener, AMapLocationListener {
    private MapView mapView;
    private AMap aMap;
    private AMapLocation aMapLocation;
    private LocationSource.OnLocationChangedListener locationListener;
    private LocationManagerProxy locationManagerProxy;
    private ProgressDialog progressDialog;
    private Button zoom;
    private Boolean nbuLoaded = false;
    private PolylineOptions polylineOptions;
    private ArrayList<NearByUser> nearByUsers;
    private HashMap<String, Integer> markerUsersMap;
    public Handler d = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();

            if (msg.what == Constant.LOCATE_SUC) {
                showMarkers(nearByUsers);
                Toast.makeText(PositionActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
            } else if (msg.what == (Constant.LOCATE_ERR)) {
                Toast.makeText(PositionActivity.this, "网络异常加载失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == (Constant.NO_NEARBY)) {
                Toast.makeText(PositionActivity.this, "没有周围用户", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.position);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setTitle("请等待");
        progressDialog.setMessage("正在加载周围用户");
        progressDialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        t.start();
    }

    public void sendMyLocation() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
        if (aMapLocation != null) {

            HttpConnet con = new HttpConnet();
            HttpPost httpPost = con.getHttpPost("nearby");
            HttpEntity entity = null;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("un", Constant.USER_NAME));
            params.add(new BasicNameValuePair("action", "udshake"));   //   ?action=nbu  near by user
            params.add(new BasicNameValuePair("lat", aMapLocation.getLatitude() + "")); //?lat=...
            params.add(new BasicNameValuePair("lng", aMapLocation.getLongitude() + ""));  // ? lng=...


            try {

                entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(entity);
                HttpParams httpParams = new BasicHttpParams();
                int timeoutConnection = 3000;
                HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);

                int timeoutSocket = 3000;
                HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
                System.out.println(httpPost.toString());
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
                BasicHttpResponse httpResponse = null;
                httpResponse = (BasicHttpResponse) httpClient.execute(httpPost);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                d.sendEmptyMessage(Constant.LOCATE_ERR);
            } catch (ConnectTimeoutException e) {
                Log.i("", "Connect Timeout yoyo");
                e.printStackTrace();
                d.sendEmptyMessage(Constant.LOCATE_ERR);
            } catch (IOException e) {
                e.printStackTrace();
                d.sendEmptyMessage(Constant.LOCATE_ERR);
            }


        }
        //           }
//        });
//        t.start();

    }

    public void loadNearByUser() {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sendMyLocation();
                if (aMapLocation != null) {
//                    String lat = "22.837401";
//                    String lng ="108.233203";
                    HttpConnet con = new HttpConnet();
                    HttpPost httpPost = con.getHttpPost("nearby");
                    HttpEntity entity = null;

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("un", Constant.USER_NAME));
                    params.add(new BasicNameValuePair("action", "nbu"));   //   ?action=nbu  near by user
                    params.add(new BasicNameValuePair("lat", aMapLocation.getLatitude() + "")); //?lat=...
                    params.add(new BasicNameValuePair("lng", aMapLocation.getLongitude() + ""));  // ? lng=...


                    try {

                        entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        httpPost.setEntity(entity);
                        HttpParams httpParams = new BasicHttpParams();
                        int timeoutConnection = 3000;
                        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);

                        int timeoutSocket = 3000;
                        HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
                        System.out.println(httpPost.toString());
                        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
                        BasicHttpResponse httpResponse = null;
                        httpResponse = (BasicHttpResponse) httpClient.execute(httpPost);

                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            Object result = null;

                            result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");

                            System.out.println(result.toString());
                            if (result.equals("{}")) {
                                Log.i("NearBy:", "找不到周围人");
                                d.sendEmptyMessage(Constant.NO_NEARBY);
                            } else {

                                JSONObject json = null;
                                try {
                                    json = new JSONObject(result.toString());
                                    nearByUsers = new ArrayList<NearByUser>();

                                    nearByUsers = NearByUserUtil.getNearByUserListFromJson(json,PositionActivity.this);
                                    d.sendEmptyMessage(Constant.LOCATE_SUC);
                                    //showMarkers(nearByUsers);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    d.sendEmptyMessage(Constant.LOCATE_ERR);
                                }
                            }
                        } else {
                            d.sendEmptyMessage(Constant.LOCATE_ERR);

                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        d.sendEmptyMessage(Constant.LOCATE_ERR);
                    } catch (ConnectTimeoutException e) {
                        Log.i("", "Connect Timeout yoyo");
                        e.printStackTrace();
                        d.sendEmptyMessage(Constant.LOCATE_ERR);
                    } catch (IOException e) {
                        e.printStackTrace();
                        d.sendEmptyMessage(Constant.LOCATE_ERR);
                    }


                }
            }
        });
        t.start();


    }

    public Integer init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
            // loadNearByUser();
        }
        return 1;
    }

    public void setUpMap() {
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        //   aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

    }

    public void showMarkers(ArrayList<NearByUser> list) {
        if (aMap != null) {
            markerUsersMap = new HashMap<String, Integer>();
            int i = 0;
            for (NearByUser nbu : list) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(nbu.getUser().getNickName());
                markerOptions.position(new LatLng(nbu.getLat(), nbu.getLng()));
                markerOptions.draggable(true);
                Marker marker = aMap.addMarker(markerOptions);
                markerUsersMap.put(marker.getId(), i);
                i++;


            }

        }


    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationListener = onLocationChangedListener;
        if (locationManagerProxy == null) {
            locationManagerProxy = LocationManagerProxy.getInstance(this);
            locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);

        }
    }

    @Override
    public void deactivate() {
        locationListener = null;
        if (locationManagerProxy != null) {
            locationManagerProxy.removeUpdates(this);
            locationManagerProxy.destroy();
        }
        locationManagerProxy = null;
    }

    public void clickme(View V) {
        Log.i("hello", "hello");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (locationListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {

                this.aMapLocation = aMapLocation;
                locationListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                if (!nbuLoaded) {
                    Log.i("asd", aMapLocation.getLatitude() + "  " + aMapLocation.getLongitude());

                    loadNearByUser();

                    nbuLoaded = true;

                }

                Log.i("LatLng:", " Lat:" + aMapLocation.getLatitude() + " Lng:" + aMapLocation.getLongitude());
            } else {
                Log.e("AmapErr", "Location ERR:" + aMapLocation.getAMapException().getErrorCode());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        //View infoWindow =getLayoutInflater().inflate(R.layout.custom_window,null) ;
        //  render(marker,infoWindow);


        return null;
    }

    public void render(Marker marker, View infoWindow) {
        ImageView img = (ImageView) infoWindow.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.profile);
        TextView proName = (TextView) infoWindow.findViewById(R.id.proName);
        TextView location = (TextView) infoWindow.findViewById(R.id.location);

        String title = marker.getTitle();
        if (title != null) {
            proName.setText(title);
        } else
            proName.setText("");
        NearByUser nbu = nearByUsers.get(markerUsersMap.get(marker.getId()));
        String dis = nbu.getDistance() + "";
        int distance = (int) (nbu.getDistance() * 1000);
        location.setText(distance + "米");


    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(R.layout.custom_window, null);
        render(marker, infoWindow);


        return infoWindow;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent();
        NearByUser nbu = nearByUsers.get(markerUsersMap.get(marker.getId()));
        Bundle bundle = new Bundle();
        bundle.putSerializable("nearByUsername", nbu.getUser());
        intent.putExtras(bundle);
        Random random = new Random();
        Boolean a;

        a = random.nextBoolean();


        if (a) {

            intent.setClass(PositionActivity.this, game1tipsActivity.class);

        } else {
            intent.setClass(PositionActivity.this, DazzlingMenuActivity.class);
        }

        startActivity(intent);
        PositionActivity.this.finish();

    }

}
