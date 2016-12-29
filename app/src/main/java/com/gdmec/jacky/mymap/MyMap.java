package com.gdmec.jacky.mymap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.gdmec.jacky.myguard.R;

public class MyMap extends Activity {
    private MapView mapView;
    private BaiduMap baiduMap;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setCenter(new LatLng(location.getLatitude(), location.getLongitude()));

        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private Marker markerA;
    private InfoWindow infoWindow;
    private LatLng dajidian = new LatLng(23.3906, 113.4535);
    private PoiSearch mPoiSearch;
    private TextView change_type;
    private TextView flytojidian;
    private TextView poisearch;
    private TextView gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_mymap);
        change_type = (TextView) findViewById(R.id.change_type);
        flytojidian = (TextView) findViewById(R.id.flytojidian);
        poisearch = (TextView) findViewById(R.id.poisearch);
        gps = (TextView) findViewById(R.id.gps);
        change_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapType();
            }
        });
        flytojidian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCenter(dajidian);
            }
        });
        poisearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POISearch();
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSLocation();
            }
        });

        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.bright_red));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("地图服务");
        mLeftImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLeftImgv.setImageResource(R.drawable.back);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String hint = "纬度" + latLng.latitude + "\n经度:" + latLng.longitude;
                Toast.makeText(MyMap.this, hint, Toast.LENGTH_SHORT).show();

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (marker != markerA) {
                    return false;
                }
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                button.setText("更改位置");
                button.setTextColor(Color.BLACK);
                final LatLng ll = marker.getPosition();
                Point p = baiduMap.getProjection().toScreenLocation(ll);
                p.y -= 47;
                LatLng llInfo = baiduMap.getProjection().fromScreenLocation(p);
                InfoWindow.OnInfoWindowClickListener listener
                        = new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        LatLng llNew = new LatLng(ll.latitude + 0.005, ll.longitude + 0.005);
                        marker.setPosition(llNew);
                        baiduMap.hideInfoWindow();

                    }
                };
                infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), llInfo, -47, listener);
                baiduMap.showInfoWindow(infoWindow);
                return false;
            }
        });
        mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener getPoiSearchResultListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    baiduMap.clear();
                    PoiOverlay overlay = new MyPoiOverlay(baiduMap);
                    overlay.zoomToSpan();
                    Toast.makeText(MyMap.this, "总数查到" + poiResult.getTotalPageNum() + "个信息点，分为" + poiResult.getTotalPageNum() + "页", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MyMap.this, "抱歉,为找到结果", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyMap.this, poiDetailResult.getName() + "\n你是吃货，鉴定完毕", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(getPoiSearchResultListener);

    }

    private void setMapType() {
        if (baiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE) {
            baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        } else {
            baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }
    }

    private void setCenter(LatLng latLng) {
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
    }

    private void setOverlay() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        OverlayOptions overlayOptions = new MarkerOptions().position(dajidian).icon(bitmapDescriptor);
        markerA = (Marker) baiduMap.addOverlay(overlayOptions);

    }

    private void setMapText() {
        OverlayOptions overlayOptions = new TextOptions().text("大机电").fontColor(0xFFFF00FF).fontSize(24).bgColor(0xAAFFFF00).position(dajidian).rotate(45);
        baiduMap.addOverlay(overlayOptions);
    }

    private void POISearch() {
        mPoiSearch.searchNearby(new PoiNearbySearchOption().location(dajidian).keyword("餐厅").radius(1000).pageNum(1));

    }

    private void GPSLocation() {
        String serviceString = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(serviceString);
        String provider = LocationManager.GPS_PROVIDER;
        Location location = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);

    }

    private class MyPoiOverlay extends com.baidu.mapapi.overlayutil.PoiOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {

            super.onPoiClick(i);
            PoiInfo poi = getPoiResult().getAllPoi().get(i);
            if (poi.hasCaterDetails) {
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));

            }
            return true;
        }

    }
}
