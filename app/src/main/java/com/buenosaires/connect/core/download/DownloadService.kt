package com.buenosaires.connect.core.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.buenosaires.connect.R

class DownloadService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val imageUrl = intent?.getStringExtra("image_url")
        if (imageUrl != null) {
            coroutineScope.launch {
                downloadImage(imageUrl)
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun downloadImage(imageUrl: String) {
        val imageLoader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .build()
        val result = imageLoader.execute(request)

        if (result.drawable is BitmapDrawable) {
            val bitmap = (result.drawable as BitmapDrawable).bitmap
            saveImage(bitmap)
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "moment_${System.currentTimeMillis()}.jpg")
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            showDownloadCompleteNotification(file.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDownloadCompleteNotification(fileName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "download_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.download_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.download_complete))
            .setContentText(getString(R.string.download_complete_text, fileName))
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
