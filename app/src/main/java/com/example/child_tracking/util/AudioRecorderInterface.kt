package com.example.child_tracking.util

interface AudioRecorderInterface {

   suspend fun recordSurrounding(childname: String): String

    class AudioRecordException(exceptionMessage: String) : Exception(exceptionMessage)

}