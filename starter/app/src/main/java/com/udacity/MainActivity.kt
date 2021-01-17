package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private lateinit var loadingButton: LoadingButton
    private var downloadID: Long = 0
    private var radioId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<RadioGroup>(R.id.radioGroup).setOnCheckedChangeListener { _, checkedId ->
            radioId = checkedId
        }
        loadingButton = findViewById(R.id.custom_button)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton.setOnClickListener {
            if (loadingButton.buttonState == ButtonState.Completed) {
                loadingButton.buttonState = ButtonState.Clicked
                if (radioId == 0) {
                    Toast.makeText(this, R.string.radio_error, Toast.LENGTH_SHORT).show()
                    loadingButton.buttonState = ButtonState.Completed
                    return@setOnClickListener
                }
                download()
            }
        }

        createChannel(CHANNEL_ID, getString(R.string.notification_channel))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                Toast.makeText(applicationContext, R.string.download_complete, Toast.LENGTH_SHORT)
                    .show();
            }

            val fileName = when (radioId) {
                R.id.glideButton -> getString(R.string.button_glide)
                R.id.udacityButton -> getString(R.string.button_udacity)
                R.id.retrofitButton -> getString(R.string.button_retrofit)
                else -> ""
            }

            var statusString = "Unknown"
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> statusString =
                        getString(R.string.status_successful)
                    DownloadManager.STATUS_FAILED -> statusString =
                        getString(R.string.status_failed)
                }
            }

            Log.d(TAG, statusString)

            loadingButton.buttonState = ButtonState.Completed
            notificationManager.sendNotification(applicationContext, fileName, statusString)
        }
    }

    private fun download() {
        loadingButton.buttonState = ButtonState.Loading

        val url = when (radioId) {
            R.id.glideButton -> "https://github.com/bumptech/glide"
            R.id.udacityButton -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
            R.id.retrofitButton -> "https://github.com/square/retrofit"
            else -> ""
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "channelId"
    }
}
