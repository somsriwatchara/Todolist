package com.watchara.todolist.activity

import android.os.Parcel
import android.os.Parcelable

data class DataModel(
    val id: String,
    val title: String,
    val notes: String,
    val timeCreate: String,
    val timeUpdate: String,
    val lat: String,
    val lng: String
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    )


    constructor() : this("", "", "", "", "", "", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(notes)
        parcel.writeString(timeCreate)
        parcel.writeString(timeUpdate)
        parcel.writeString(lat)
        parcel.writeString(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataModel> {
        override fun createFromParcel(parcel: Parcel): DataModel {
            return DataModel(parcel)
        }

        override fun newArray(size: Int): Array<DataModel?> {
            return arrayOfNulls(size)
        }
    }
}