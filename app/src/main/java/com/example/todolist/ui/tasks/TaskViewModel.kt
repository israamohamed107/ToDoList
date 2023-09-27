package com.example.todolist.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.todolist.data.MyDao
import com.example.todolist.data.PreferenceManager
import com.example.todolist.data.SortBy
import com.example.todolist.data.Task
import com.example.todolist.utils.ADD_NEW_TASK_FLAG
import com.example.todolist.utils.EDIT_TASK_FLAG

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel  @ViewModelInject constructor(
    private val myDao: MyDao,
    private val preferenceManager:PreferenceManager,
) : ViewModel() {

    private val _taskEventChannel = Channel<TasksEvent>()
    val taskEventChannel = _taskEventChannel.receiveAsFlow()

    val preferenceFlow = preferenceManager.preferenceFlow

    val searchQuery = MutableStateFlow("")

    private val taskFlow =
        combine(searchQuery,preferenceFlow) { query, filterPrefernce ->
            Pair(
                query,
                filterPrefernce
            )
        }.flatMapLatest {(query,filterPreference) ->
            myDao.getAllTasks(query,filterPreference.sortBy , filterPreference.hideCompleted)
        }

    val tasks = taskFlow.asLiveData()


    fun sortOrderSelected(sortBy: SortBy) {
        viewModelScope.launch{
            preferenceManager.setSortOrder(sortBy)
        }
    }

    fun hideCompletedSelected(hideCompleted : Boolean){
        viewModelScope.launch {
            preferenceManager.setHideCompleted(hideCompleted)
        }
    }

    fun onTaskClicked(task: Task){
        viewModelScope.launch {
            _taskEventChannel.send(TasksEvent.NavigateToEditScreen(task))
        }
    }

    fun onTaskChecked(task: Task , isClicked:Boolean){
        viewModelScope.launch {
            myDao.update(task.copy(completed = isClicked) )
        }
    }

    fun onItemSwiped(task: Task){
        viewModelScope.launch{
            myDao.delete(task)
            _taskEventChannel.send(TasksEvent.ShowDeleteTaskMessage(task))
        }
    }

    fun onUndoClicked(task: Task){
        viewModelScope.launch {
            myDao.insert(task)
        }
    }

    fun onAddButtonClicked() {
       viewModelScope.launch {
           _taskEventChannel.send(TasksEvent.NavigateToAddScreen)
       }
    }

    fun onAddEditClicked(flag:Int){
       when(flag){
           ADD_NEW_TASK_FLAG -> showTaskSavedConfirmationMessage("Task added")
           EDIT_TASK_FLAG -> showTaskSavedConfirmationMessage("Task updated")
       }
    }

    private fun showTaskSavedConfirmationMessage(message: String) {
        viewModelScope.launch {
            _taskEventChannel.send(TasksEvent.AddEditConfirmationMessage(message))
        }
    }

    fun deleteCompleted(){
        viewModelScope.launch {
            myDao.deleteCompleted()
        }
    }

    fun showDeleteConfirmationMessage(){
        viewModelScope.launch {
            _taskEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedFragment)
        }
    }
    sealed class TasksEvent{
        object NavigateToAddScreen : TasksEvent()
        data class NavigateToEditScreen(val task: Task) : TasksEvent()
        data class ShowDeleteTaskMessage(val task: Task) : TasksEvent()
        data class AddEditConfirmationMessage(val message: String) : TasksEvent()
        object NavigateToDeleteAllCompletedFragment : TasksEvent()

    }

}

