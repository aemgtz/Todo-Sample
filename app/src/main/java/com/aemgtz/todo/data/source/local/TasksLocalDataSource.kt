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
package com.aemgtz.todo.data.source.local

import androidx.annotation.VisibleForTesting
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.aemgtz.todo.utils.AppExecutors


/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val tasksDao: TasksDao
) : TasksDataSource {

    /**
     * Note: [TasksDataSource.LoadTasksCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        appExecutors.diskIO.execute {
            val tasks = tasksDao.getTasks()
            appExecutors.mainThread.execute {
                if (tasks.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(tasks)
                }
            }
        }
    }

    /**
     * Note: [TasksDataSource.GetTaskCallback.onDataNotAvailable] is fired if the [Task] isn't
     * found.
     */
    override fun getTask(taskId: Int, callback: TasksDataSource.GetTaskCallback) {
        appExecutors.diskIO.execute {
            val task = tasksDao.getTaskById(taskId)
            appExecutors.mainThread.execute {
                if (task != null) {
                    callback.onTaskLoaded(task)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveTask(task: Task) {
        appExecutors.diskIO.execute { tasksDao.insertTask(task) }
    }

    override fun saveTask(task: Task, callback: TasksDataSource.GetTaskCallback) {
        appExecutors.diskIO.execute {
            val id = tasksDao.insertTask(task)
            val savedTask = tasksDao.getTaskById(id.toInt())
            if (savedTask != null) {
                callback.onTaskLoaded(savedTask)
            } else {
                callback.onDataNotAvailable()
            }
        }
    }

    override fun completeTask(task: Task) {
        appExecutors.diskIO.execute { task.id?.let { tasksDao.updateCompleted(it, true) } }
    }

    override fun completeTask(taskId: Int) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        appExecutors.diskIO.execute { task.id?.let { tasksDao.updateCompleted(it, false) } }
    }

    override fun activateTask(taskId: Int) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        appExecutors.diskIO.execute { tasksDao.deleteCompletedTasks() }
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        appExecutors.diskIO.execute { tasksDao.deleteTasks() }
    }

    override fun deleteTask(taskId: Int) {
        appExecutors.diskIO.execute { tasksDao.deleteTaskById(taskId) }
    }

    companion object {
        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, tasksDao: TasksDao): TasksLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TasksLocalDataSource::javaClass) {
                    INSTANCE = TasksLocalDataSource(appExecutors, tasksDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}