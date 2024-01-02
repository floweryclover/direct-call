package kr.co.starryflower.directcall

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.starryflower.directcall.databinding.ActivityMainBinding
import android.Manifest
import android.os.Build
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: MyBroadcastReceiver
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private val intentFilter = IntentFilter()
    private val peers = mutableListOf<WifiP2pDevice>()
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        peers.clear()
        peers.addAll(refreshedPeers)

        if (peers.isNotEmpty()) {
            binding.myText.text = peers[0].deviceName
        } else {
            binding.myText.text = "상대 없음"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        receiver = MyBroadcastReceiver(manager, channel, this, peerListListener)

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        registerReceiver(receiver, intentFilter)

        val permissionForWifiDirectif = if (Build.VERSION.SDK_INT < 33) {
            android.Manifest.permission.ACCESS_FINE_LOCATION
        } else {
            android.Manifest.permission.NEARBY_WIFI_DEVICES
        }
        if (checkSelfPermission(permissionForWifiDirectif) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(permissionForWifiDirectif), 1)
        }


        binding.searchButton.setOnClickListener {
            manager.discoverPeers(channel, object: WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("INFO", "discoverPeers().onSuccess()")
                }

                override fun onFailure(p0: Int) {
                    Log.d("ERROR", "discoverPeers().onFailure() $p0")
                }
            })
        }
    }
}