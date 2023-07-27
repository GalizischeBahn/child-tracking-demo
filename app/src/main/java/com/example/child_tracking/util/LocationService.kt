package com.example.child_tracking.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.service.voice.VoiceInteractionService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.child_tracking.ApplicationMain
import com.example.child_tracking.R
import com.example.child_tracking.data.FirebaseRepository
import com.example.child_tracking.data.UsersDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.tasks.await
import java.awt.font.TextAttribute.FOREGROUND
import java.io.File
import java.io.IOException
import java.time.LocalDateTime


class LocationService() : VoiceInteractionService() {
    var longtitude = 0.0
    var latitude = 0.0

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainThreadScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var locationClient: DefaultLocationClient
    @RequiresApi(Build.VERSION_CODES.S)
    private var recorder: MediaRecorder? = null
   // private lateinit var audioRecorderClient: AudioRecorderClient
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var usersDataStore: UsersDataStore
    private lateinit var parentEmail: String
    private lateinit var childName: String
    @Volatile
    var isOnline: Boolean? = null
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRefForRecord: DatabaseReference
    private var isUserPrefReady = false
    lateinit var locationProvider: FusedLocationProviderClient
    private var recordingJob: Job? = null
    private var userDataJob: Job? = null
    private var finalPath: String? = null
    private var powerManager: PowerManager? = null
    private var wakeLock : WakeLock? = null







    override fun onCreate() {
        super.onCreate()

        firebaseRepository = (application as ApplicationMain).firebaseRepo
        locationProvider = FusedLocationProviderClient(applicationContext)
     //   audioRecorderClient = AudioRecorderClient(applicationContext)
        locationClient = DefaultLocationClient(locationProvider, applicationContext)
        usersDataStore = UsersDataStore(applicationContext)



    }
    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(applicationContext)
        }
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager!!.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "child-tracking: recorder lock")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "location",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Hey kiddo")
            .setContentText("App is worrking")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(false)
            .setChannelId("location")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        startForeground(1, notification.build())

        val isOnlinelistener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var job: Job? = null

                 isOnline = snapshot.getValue(Boolean::class.java)!!
                if (isOnline as Boolean) {
                    job = coroutineScope.launch {
                        if (!isOnline!!) job!!.cancel()
                        locationClient
                            .getLocation(500)
                            .takeIf { isOnline!! }
                            ?.collect{ if (isOnline as Boolean){
                                longtitude = it.longitude
                                latitude = it.latitude
                                firebaseRepository.updateLocation(
                                    latitude,
                                    longtitude,
                                    locationLastTimeupdated = LocalDateTime.now().toString()

                                )}else
                                    job?.cancel()
                                    return@collect}}

                
                }
                else {
                    if (job != null) {
                        job.cancel()
                    Log.d("wait", "wait")}}}


            override fun onCancelled(error: DatabaseError) {
                throw Exception("cancelled")
            }}

        val isFlagToRecordOnListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val isTrue = snapshot.getValue(Boolean::class.java)
                if (isTrue!!) {
                    coroutineScope.launch{
                        recordSurrounding()
                        recordingJob?.join()
                        uploadToStorage()

                    }}}


            override fun onCancelled(error: DatabaseError) {
                throw Exception("cancelled")
            }}

        userDataJob = coroutineScope.launch {
            usersDataStore.childNamePreferenceFlow.collect { it ->
                usersDataStore.parentEmailPreferenceFlow.collect { email ->
                    parentEmail = email!!
                    databaseRef = firebaseRepository.databaseTable
                        .child(email!!)
                        .child("isOnline")
                    childName = it!!

                    makeRecordRef()
                    databaseRef.addValueEventListener(isOnlinelistener)
                    databaseRefForRecord.addValueEventListener(isFlagToRecordOnListener)
                }}}
        return super.onStartCommand(intent, flags, startId)}

    fun makeRecordRef() {
        databaseRefForRecord = firebaseRepository.databaseTable
            .child(parentEmail)
            .child("childList")
            .child(childName)
            .child("flagToRecord")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recordSurrounding() {

            val pathToCacheStorageWithChildName = "${applicationContext.cacheDir.absolutePath}/${childName}"
           recordingJob = coroutineScope.launch {
           finalPath = recordByMediaRecorder(pathToCacheStorageWithChildName)


           }

        }

    fun uploadToStorage() {

        coroutineScope.launch {
        val file = Uri.fromFile(File(finalPath))
        val storageReference = firebaseRepository.storageReference.child(parentEmail).child(childName).child(file.lastPathSegment!!)
        val uploadTask = storageReference.putFile(file).await()


                val downloadRef = storageReference.downloadUrl.await().toString()
                firebaseRepository.updateRefAndFlags(downloadRef,true, false)

            }

        }

    @RequiresApi(Build.VERSION_CODES.O)
     suspend fun recordByMediaRecorder(path: String): String{
        val pathToRecord = "${path}${LocalDateTime.now()}.mp4"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder?.apply {
                setAudioSource(MediaRecorder.getAudioSourceMax())
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(pathToRecord)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                try {
                    prepare()
                } catch (e: IOException) {
                    println("Prepare has failed:  ${e.printStackTrace()}")
                }
                start()
                delay(5000)
                stop()
                release()
            }
        }
        return pathToRecord
    }





    override fun onBind(intent: Intent?): IBinder? {
        Log.d("service binder", "service is bindig")
        return null
    }
    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.release()
        coroutineScope.cancel()
    }


}