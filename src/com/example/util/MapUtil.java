package com.example.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.baidumapexample.MyApp;
import com.example.baidumapexample.R;

public class MapUtil implements BaiduMap.OnMapClickListener,
		OnGetRoutePlanResultListener {
	private static MapUtil mapUtil;
	private static Context context;
	public BDLocation location;
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	// private PopupOverlay pop;
	public final int CAR = 1;
	public final int BUS = 2;
	public final int BICYLE = 3;
	private Planlocation planlocation;
	public LatLng firstLatLng;
	int nodeIndex = -1;// 节点索引,供浏览节点时使用

	private MapUtil() {
		SDKInitializer.initialize(context);
	}

	/**
	 * 把一个坐标点显示在地图上面.
	 * 
	 * @param mMapView
	 * @param point
	 * @param image
	 *            默认图片资源id
	 */
	public void showOnePoint(final BaiduMap mBaiduMap, final MyPoint point,
			int image) {
		mBaiduMap.clear();

		LatLng llA = new LatLng(point.getLat(), point.getLng());
		BitmapDescriptor bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);

		OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
				.zIndex(9).draggable(true);
		final Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llA);
		mBaiduMap.setMapStatus(u);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(context);
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
				if (marker == mMarkerA) {
					button.setText(point.getName());
					listener = new OnInfoWindowClickListener() {
						public void onInfoWindowClick() {
							mBaiduMap.hideInfoWindow();
						}
					};
					LatLng ll = marker.getPosition();
					InfoWindow mInfoWindow = new InfoWindow(
							BitmapDescriptorFactory.fromView(button), ll, -47,
							listener);
					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				return true;
			}
		});
	}

	/**
	 * 清除地图所有的点
	 * 
	 * @param mMapView
	 */
	public void clearAllPoint(BaiduMap mBaiduMap) {
		mBaiduMap.clear();
	}

	/**
	 * 把点增加到图上面
	 * 
	 * @param mMapView
	 * @param list
	 *            点的信息
	 * @param image
	 *            默认图片资源id
	 */
	public void addPt(final BaiduMap mBaiduMap, final List<MyPoint> list,
			int image) {

		mBaiduMap.clear();
		BitmapDescriptor bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);
		final List<Marker> markerList = new ArrayList<Marker>();
		for (int i = 0; i < list.size(); i++) {
			LatLng llA = new LatLng(list.get(i).getLat(), list.get(i).getLng());
			OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
					.zIndex(9).draggable(true);
			markerList.add((Marker) (mBaiduMap.addOverlay(ooA)));
		}
		MapStatusUpdate u = MapStatusUpdateFactory
				.newLatLng(getCenterPointer(list));
		mBaiduMap.setMapStatus(u);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(context);
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
				for (int i = 0; i < list.size(); i++) {
					if (marker == markerList.get(i)) {
						button.setText(list.get(i).getName());
						break;
					}
				}
				listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				};
				LatLng ll = marker.getPosition();
				InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(button), ll, -47, listener);
				mBaiduMap.showInfoWindow(mInfoWindow);

				return true;
			}
		});

	}

	/**
	 * 得到坐标群的中心点坐标
	 * 
	 * @param list
	 * @return
	 */
	public LatLng getCenterPointer(List<MyPoint> list) {

		double maxLng = 0;
		double maxLat = 0;
		double minLng = 0;
		double minLat = 0;

		for (int i = 0; i < list.size(); i++) {
			double lat = list.get(i).getLat();
			double lng = list.get(i).getLng();
			if (i == 0) {
				minLat = maxLat = lat;
				minLng = maxLng = lng;
				continue;
			}

			if (minLat > lat) {
				minLat = lat;
			}
			if (minLng > lng) {
				minLng = lng;
			}
			if (maxLat < lat) {
				maxLng = lat;
			}
			if (maxLng < lng) {
				maxLng = lng;
			}
		}
		LatLng southwest = new LatLng(minLat, minLng);
		LatLng northeast = new LatLng(maxLat, maxLng);
		LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
				.include(southwest).build();
		return bounds.getCenter();
	}

	/**
	 * 开始定位
	 * 
	 * @param option
	 *            定位参数
	 */
	public void beginOrientation(LocationClientOption option) {
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(myListener);
		if (option == null) {
			option = setLocationOption();
		}
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	public void stopOrientation() {
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(myListener);
			mLocClient.stop();
		}
	}

	/**
	 * 设置定位相关参数
	 */
	private LocationClientOption setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		return option;
	}

	/**
	 * 停止百度地图服务
	 */
	public void onDestory() {
		stopOrientation();
	}

	/**
	 * 通过一系列坐标在百度地图描述指定的拆线
	 * 
	 * @param mMapView
	 * @param list
	 */
	 public void drawMapWay(BaiduMap mBaiduMap, List<LatLng> list) {
	
	 }

	/**
	 * 通过一系列坐标在百度地图描述指定的拆线
	 * 
	 * @param mMapView
	 * @param list
	 * @param lineSymbol
	 *            不填默认为红色
	 */
//	 public void drawMapWay(BaiduMap mBaiduMap, List<MyPoint> list) {
//	 
//	 }

	/**
	 * 算出二点坐标距离差
	 * */
	public float getTwoGeogeLength(double lat1, double lng1, double lat2,
			double lng2) {
		float[] result = new float[1];
		Location.distanceBetween(lat1, lng1, lat2, lng2, result);
		return result[0];
	}

	/**
	 * 搜索路线，比如从火车站到汽车站路线
	 * 
	 * @param city
	 *            // 所在城市
	 * @param start
	 *            // 开始地点
	 * @param end
	 *            // 结束地点
	 * @param type
	 *            // 类型：汽车、公交、步行(CAR,BUS,BICYLE)
	 */
	public void locationSearch(String city, LatLng start, LatLng end, int type) {
		PlanNode starts = PlanNode.withLocation(start);
		PlanNode ends = PlanNode.withLocation(end);

		RoutePlanSearch mSearch = null;
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

		switch (type) {
		case CAR:
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(starts)
					.to(ends));
			break;
		case BUS:
			mSearch.transitSearch((new TransitRoutePlanOption()).from(starts)
					.city(city).to(ends));
			break;
		case BICYLE:
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(starts)
					.to(ends));
			break;
		default:
			mSearch.transitSearch((new TransitRoutePlanOption()).from(starts)
					.city(city).to(ends));
			break;
		}
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		// TODO Auto-generated method stub
		if (planlocation != null) {
			planlocation.onGetDrivingRouteResult(result);
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		// TODO Auto-generated method stub
		if (planlocation != null) {
			planlocation.onGetTransitRouteResult(result);
		}
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		// TODO Auto-generated method stub
		if (planlocation != null) {
			planlocation.onGetWalkingRouteResult(result);
		}
	}

	/**
	 * 创建mapUtil对象
	 * 
	 * @param context
	 * @return
	 */
	public static MapUtil getIntance(Context context) {

		mapUtil.context = context;

		if (mapUtil == null) {
			synchronized (MapUtil.class) {
				mapUtil = new MapUtil();
			}
		}

		return mapUtil;
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (location == null)
				return;
			mapUtil.location = location;
			MyApp.location = location;
			mLocClient.stop();
		}

	}

	public Planlocation getPlanlocation() {
		return planlocation;
	}

	public void setPlanlocation(Planlocation planlocation) {
		this.planlocation = planlocation;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
