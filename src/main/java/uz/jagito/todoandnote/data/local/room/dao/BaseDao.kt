package uz.jagito.todoandnote.data.local.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Update
    fun update(data: T): Int

    @Update
    fun updateAll(data: List<T>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: T): Long

    @Insert
    fun insertAll(data: List<T>): List<Long>

    @Delete
    fun delete(data: T): Int

    @Delete
    fun deleteAll(data: List<T>): Int
}