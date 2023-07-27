package com.example.child_tracking.ui

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.child_tracking.data.FirebaseRepository
import com.example.child_tracking.ApplicationMain
import com.example.child_tracking.data.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChildTrackingViewModel(private val firebaseRepository: FirebaseRepository): ViewModel() {

    lateinit var email: String
    lateinit var listlivedata: LiveData<MutableList<Child>>
    lateinit var childLiveData: LiveData<Child>


    fun addParentEmailToDatabase(email: String?) {
        viewModelScope.launch {
            if (email != null) {
                firebaseRepository.addParentEmailToTree(email)
            }
        }
    }
    fun makeParentOnline(email: String?) {
        firebaseRepository.parentIsOnline(email)
    }
    fun makeParentOffline(email: String?) {
        firebaseRepository.parentIsOffline(email)
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
           // @Suppress("UNCHECKED_CAST")
            @SuppressLint("SuspiciousIndentation")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
              val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
              //  val savedStateHandle = extras.createSavedStateHandle()




                return ChildTrackingViewModel(

                  (application as ApplicationMain).firebaseRepo,
                  //  savedStateHandle
                ) as T
            }
        }
}

    fun registerNewChild(email: String, childName: String) {
        firebaseRepository.registerNewChild(email, childName)
    }
    fun getAllChildren(email: String) {
viewModelScope.launch {

    firebaseRepository.getAllChildren(email)
    listlivedata = firebaseRepository.getAllChildren(email).asLiveData()}
}
   fun childrenListForEmail(): MutableList<Child> { return firebaseRepository.childList
    }
    fun setCurrentChild(child: Child) {
        firebaseRepository.currentChild = child
    }

    fun startListeningChanges() {
        viewModelScope.launch {
        childLiveData = firebaseRepository.startListeningChanges().asLiveData()

    }

    }
    fun sendRecordSignal() {
        firebaseRepository.sendRecordSignal()
    }
}