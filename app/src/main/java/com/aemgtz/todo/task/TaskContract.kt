package com.aemgtz.todo.task

import com.aemgtz.todo.data.Task
import com.aemgtz.todo.utils.BasePresenter
import com.aemgtz.todo.utils.BaseView


interface TaskContract {

    interface View : BaseView<Presenter?> {
        fun onTaskUpdate()
    }

    interface Presenter : BasePresenter {
        fun fetchTasks()
        fun addTask(task: Task)
        fun updateTask(task: Task)
        fun delete(task: Task)
    }
}