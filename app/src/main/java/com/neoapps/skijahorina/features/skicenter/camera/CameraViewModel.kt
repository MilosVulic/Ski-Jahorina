package com.neoapps.skijahorina.features.skicenter.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.io.InputStream
import java.net.URL

class CameraViewModel : ViewModel() {

    private val _imageList = MutableLiveData<List<Bitmap>>()
    val imageList: LiveData<List<Bitmap>> get() = _imageList

    fun fetchImages(url: String) {
        viewModelScope.launch {
            try {
                val result = fetchData(url)
                _imageList.value = result
            } catch (e: Exception) {
                // Handle errors as needed
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchData(url: String): List<Bitmap> = withContext(Dispatchers.IO) {
        val uniqueImageUrls: MutableSet<String> = mutableSetOf()
        val imageList: MutableList<Bitmap> = mutableListOf()

        try {
            val document = Jsoup.connect(url).get()
            val imageElements = document.select("img.webcam-card__image")

            for (imageElement in imageElements) {
                val imageUrl = imageElement.attr("src")

                if (uniqueImageUrls.add(imageUrl)) {
                    val inputStream: InputStream = URL(imageUrl).openStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageList.add(bitmap)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        imageList
    }
}
