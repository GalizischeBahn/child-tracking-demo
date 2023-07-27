package com.example.child_tracking.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.delay
import java.io.IOException
import java.time.LocalDateTime


class AudioRecorderClient(private val context: Context): AudioRecorderInterface {

    private var recorder: MediaRecorder? = null




    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun recordSurrounding(path: String): String{
        val pathToRecord = "${path}${LocalDateTime.now()}.mp4"
        recorder = MediaRecorder().apply {
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
    return pathToRecord
    }



}