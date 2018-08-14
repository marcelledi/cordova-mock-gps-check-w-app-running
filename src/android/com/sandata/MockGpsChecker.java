package com.sandata;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MockGpsChecker extends CordovaPlugin{

    private int MY_PERMISSIONS_REQUEST = 0;

    private JSONArray arrayGPS = new JSONArray();
    private JSONObject objGPS = new JSONObject();
    private com.sandata.MockGpsChecker mContext;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        mContext = this;
	contextGeral = this.cordova.getActivity().getApplicationContext(); 
        if (action.equals("check")) {
            objGPS = new JSONObject();
            if (android.os.Build.VERSION.SDK_INT < 18) {
                if (Secure.getString(this.cordova.getActivity().getContentResolver(), Secure.ALLOW_MOCK_LOCATION).equals("0")){
                    objGPS.put("isMock",false);
                }else{
                    objGPS.put("isMock",true);
                }

            }
            else {
                objGPS.put("isMock",areThereMockPermissionApps(mContext.cordova.getActivity()));
            }
            Log.i("Location", "isMock: "+objGPS.get("isMock"));
            callbackContext.success(objGPS);
            return true;
        }else {
            return false;
        }

    }

    public static String areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        // Check for System App //
                        if(!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)) {
                            if (requestedPermissions[i]
                                    .equals("android.permission.ACCESS_MOCK_LOCATION")
                                    && !applicationInfo.packageName.equals(context.getPackageName())) {
				    	return isAppRunning(context, applicationInfo.packageName);
// 					if(isAppRunning(context, applicationInfo.packageName)){
// 						count++;
// 					}
                                
                            }
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }
        if (count > 0)
            return "entrou";
        return "nentrou";
    }
	
	public static String isAppRunning(Context context, String packageName) {
	    String apps = "";
             ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
             List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            if (procInfos != null)
            {
                for ( RunningAppProcessInfo processInfo : procInfos) {
			apps = apps + ", " + processInfo.processName;
                    if (processInfo.processName.equals(packageName)) {
                        return apps + "Entrou no IF";
                    }
                }
            }
            return apps;
        }

}
