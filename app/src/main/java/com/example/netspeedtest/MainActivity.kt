package com.example.netspeedtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.netspeedtest.InternetService.Companion.fileName
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        btnSend.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnStart -> {
                Toast.makeText(this, "Starting...", Toast.LENGTH_SHORT).show()
                val internetServiceIntent = Intent(this, InternetService::class.java)
                startService(internetServiceIntent)

            }
            R.id.btnStop -> {
                Toast.makeText(this, "Stopping..", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, InternetService::class.java)
                stopService(intent)
            }
            R.id.btnSend -> {
                Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, InternetService::class.java)
                stopService(intent)
                try {

                    val fileLocation = File(filesDir, fileName)
                    val path = FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.netspeedtest.fileprovider",
                        fileLocation
                    )

                    val fileIntent = Intent(Intent.ACTION_SEND)
                    fileIntent.type = "text/csv"
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
                    fileIntent.putExtra(Intent.EXTRA_TEXT, "need speed")
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path)
                    startActivity(Intent.createChooser(fileIntent, "Send mail"))

                }catch (e:Exception){
                    e.printStackTrace()
                }


            }
        }
    }

}
