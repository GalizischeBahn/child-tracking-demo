package com.example.child_tracking.data

import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class FirebaseRepository {

    val storage = Firebase.storage

    companion object {
        private var DATABASE_INSTANCE: FirebaseDatabase? = null

        fun getFirebaseDatabaseInstance(): FirebaseDatabase = DATABASE_INSTANCE ?: synchronized(this)
        { DATABASE_INSTANCE ?: FirebaseDatabase
            .getInstance("https://child-control-581cf-default-rtdb.firebaseio.com/")
            .also { DATABASE_INSTANCE = it }


    }}


    private val database = getFirebaseDatabaseInstance()
    val databaseTable = database.getReference("users")
    val storageReference = storage.reference


     lateinit var currentChild: Child
     var childList = mutableListOf<Child>()
    val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

     fun registerNewChild(email: String, childName: String, latitude: Double? = null, longtitude: Double? = null,
                                 locationLastTimeupdated: String? = null) {

        currentChild = Child(childName, latitude, longtitude, locationLastTimeupdated, email, false, false, null)

        databaseTable.child(email).child("childList").child(currentChild.childName).setValue(currentChild)

    }
     fun updateLocation(latitude: Double?, longtitude: Double?, locationLastTimeupdated: String?) {

       // var updatedChild = currentChild.copy(latitude = latitude, longtitude = longtitude,
       //     locationLastTimeUpdated = locationLastTimeupdated)

        databaseTable.child(currentChild.parentEmail).child("childList").child(currentChild.childName).child("latitude").setValue(latitude)
         databaseTable.child(currentChild.parentEmail).child("childList").child(currentChild.childName).child("longtitude").setValue(longtitude)
    }

     fun parentIsOnline(email: String?) {
        //databaseTable.child(child.parentEmail).child("isOnline").setValue(true)
        databaseTable.child(email!!).child("isOnline").setValue(true)


     }

     fun parentIsOffline(email: String?) {
       // databaseTable.child(child.parentEmail).child("isOnline").setValue(true)
         databaseTable.child(email!!).child("isOnline").setValue(false)

    }

     fun addParentEmailToTree(email: String) {
        databaseTable.child(email)
    }

   // private fun postChildrenListCallBack(list: MutableList<Child>) {
  //      childList = list
  //  }

    @SuppressLint("SuspiciousIndentation")
    fun getAllChildren(email: String): Flow<MutableList<Child>> = callbackFlow {
        val childListRef = databaseTable.child(email).child("childList")
        childListRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                val children = mutableListOf<Child>()
                for (childSnapshot in dataSnapshot.children) {
                    val child = childSnapshot.getValue(Child::class.java)
                        children.add(child!!)}
                        launch {
                            send(children)

                }

                }
        }

        awaitClose() // Await closure of the flow when it is no longer needed
    }

fun startListeningChanges(): Flow<Child> = callbackFlow{
    val databaseRef = databaseTable
                                    .child(currentChild.parentEmail)
                                    .child("childList")
                                    .child(currentChild.childName)

    val childEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val child = snapshot.getValue(Child::class.java)
            launch { send (child!!) }

        }

        override fun onCancelled(error: DatabaseError) {
            throw Exception("cancelled")
        }
    }
    databaseRef.addValueEventListener(childEventListener)

    awaitClose()}

    fun isParentOnline(email: String): Flow<Boolean> = callbackFlow {
        var ref = databaseTable.child(email!!).child("isOnline")


        val isOnlinelistener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isOnline = snapshot.getValue(Boolean::class.java)
                launch { send (isOnline!!) }

            }

            override fun onCancelled(error: DatabaseError) {
                throw Exception("cancelled")
            }
        }
        fun updateStorageRedAndFlags(ref: String, isUploaded: Boolean, isSignalToRecord: Boolean) {

        }
        ref.addValueEventListener(isOnlinelistener)




    awaitClose()}
    fun updateRefAndFlags(ref: String, isDone: Boolean, isFlagtoRecordOn: Boolean) {

        databaseTable.child(currentChild.parentEmail).child("childList")
            .child(currentChild.childName).child("referenceToAudioRecord").setValue(ref)

        databaseTable.child(currentChild.parentEmail).child("childList")
            .child(currentChild.childName).child("doneWithUploading").setValue(isDone)

        databaseTable.child(currentChild.parentEmail).child("childList")
            .child(currentChild.childName).child("flagToRecord").setValue(isFlagtoRecordOn)

    }

    fun sendRecordSignal() {
        databaseTable.child(currentChild.parentEmail)
            .child("childList")
            .child(currentChild.childName)
            .child("flagToRecord")
            .setValue(true)
    }

    fun flagToRecordToFalse() {
        databaseTable.child(currentChild.parentEmail)
            .child("childList")
            .child(currentChild.childName)
            .child("flagToRecord")
            .setValue(false)
    }

}


