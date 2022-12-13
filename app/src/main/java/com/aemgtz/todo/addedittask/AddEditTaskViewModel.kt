package com.aemgtz.todo.addedittask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.aemgtz.todo.data.TasksRepository

class AddEditTaskViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    private val _isTaskSaved = MutableLiveData<Boolean>()

    val isTaskSaved: LiveData<Boolean>
        get() =_isTaskSaved

    fun start(){
        _isTaskSaved.value = false
    }

    fun saveTask(task: Task){
        tasksRepository.saveTask(task, object : TasksDataSource.GetTaskCallback{
            override fun onTaskLoaded(task: Task) {
                _isTaskSaved.value = true
            }
            override fun onDataNotAvailable() {

            }
        })
    }

    fun deleteTask(task: Task){
        task.taskId?.let {
            tasksRepository.deleteTask(it, object : TasksDataSource.TaskCallback<Boolean>{
                override fun onTaskLoaded(result: Boolean) {
                    TODO("Not yet implemented")
                    _isTaskSaved.value = true
                }

                override fun onDataNotAvailable() {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}