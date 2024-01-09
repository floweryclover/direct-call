package kr.co.starryflower.directcall

import android.os.Build

class Permissions {
    companion object {
        fun getPermissionForWifiDirect() : String {
            return if (Build.VERSION.SDK_INT < 33) {
                android.Manifest.permission.ACCESS_FINE_LOCATION
            } else {
                android.Manifest.permission.NEARBY_WIFI_DEVICES
            }
        }
    }
}