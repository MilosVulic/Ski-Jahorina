package com.neoapps.skijahorina.features.skicenter.properties

import com.google.firebase.firestore.FirebaseFirestore

class PropertyRepository : PropertyRepositoryFirebase {
    private val db = FirebaseFirestore.getInstance()
    private val propertiesCollection = db.collection("properties")

    override fun getAllProperties(callback: (List<Property>) -> Unit) {
        propertiesCollection.get()
            .addOnSuccessListener { documents ->
                val properties = mutableListOf<Property>()
                for (document in documents) {
                    val property = document.toObject(Property::class.java)
                    if (property.propertyStatus == PropertyStatus.PUBLISHED && property.uuid == "1") {
                        properties.add(property)
                    }
                }
                callback(properties)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
