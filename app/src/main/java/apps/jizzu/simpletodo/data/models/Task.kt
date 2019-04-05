package apps.jizzu.simpletodo.data.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "tasks_table")
class Task : Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @NonNull
    var id: Long = 0

    @ColumnInfo(name = "task_title")
    var title: String = ""

    @ColumnInfo(name = "task_note")
    var note: String = ""

    @ColumnInfo(name = "task_date")
    @NonNull
    var date: Long = 0

    @ColumnInfo(name = "task_position")
    @NonNull
    var position: Int = 0

    @ColumnInfo(name = "task_time_stamp")
    @NonNull
    var timeStamp: Long = 0

    @Ignore
    constructor() {
        this.timeStamp = Date().time
    }

    constructor(id: Long, title: String, note: String, date: Long, position: Int, timeStamp: Long) {
        this.id = id
        this.title = title
        this.note = note
        this.date = date
        this.position = position
        this.timeStamp = timeStamp
    }
}
