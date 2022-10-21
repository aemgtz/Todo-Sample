package com.aemgtz.todo.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    var id: Int? = null,
    @ColumnInfo(name = "taskIdentifier")
    var taskId: String? = null,
    @ColumnInfo(name = "title")
    var title: String? = "",
    @ColumnInfo(name = "detail")
    var detail: String? = "",
    @ColumnInfo(name = "isCompleted")
    var isCompleted: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(taskId)
        parcel.writeString(title)
        parcel.writeString(detail)
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Task(id=$id, taskId=$taskId, title=$title, detail=$detail, isCompleted=$isCompleted)"
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
