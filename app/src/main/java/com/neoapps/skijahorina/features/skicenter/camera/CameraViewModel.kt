package com.neoapps.skijahorina.features.skicenter.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.neoapps.skijahorina.common.AppAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CameraViewModel : ViewModel() {

    private val _cameraDataList = MutableLiveData<List<Camera>>()
    val cameraDataList: LiveData<List<Camera>> get() = _cameraDataList

    private var loaded = false

    fun fetchCameraDataFromFirestore() {
        if (loaded && _cameraDataList.value != null) return
        viewModelScope.launch {
            val cameraList = withContext(Dispatchers.IO) {
                runCatching {
                    FirebaseFirestore.getInstance()
                        .collection("cameras-jahorina")
                        .get()
                        .await()
                        .documents
                        .map { doc ->
                            Camera(
                                name = doc.getString("name") ?: "",
                                url = doc.getString("url") ?: ""
                            )
                        }
                }.getOrElse { error ->
                    AppAnalytics.recordException(
                        error,
                        "Jahorina camera Firestore load failed",
                        mapOf("source" to "cameras_firestore")
                    )
                    AppAnalytics.logDataFetchFailed("cameras_firestore")
                    emptyList()
                }
            }
            loaded = true
            _cameraDataList.value = cameraList
        }
    }
}
