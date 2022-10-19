/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aemgtz.todo.data.source.remote

import android.os.Handler
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.google.common.collect.Lists

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<Int, Task>(2)

    init {
        addTask(1, "Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask(2, "Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private fun addTask(id: Int, title: String, description: String) {
        val newTask = Task(id, title, description, false)
        TASKS_SERVICE_DATA[id] = newTask
    }

    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        // Simulate network by delaying the execution.
        val tasks = Lists.newArrayList(TASKS_SERVICE_DATA.values)
        Handler().postDelayed({
            callback.onTasksLoaded(tasks)
        }, SERVICE_LATENCY_IN_MILLIS)
    }

    /**
     * Note: [GetTaskCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getTask(taskId: Int, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskId]

        // Simulate network by delaying the execution.
        with(Handler()) {
            if (task != null) {
                postDelayed({ callback.onTaskLoaded(task) }, SERVICE_LATENCY_IN_MILLIS)
            } else {
                postDelayed({ callback.onDataNotAvailable() }, SERVICE_LATENCY_IN_MILLIS)
            }
        }
    }

    override fun saveTask(task: Task) {
        task.id?.let { TASKS_SERVICE_DATA.put(it, task) }
    }

    override fun saveTask(task: Task, callback: TasksDataSource.GetTaskCallback) {

    }

    override fun completeTask(task: Task) {
        val completedTask = Task(task.id, task.title, task.detail, true).apply {
            isCompleted = true
        }
        task.id?.let { TASKS_SERVICE_DATA.put(it, completedTask) }
    }

    override fun completeTask(taskId: Int) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        val activeTask = Task(task.id, task.title, task.detail, task.isCompleted)
        task.id?.let { TASKS_SERVICE_DATA.put(it, activeTask) }
    }

    override fun activateTask(taskId: Int) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues { !it.isCompleted
        } as LinkedHashMap<Int, Task>
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: Int) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}
