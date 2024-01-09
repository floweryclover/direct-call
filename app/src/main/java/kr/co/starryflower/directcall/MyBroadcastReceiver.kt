package kr.co.starryflower.directcall

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.os.Build
import android.util.Log

class MyBroadcastReceiver(private val wifiP2pManager: WifiP2pManager, private val channel: WifiP2pManager.Channel, private val activity: Activity, private val peerListListener: PeerListListener) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent:Intent) {
        Log.d("INFO", "OnReceive()")
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // 이 코드가 실행되고 있다는 것은 이미 P2P 권한이 부여되어 discoverPeers()가 호출되었다는 뜻, 따라서 assert함
                assert(activity.checkSelfPermission(Permissions.getPermissionForWifiDirect()) == PackageManager.PERMISSION_GRANTED)

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