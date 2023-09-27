package com.example.todolist.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.time.format.DateTimeFormatter

@Entity(tableName = "tasks_table")
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val date: Long = System.currentTimeMillis(),
    val completed: Boolean=false,
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0
) : Parcelable {

    val creationTime:String
    get() = DateFormat.getTimeInstance().format(date)

}
