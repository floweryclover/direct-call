package kr.co.starryflower.directcall

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.WpsInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kr.co.starryflower.directcall.databinding.ActivityMainBinding
import kr.co.starryflower.directcall.Permissions

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

        binding.peers.removeAllViews()
        for (peer in peers) {
            val button = Button(this)
            button.text = peer.deviceName
            button.setOnClickListener {
                val config = WifiP2pConfig().apply {
                    deviceAddress = peer.deviceAddress
                    wps.setup = WpsInfo.PBC
                }
                manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {

                    }

                    override fun onFailure(p0: Int) {
                        Toast.makeText(
                            this@MainActivity,
                            "연결에 실패했습니다. 재시도하세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            binding.peers.addView(button)
        }

        discoverPeers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        if (checkSelfPermission(Permissions.getPermissionForWifiDirect()) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Permissions.getPermissionForWifiDirect()), 1)
        }
    }

    public override fun onResume() {
        super.onResume()
        receiver = MyBroadcastReceiver(manager, channel, this, peerListListener)
        registerReceiver(receiver, intentFilter)

        discoverPeers()
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun discoverPeers() {
        assert(checkSelfPermission(Permissions.getPermissionForWifiDirect()) == PackageManager.PERMISSION_GRANTED)
        manager.discoverPeers(channel,  object : ActionListener {
            override fun onSuccess() {
                Log.d("SUCCESS", "")
            }

            override fun onFailure(p0: Int) {
                Log.d("ERROR", "$p0")
            }
        })
    }
}