package uz.jagito.todoandnote.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "label")
class LabelData (
    var label: String = "",
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
): Serializable