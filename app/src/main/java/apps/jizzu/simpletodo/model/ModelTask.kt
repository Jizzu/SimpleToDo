package apps.jizzu.simpletodo.model

import java.io.Serializable
import java.util.Date

/**
 * Class for representing a specific task.
 */
class ModelTask : Serializable {

    var id: Long = 0
    var title: String? = null
    var date: Long = 0
    var position: Int = 0
    var timeStamp: Long = 0

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
