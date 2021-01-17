package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0
const val ARG_FILE_NAME = "fileName"
const val ARG_STATUS = "status"

fun NotificationManager.sendNotification(context: Context, fileName: String, status: String) {
    val intent = Intent(context, DetailActivity::class.java).apply {
        putExtras(Bundle().apply {
            putString(ARG_FILE_NAME, fileName)
            putString(ARG_STATUS, status)
        })
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val builder =
        NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_description))
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                context.getString(R.string.notification_action),
                pendingIntent
            )
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}