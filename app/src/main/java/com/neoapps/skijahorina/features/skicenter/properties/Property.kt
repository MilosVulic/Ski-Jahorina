package com.neoapps.skijahorina.features.skicenter.properties

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Property(
    var uuid: String = "",
    var website: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var name: String = "",
    var propertyStatus: PropertyStatus = PropertyStatus.IN_REVIEW,
    var propertyType: PropertyType = PropertyType.APARTMENT,
    var pictures: String = "",
    var description: String = "",
    var price: String = "",
    var location: String = "",
    var updatedAt: String = "",
    var deletedAt: String = ""
) : Parcelable {
    constructor() : this("", "", "","", "", PropertyStatus.IN_REVIEW, PropertyType.APARTMENT,  "", "","","", "")
}