package com.example.netspeedtest

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.File
import java.util.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.text.SimpleDateFormat


class InternetService : Service() {

    private val TAG = InternetService::class.java.simpleName
    val CHANNEL_ID = "Service"
    private var downSpeed = 0.0
    private var upSpeed = 0.0
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var date: Date = Date()
    private val data = StringBuilder()


    companion object {
        var fileName = ""
    }

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        data.append("Download Speed ,Upload Speed,Date,Time")
        mRunnable = Runnable {
            createNotification()
            mHandler.postDelayed(
                mRunnable,
                1000
            )
        }
        mRunnable.run()
        // Create an explicit intent for an Activity in your app


        return START_STICKY
    }


    override fun onDestroy() {
        mHandler.removeCallbacks(mRunnable)
    }

    private fun createNotification() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.isDefaultNetworkActive && cm.isDefaultNetworkActive != null) {
            val nc = cm.getNetworkCapabilities(cm.activeNetwork)
            downSpeed = nc!!.linkDownstreamBandwidthKbps.toDouble()
            upSpeed = nc.linkUpstreamBandwidthKbps.toDouble()
            val spf = SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa")
            data.append("\n $downSpeed kbps,$upSpeed kbps,${ SimpleDateFormat("MMM dd,hh:mm:ss aaa").format(date)}")
            try {
                var file = createFile()
                fileName = file.name
                val out = openFileOutput(file.name, Context.MODE_PRIVATE)
                out.write(data.toString().toByteArray())
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Net Speed Testing ")
            .setContentIntent(pendingIntent)
        NotificationManagerCompat.from(this).apply {
            builder.setContentText("Current Speed \t downLoad $downSpeed kbps \tUpLoad $upSpeed kbps")
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .priority = NotificationCompat.PRIORITY_MIN

            notify(1, builder.build())
        }

    }

    private fun createFile(): File {
        var file: File? = null
        try {
            val myDir = File(getExternalFilesDir(null), "folder")
            myDir.mkdir()

            if (myDir.exists()) {
                file = File.createTempFile(
                    "Internet Speed Results", ".csv",
                    myDir
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file!!
    }
}

