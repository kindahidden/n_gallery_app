package com.elfen.ngallery.services


import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.util.fastAny
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.net.toUri
import com.elfen.ngallery.MainActivity
import com.elfen.ngallery.R
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.net.URL
import java.time.Duration
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    @Inject
    lateinit var galleryRepository: GalleryRepository

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private var lastNotificationUpdate = 0L

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val id = msg.arg2

            try {
                runBlocking {
                    Log.d("DownloadService", "handleMessage: request")
                    val isSaved = galleryRepository.getSavedGalleriesFlow()
                        .first()
                        .fastAny { it.id == id && it.state == DownloadState.Done }
                    if (isSaved)
                        return@runBlocking

                    val response = galleryRepository.fetchGalleryById(id)
                    if (response is Resource.Error) {
                        throw Exception(response.message)
                    }


                    Log.d("DownloadService", "handleMessage: gallery successfully loaded")
                    val gallery = response.data!!
                    val totalToBeDownloaded = gallery.pages.size + 1

                    val updateProgress: (suspend (progress: Int) -> Unit) = { progress ->
                        Log.d("DownloadService", "handleMessage: progress update $progress")
                        val state = DownloadState.Downloading(
                            progress = progress,
                            total = totalToBeDownloaded
                        )
                        galleryRepository.updateDownloadState(id, downloadState = state)
                        updateNotification(gallery, state)
                    }

                    updateProgress(0)

                    val coverDir = File(applicationContext.dataDir, "cover")
                    coverDir.mkdirs()
                    val coverExtension = gallery.cover.url.split(".").last()
                    val coverFile = File(coverDir, "$id.$coverExtension")

                    // Download Thumbnail
                    URL(gallery.cover.url).openStream().use { input ->
                        coverFile.outputStream().use {
                            input.copyTo(it)
                        }
                    }

                    updateProgress(1)
                    Log.d("DownloadService", "handleMessage: Cover Downloaded")

                    // Download Pages
                    val pagesDir = File(applicationContext.dataDir, "galleries/$id")
                    pagesDir.mkdirs()

                    gallery.pages.forEach {
                        val extension = it.hdUrl.split(".").last()
                        val pageFile =
                            File(pagesDir, "${it.page}.$extension")
                        URL(it.hdUrl).openStream().use { input ->
                            pageFile.outputStream().use {
                                input.copyTo(it)
                            }
                        }

                        updateProgress(it.page + 1)
                    }

                    galleryRepository.updateDownloadState(id, DownloadState.Done)
                    notifyDownloadDone(gallery)
                    Log.d("DownloadService", "handleMessage: Gallery $id Downloaded")
                }
            } catch (e: InterruptedException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        applicationContext,
                        "Download failed for #$id: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                notifyFail(id, e.message ?: "Unknown Error Occurred")

                Thread.currentThread().interrupt()
            }

            stopSelf(msg.arg1)
        }
    }

    private fun getGalleryDeepLinkIntent(id: Int): PendingIntent? {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "ngallery://read/$id".toUri(),
            this,
            MainActivity::class.java
        )

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }

        return deepLinkPendingIntent
    }

    private fun notifyDownloadDone(gallery: Gallery) {
                val notification = NotificationCompat
            .Builder(this, "DOWNLOAD_CHANNEL")
            .setContentTitle(gallery.title)
            .setContentText("Gallery Downloaded")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSilent(true)
            .setContentIntent(getGalleryDeepLinkIntent(gallery.id))
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(gallery.id, notification)
    }


    private fun startForeground() {
        val notification = NotificationCompat
            .Builder(applicationContext, "DOWNLOAD_CHANNEL")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentTitle("Downloading...")
            .setContentText("Preparing Downloads")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSilent(true)
            .setOngoing(true)
            .build()

        Log.d("DownloadService", "startForeground")
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ 100, // Cannot be 0
            /* notification = */ notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    private fun notifyFail(id: Int, message: String){
        val notification = NotificationCompat
            .Builder(this, "DOWNLOAD_CHANNEL")
            .setContentTitle("Download #$id failed")
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_error_24)
            .setSilent(true)
            .setContentIntent(getGalleryDeepLinkIntent(id))
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }

    private fun updateNotification(gallery: Gallery, downloadState: DownloadState) {
        val throttleDuration = Duration.ofSeconds(1)
        if((Clock.System.now().toEpochMilliseconds() - lastNotificationUpdate) < throttleDuration.toMillis()){
            return
        }

        lastNotificationUpdate = Clock.System.now().toEpochMilliseconds()
        val notification = NotificationCompat
            .Builder(this, "DOWNLOAD_CHANNEL")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentTitle(gallery.title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(getGalleryDeepLinkIntent(gallery.id))
            .setContentText(
                when (downloadState) {
                    is DownloadState.Pending -> "Pending"
                    is DownloadState.Done -> "Done"
                    is DownloadState.Downloading -> "Downloading...(${downloadState.progress}/${downloadState.total})"
                    else -> ""
                }
            )
            .setOngoing(true)
            .setSilent(true)
            .apply {
                if (downloadState is DownloadState.Downloading) {
                    setProgress(
                        /* max = */ downloadState.total,
                        /* progress = */ downloadState.progress,
                        /* indeterminate = */ false
                    )
                }
            }
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(gallery.id)
        notificationManager.notify(100, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Log.d("DownloadService", "onStartCommand")
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            msg.arg2 = intent.getIntExtra("id", 0)
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }
}