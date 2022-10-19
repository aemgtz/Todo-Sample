package com.aemgtz.todo.task

import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.aemgtz.todo.data.TasksRepository


class TaskPresenter(private val view: TaskContract.View?, private val tasksRepository: TasksRepository) : TaskContract.Presenter {

    init {
        view?.presenter = this
    }

    override fun fetchTasks() {
        view?.setLoadingIndicator(true)
        tasksRepository.getTasks(object: TasksDataSource.LoadTasksCallback{
            override fun onTasksLoaded(tasks: List<Task>) {
                view?.setLoadingIndicator(false)
                view?.onTaskLoaded(tasks)
            }

            override fun onDataNotAvailable() {
                view?.setLoadingIndicator(false)
            }
        })
    }

    override fun addTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun delete(task: Task) {
        TODO("Not yet implemented")
    }

}