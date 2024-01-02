package kr.co.starryflower.directcall

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class MyBroadcastReceiver(wifiP2pManager: WifiP2pManager, channel: WifiP2pManager.Channel, activity: Activity, peerListListener: PeerListListener) : BroadcastReceiver() {
    private val wifiP2pManager = wifiP2pManager
    private val channel = channel
    private val activity = activity
    private val peerListListener = peerListListener

    override fun onReceive(context: Context, intent:Intent) {
        Log.d("INFO", "OnReceive()")
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                val permissionForWifiDirectif = if (Build.VERSION.SDK_INT < 33) {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                } else {
                    android.Manifest.permission.NEARBY_WIFI_DEVICES
                }
                if (activity.checkSelfPermission(permissionForWifiDirectif) == PackageManager.PERMISSION_DENIED) {
                    activity.requestPermissions(arrayOf(permissionForWifiDirectif), 1)
                }

                wifiP2pManager.requestPeers(channel, peerListListener)
                Log.d("INFO", "onReceive() -> WIFI_P2P_PEERS_CHANGED_ACTION")
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

            }
        }
    }
}