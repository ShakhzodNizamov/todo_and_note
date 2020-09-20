package uz.jagito.todoandnote.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import uz.jagito.todoandnote.data.local.room.entities.TodoData

@Dao
interface TodoDao: BaseDao<TodoData>{
    @Query("SELECT * FROM todo WHERE deleted = 0")
    fun getAllNotDeleted(): List<TodoData>

    @Query("SELECT * FROM todo WHERE deleted = 0 AND cancelled = 0 AND done = 0")
    fun getAllActiveTodo(): List<TodoData>

    //selectAll from Basket
    @Query("SELECT * FROM todo WHERE deleted = 1")
    fun getTrashList(): List<TodoData>

    //selectAll from History
    @Query("SELECT * FROM todo")
    fun getHistoryData(): List<TodoData>

    //deleteAll from Trash
    @Query("DELETE FROM todo WHERE deleted = 1")
    fun deleteAllTrashData()

    @Query("SELECT * FROM todo WHERE id =:id")
    fun getTodoData(id: Long): List<TodoData>

    @Query("DELETE FROM todo WHERE labelID =:id")
    fun deleteTodoByLabelId(id: Long)

    @Query("SELECT * FROM todo WHERE labelID =:id AND deleted = 0 AND cancelled = 0 AND done = 0")
    fun getTodoByLabelId(id: Long): List<TodoData>

    @Query("SELECT * FROM todo WHERE deadline >= :t1 AND deadline <= :t2 AND deleted = 0 AND cancelled = 0 AND done = 0")
    fun getTodoByRangeTime(t1: Long, t2: Long):List<TodoData>

    @Query("SELECT * FROM todo WHERE cancelled = 1 AND deleted = 0")
    fun getCancelled():List<TodoData>

    @Query("SELECT * FROM todo WHERE done = 1 AND deleted = 0")
    fun getDone():List<TodoData>

    @Query("SELECT * FROM todo WHERE deadline <= :t1 AND deleted = 0 AND cancelled = 0 AND done = 0")
    fun getTodoBeforeTime(t1: Long):List<TodoData>

    @Query("SELECT * FROM todo WHERE deadline > :t1 AND deleted = 0 AND cancelled = 0 AND done = 0")
    fun getTodoAfterTime(t1: Long):List<TodoData>
}