package com.sandata;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Arrays;

public class MockGpsChecker extends CordovaPlugin{

    private int MY_PERMISSIONS_REQUEST = 0;

    private JSONArray arrayGPS = new JSONArray();
    private JSONObject objGPS = new JSONObject();
    private com.sandata.MockGpsChecker mContext;

    @Override
    public JSONArray execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        mContext = this;
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
                objGPS.put("isMock",areThereMockPermissionApps(mContext.cordova.getActivity(), data));
            }
            Log.i("Location", "isMock: "+objGPS.get("isMock"));
            callbackContext.success(objGPS);
            return data;
        }else {
            return data;
        }

    }

    public static boolean areThereMockPermissionApps(Context context, JSONArray whiteList) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                //Se este aplicativo estiver na whiteList, pule esta interacao
                if (Arrays.asList(whiteList).contains(applicationInfo.packageName)) {
                    continue;
                }
                
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
                                count++;
                            }
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

}
