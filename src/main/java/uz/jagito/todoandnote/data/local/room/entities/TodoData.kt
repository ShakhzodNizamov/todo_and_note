package uz.jagito.todoandnote.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo")
data class TodoData(

    var title: String = "",
    var date: Long = 0,
    var deadline: Long = 0,
    var hashTag: String = "",
    var description: String = "",
    var priority: Byte,
    var deleted: Boolean = false,
    var done: Boolean = false,
    var cancelled: Boolean = false,
    var outOfTime: Boolean = false,
    var labelID: Long = -1,
    var labelName: String = "",
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
): Serializable