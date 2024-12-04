package com.example.smishingdetectionapp.forum.model

import android.os.Parcel
import android.os.Parcelable

class ForumTopic(val title: String, val description: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(description)
    }

    companion object CREATOR : Parcelable.Creator<ForumTopic> {
        override fun createFromParcel(parcel: Parcel): ForumTopic {
            return ForumTopic(parcel)
        }

        override fun newArray(size: Int): Array<ForumTopic?> {
            return arrayOfNulls(size)
        }
    }
}
