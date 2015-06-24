package com.example.baidumapexample;

import android.app.Application;

import com.baidu.location.BDLocation;
import com.example.util.MapUtil;

public class MyApp extends Application {

	public static BDLocation location;
	public static MapUtil mapUtil;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mapUtil	=	MapUtil.getIntance(this);
		mapUtil.beginOrientation(null);
	}
}
