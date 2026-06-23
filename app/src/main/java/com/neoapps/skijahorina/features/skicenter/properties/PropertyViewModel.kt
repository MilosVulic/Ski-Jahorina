package com.neoapps.skijahorina.features.skicenter.properties

import androidx.lifecycle.ViewModel

class PropertyViewModel : ViewModel() {
    private val propertyRepository = PropertyRepository()

    fun getAllProperties( callback: (List<Property>) -> Unit) {
        propertyRepository.getAllProperties(callback)
    }
}