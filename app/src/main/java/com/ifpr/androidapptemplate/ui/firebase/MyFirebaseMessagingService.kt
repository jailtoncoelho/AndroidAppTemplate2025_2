package com.ifpr.androidapptemplate.ui.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context // ✅ IMPORT NECESSÁRIO
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ifpr.androidapptemplate.MainActivity
import com.ifpr.androidapptemplate.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "MENSAGEM RECEBIDA! De: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Payload de Dados: ${remoteMessage.data}")
            val status = remoteMessage.data["status"]
            if (status != null) {
                sendStatusNotification(status)
            } else {
                remoteMessage.data["message"]?.let { message ->
                    sendDefaultNotification(message)
                }
            }
        } else {
            remoteMessage.notification?.let {
                Log.d(TAG, "Payload de Notificação: ${it.body}")
                sendDefaultNotification(it.body)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "NOVO TOKEN GERADO: $token")
        // Enviar token para o seu servidor.
    }

    private fun sendDefaultNotification(messageBody: String?) {
        if (messageBody.isNullOrBlank()) return

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "fcm_default_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        // ✅ CORREÇÃO APLICADA AQUI
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Notificações Gerais", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
        Log.d(TAG, "Tentando exibir notificação padrão...")
    }

    private fun sendStatusNotification(status: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = "status_channel"
        // ✅ CORREÇÃO APLICADA AQUI
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Status do Pedido", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val targetProgress = when (status.lowercase()) {
            "em preparo" -> 33
            "saindo para entrega" -> 66
            "entregue" -> 100
            else -> 0
        }

        val remoteViews = RemoteViews(packageName, R.layout.custom_notification)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setCustomContentView(remoteViews)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)

        val notificationId = 1001
        var currentProgress = 0

        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                if (currentProgress <= targetProgress) {
                    remoteViews.setTextViewText(R.id.status_text, "Pedido: $status")
                    remoteViews.setProgressBar(R.id.progress_bar, 100, currentProgress, false)
                    notificationManager.notify(notificationId, builder.build())
                    currentProgress++
                    handler.postDelayed(this, 50L)
                } else {
                    if (targetProgress == 100) {
                        builder.setOngoing(false).setAutoCancel(true)
                        notificationManager.notify(notificationId, builder.build())
                    }
                }
            }
        }
        handler.post(updateRunnable)
        Log.d(TAG, "Tentando exibir notificação de status...")
    }
}
