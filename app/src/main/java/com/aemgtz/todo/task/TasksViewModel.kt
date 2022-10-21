package com.aemgtz.todo.task



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.aemgtz.todo.data.TasksRepository

//https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
class TasksViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val tasks: LiveData<List<Task>>
        get() = _tasks

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun fetchTasks(forceUpdate: Boolean){
        if (forceUpdate){
            tasksRepository.refreshTasks()
        }
        _dataLoading.value = true
        tasksRepository.getTasks(object : TasksDataSource.LoadTasksCallback{
            override fun onTasksLoaded(tasks: List<Task>) {
                _tasks.value = tasks
                _dataLoading.value = false
            }

            override fun onDataNotAvailable() {
                _dataLoading.value = false
            }
        })
    }

    fun deleteTask(task: Task){
        task.taskId?.let {
            tasksRepository.deleteTask(it)
        }
    }
}