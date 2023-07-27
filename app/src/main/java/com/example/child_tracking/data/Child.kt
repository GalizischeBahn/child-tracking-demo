package com.example.child_tracking.data

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Child(var childName: String,
                 var latitude: Double? = null,
                 var longtitude: Double? = null,
                 var locationLastTimeUpdated: String? = null,
                 var parentEmail: String,
                 var flagToRecord: Boolean?,
                 var doneWithUploading: Boolean,
                 var referenceToAudioRecord: String?
) : Serializable {

    constructor() : this("", null, null, null, "", null,  false, null)


}