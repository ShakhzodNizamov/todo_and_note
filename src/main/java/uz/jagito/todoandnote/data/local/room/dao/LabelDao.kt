package uz.jagito.todoandnote.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import uz.jagito.todoandnote.data.local.room.entities.LabelData

@Dao
interface LabelDao: BaseDao<LabelData> {

    @Query("SELECT * FROM label WHERE id =:id")
    fun getLabelData(id: Long): LabelData

    @Query("SELECT * FROM label")
    fun getAllLabelsData(): List<LabelData>
}