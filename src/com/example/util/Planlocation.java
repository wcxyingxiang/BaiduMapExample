package com.example.util;

import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;


public interface Planlocation {
	public void onGetWalkingRouteResult(WalkingRouteResult arg0);

	public void onGetTransitRouteResult(TransitRouteResult arg0);

	public void onGetDrivingRouteResult(DrivingRouteResult arg0);
}
