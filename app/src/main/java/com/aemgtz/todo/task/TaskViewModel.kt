package com.aemgtz.todo.task

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aemgtz.todo.data.Injection
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksRepository

class TaskViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val tasks: LiveData<List<Task>>
        get() = _tasks

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: TaskViewModel? = null
//        fun getInstance(application: Application) =
//            INSTANCE ?: synchronized(TaskViewModel::class.java) {
//                INSTANCE ?: TaskViewModel(
//                    Injection.provideTasksRepository(application.applicationContext))
//                    .also { INSTANCE = it }
//            }
    }
}