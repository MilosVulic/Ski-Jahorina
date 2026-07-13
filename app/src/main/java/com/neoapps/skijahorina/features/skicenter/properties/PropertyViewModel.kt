package com.neoapps.skijahorina.features.skicenter.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PropertyViewModel : ViewModel() {

    private val propertyRepository = PropertyRepository()

    private val _properties = MutableLiveData<List<Property>>()
    val properties: LiveData<List<Property>> = _properties

    fun loadProperties() {
        if (_properties.value != null) return
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                propertyRepository.getAllPropertiesSuspend()
            }
            _properties.value = result
        }
    }
}
