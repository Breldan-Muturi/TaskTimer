package turi.Kotlin.tasktimer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Task(var id: Long, val name: String, val description: String, val sortOrder: Int): Parcelable {
}