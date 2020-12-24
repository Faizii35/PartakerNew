package com.it.partaker.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.it.partaker.activities.HomeNGOActivity
import com.it.partaker.activities.MainActivity
import com.it.partaker.activities.MainReceiverActivity

class MyFirebaseMessaging : FirebaseMessagingService() {
    override fun onMessageReceived(mRemoteMessage: RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)

        val sented = mRemoteMessage.data["sented"]
        val user = mRemoteMessage.data["user"]
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && sented == firebaseUser.uid){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                sendOreoNotification(mRemoteMessage)
            }
            else {
                sendNotification(mRemoteMessage)
            }
        }
    }

    private fun sendNotification(mRemoteMessage: RemoteMessage)
    {

        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification = mRemoteMessage.notification
        val j = user!!.replace("\\[D]".toRegex(), "").toInt()

        val intent = when (title) {
            "Post (Donation) Approval" -> {
                Intent(this,MainActivity::class.java)
            }
            "Donation Approval" -> {
                Intent(this,MainActivity::class.java)
            }

            "Post (Request) Approval" -> {
                Intent(this,MainReceiverActivity::class.java)
            }
            "Request Approval" -> {
                Intent(this,MainReceiverActivity::class.java)
            }
            else -> {
                Intent(this,HomeNGOActivity::class.java)
            }
        }

        val bundle = Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if(j>0){
            i=j
        }

        noti.notify(i,builder.build())

    }

    private fun sendOreoNotification(mRemoteMessage: RemoteMessage) {

        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification = mRemoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this,MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OreoNotification(this)
        val builder : Notification.Builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon)
        var i = 0
        if(j>0){
            i=j
        }

        oreoNotification.getManager!!.notify(i,builder.build())
    }
}