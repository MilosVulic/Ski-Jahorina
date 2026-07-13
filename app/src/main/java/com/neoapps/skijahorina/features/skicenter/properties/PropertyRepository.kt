package com.neoapps.skijahorina.features.skicenter.properties

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PropertyRepository : PropertyRepositoryFirebase {
    private val db = FirebaseFirestore.getInstance()
    private val propertiesCollection = db.collection("properties")

    override fun getAllProperties(callback: (List<Property>) -> Unit) {
        propertiesCollection.get()
            .addOnSuccessListener { documents ->
                callback(parseProperties(documents))
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    suspend fun getAllPropertiesSuspend(): List<Property> = suspendCoroutine { continuation ->
        propertiesCollection.get()
            .addOnSuccessListener { documents ->
                continuation.resume(parseProperties(documents))
            }
            .addOnFailureListener {
                continuation.resume(emptyList())
            }
    }

    private fun parseProperties(documents: com.google.firebase.firestore.QuerySnapshot): List<Property> {
        val properties = mutableListOf<Property>()
        for (document in documents) {
            val property = document.toObject(Property::class.java)
            if (property.propertyStatus == PropertyStatus.PUBLISHED && property.uuid == "1") {
                properties.add(property)
            }
        }
        return properties
    }
}
