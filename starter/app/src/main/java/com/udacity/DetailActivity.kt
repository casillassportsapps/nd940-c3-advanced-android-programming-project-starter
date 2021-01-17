package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val manager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        manager.cancelAll()

        val extras = intent.extras
        extras?.let {
            val fileName = it.getString(ARG_FILE_NAME)
            val status = it.getString(ARG_STATUS)

            findViewById<TextView>(R.id.downloadName).text = fileName
            val statusTextView = findViewById<TextView>(R.id.statusName)
            statusTextView.text = status
            if (status == getString(R.string.status_failed)) {
                statusTextView.setTextColor(Color.RED)
            }
        }

        findViewById<Button>(R.id.okButton).setOnClickListener {
            onBackPressed()
        }
    }
}
