package com.example.todolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {


    fun getAllTasks(taskName :  String, sortBy: SortBy, hideComplete: Boolean): Flow<List<Task>> =
        when(sortBy){
            SortBy.Name -> getAllTasksSortedByName(taskName,hideComplete)
            SortBy.Date -> getAllTasksSortedByDate(taskName,hideComplete)
        }


    @Query("SELECT * FROM tasks_table WHERE(completed != :hideComplete OR completed =0) AND name LIKE '%' || :taskName ||'%' ORDER BY important DESC , name")
    fun getAllTasksSortedByName(taskName :  String , hideComplete:Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE(completed != :hideComplete OR completed =0) AND name LIKE '%' || :taskName ||'%' ORDER BY important DESC , date")
    fun getAllTasksSortedByDate(taskName :  String , hideComplete:Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task:Task)

    @Update
    suspend fun update(task:Task)

    @Delete
    suspend fun delete(task:Task)

    @Query("DELETE  FROM tasks_table WHERE completed = 1 ")
    suspend fun deleteCompleted()
}
