package com.neoapps.skijahorina.features.skicenter.properties


interface PropertyRepositoryFirebase {

    fun getAllProperties(callback: (List<Property>) -> Unit)
}