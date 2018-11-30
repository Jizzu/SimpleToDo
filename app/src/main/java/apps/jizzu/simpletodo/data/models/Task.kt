package apps.jizzu.simpletodo.data.models

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
    var id: Long = 0

    @ColumnInfo(name = "task_title")
    var title: String? = null

    @ColumnInfo(name = "task_date")
    var date: Long = 0

    @ColumnInfo(name = "task_position")
    var position: Int = 0

    @ColumnInfo(name = "task_time_stamp")
    var timeStamp: Long = 0

    @Ignore
    constructor() {
        this.timeStamp = Date().time
    }

    constructor(id: Long, title: String, date: Long, position: Int, timeStamp: Long) {
        this.id = id
        this.title = title
        this.date = date
        this.position = position
        this.timeStamp = timeStamp
    }
}
