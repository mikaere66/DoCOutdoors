package com.michaelrmossman.docoutdoors.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.michaelrmossman.docoutdoors.MainActivity
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.utils.NOTIFICATION_CHANNEL_DESC_UPD
import com.michaelrmossman.docoutdoors.utils.NOTIFICATION_CHANNEL_NAME_UPD
import com.michaelrmossman.docoutdoors.utils.NOTIFICATION_CHANNEL_UPD_ID
import com.michaelrmossman.docoutdoors.utils.NOTIFICATION_ID
import com.michaelrmossman.docoutdoors.utils.NOTIFICATION_NEW_TITLE
import com.michaelrmossman.docoutdoors.utils.NOTIFICATION_UPD_TITLE
import com.michaelrmossman.docoutdoors.utils.PENDING_INTENT_REQUEST_CODE

fun makeStatusNotification(
    context: Context, message: String, numNewAlerts: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        /* Create the NotificationChannel, but only for API 26+, because the
           NotificationChannel class is new & not in the support library */
        val nameUpdating = NOTIFICATION_CHANNEL_NAME_UPD
        val importanceUpd = NotificationManager.IMPORTANCE_DEFAULT
        val channelUpdating = NotificationChannel(
            NOTIFICATION_CHANNEL_UPD_ID, nameUpdating, importanceUpd
        ).apply {
            description = NOTIFICATION_CHANNEL_DESC_UPD
        }

        // Add the channel
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager?

        notificationManager?.createNotificationChannel(
            channelUpdating
        )
    }

    // Create pending intent
    val intent = Intent(context, MainActivity::class.java)
    // Optional : add extras, if you need to pass data to the target activity
    // e.g. intent.putExtra("key", "value")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    val pendingIntent = PendingIntent.getActivity(
        context,
        PENDING_INTENT_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
    )

    // Create the notification
    val builder = NotificationCompat.Builder(
        context, NOTIFICATION_CHANNEL_UPD_ID
    )
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setContentTitle(
        when (numNewAlerts) {
            -1   -> NOTIFICATION_UPD_TITLE
            else -> NOTIFICATION_NEW_TITLE
        }
    )
    .setContentText(message)
    .setContentIntent(
        when (numNewAlerts) {
            -1   -> null
            else -> pendingIntent
        }
    )
    .setAutoCancel(true) // Automatically dismiss notification when tapped
    .setPriority(
        when (numNewAlerts) {
            -1   -> NotificationCompat.PRIORITY_LOW
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
    )

    // Check the permissions
    if (
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Consider calling ActivityCompat#requestPermissions here
        // to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }

    // Show the notification
    NotificationManagerCompat.from(
        context
    ).notify(
        NOTIFICATION_ID, builder.build()
    )
}