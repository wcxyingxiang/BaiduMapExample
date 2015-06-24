package com.example.baidumapexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.util.MyPoint;
import com.example.util.Planlocation;

public class MainActivity extends Activity {
    MapView mMapView = null;
    BaiduMap mBaiduMap;
    BitmapDescriptor mCurrentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 路径规划功能
                // LatLng start = new LatLng(MyApp.location.getLatitude(),
                // MyApp.location.getLongitude());
                // LatLng end = new LatLng(31.335954,120.61727);
                //
                // MyApp.mapUtil.locationSearch("苏州", start, end, 2);
                // MyApp.mapUtil.setPlanlocation(planlocation);

                // 定位一个点
                // MyPoint myPoint = new MyPoint();
                // myPoint.setLat(31.335954);
                // myPoint.setLng(120.61727);
                // myPoint.setName("苏州火车站");
                // MyApp.mapUtil.showOnePoint(mBaiduMap, myPoint, -1);

                // 显示多个点
                List<MyPoint> list = new ArrayList<MyPoint>();
                MyPoint myPoint1 = new MyPoint();
                myPoint1.setLat(31.335954);
                myPoint1.setLng(120.61727);
                myPoint1.setName("苏州火车站");
                MyPoint myPoint2 = new MyPoint();
                myPoint2.setLat(31.347351);
                myPoint2.setLng(120.717355);
                myPoint2.setName("苏州园区站");
                MyPoint myPoint3 = new MyPoint();
                myPoint3.setLat(31.282627);
                myPoint3.setLng(120.747826);
                myPoint3.setName("文星广场");
                MyPoint myPoint4 = new MyPoint();
                myPoint4.setLat(31.307066);
                myPoint4.setLng(120.591449);
                myPoint4.setName("苏州市政府");
                MyPoint myPoint5 = new MyPoint();
                myPoint5.setLat(31.325205);
                myPoint5.setLng(120.683435);
                myPoint5.setName("苏州中心");

                list.add(myPoint1);
                list.add(myPoint2);
                list.add(myPoint3);
                list.add(myPoint4);
                list.add(myPoint5);

                MyApp.mapUtil.addPt(mBaiduMap, list, -1);

            }
        });
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 开启定位图层
                mBaiduMap.setMyLocationEnabled(true);
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(MyApp.location.getRadius())
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(MyApp.location.getLatitude())
                        .longitude(MyApp.location.getLongitude()).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_geo);
                MyLocationConfiguration config = new MyLocationConfiguration(
                        LocationMode.NORMAL, true, mCurrentMarker);
                mBaiduMap.setMyLocationConfigeration(config);

                LatLng ll = new LatLng(MyApp.location.getLatitude(),
                        MyApp.location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        };
        timer.schedule(task, 3000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    Planlocation planlocation = new Planlocation() {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                // route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(
                        mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                // routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                // route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(
                        mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                // routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {

                // route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
                        mBaiduMap);
                // routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    };

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            // if (useDefaultIcon) {
            // return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            // }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            // if (useDefaultIcon) {
            // return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            // }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            // if (useDefaultIcon) {
            // return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            // }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            // if (useDefaultIcon) {
            // return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            // }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            // if (useDefaultIcon) {
            // return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            // }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            // if (useDefaultIcon) {
            // return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            // }
            return null;
        }
    }
}
