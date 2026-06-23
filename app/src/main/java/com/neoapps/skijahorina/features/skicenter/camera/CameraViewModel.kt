package com.neoapps.skijahorina.features.skicenter.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CameraViewModel : ViewModel() {

    private val _cameraDataList = MutableLiveData<List<Camera>>()
    val cameraDataList: LiveData<List<Camera>> get() = _cameraDataList

    fun fetchCameraDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("cameras-jahorina")
            .get()
            .addOnSuccessListener { snapshot ->
                val cameraList = snapshot.documents.map { doc ->
                    Camera(
                        name = doc.getString("name") ?: "",
                        url = doc.getString("url") ?: ""
                    )
                }
                _cameraDataList.value = cameraList
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}