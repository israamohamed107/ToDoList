package com.example.todolist.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.MyDao
import com.example.todolist.data.Task
import com.example.todolist.utils.ADD_NEW_TASK_FLAG
import com.example.todolist.utils.EDIT_TASK_FLAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditTaskViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val myDao: MyDao
) : ViewModel() {

    private val _addEditTaskChannel = Channel<AddEditTaskEvent>()
    val addEditTaskChannel = _addEditTaskChannel.receiveAsFlow()

    val task = savedStateHandle.get<Task>("task")

    var taskName = savedStateHandle.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            savedStateHandle["taskName"] = value
        }
    var taskImportance = savedStateHandle.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            savedStateHandle["taskImportance"] = value
        }

    fun onSaveBtnCliched(){
        if (taskName.isBlank()){
            // show invalid input message
            showInvalidInputMessage("Name can\'t be empty!")
            return
        }

        if (task != null) {
            // update current task
            // navigate back
            val updatedTask = task.copy(name = taskName , important = taskImportance)
            updateTask(updatedTask)
        }else{
            // create new task
            // navigate back
            val newTask = Task(name = taskName , important = taskImportance)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage(message: String) {

        viewModelScope.launch {
            _addEditTaskChannel.send(AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidMessage(message))
        }
    }

    private fun createTask(task: Task) {
        viewModelScope.launch {
            myDao.insert(task)
            _addEditTaskChannel.send(AddEditTaskViewModel.AddEditTaskEvent.NavigateBack(
                ADD_NEW_TASK_FLAG))
        }
    }

    fun updateTask(task: Task){
        viewModelScope.launch{
            myDao.update(task)
            _addEditTaskChannel.send(AddEditTaskViewModel.AddEditTaskEvent.NavigateBack(
                EDIT_TASK_FLAG))
        }
    }

    sealed class AddEditTaskEvent{
        data class ShowInvalidMessage(val message: String):AddEditTaskEvent()
        data class NavigateBack(val flag:Int) :AddEditTaskEvent()
    }

}