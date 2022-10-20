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
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Implementation of the data source that adds a latency simulating network.
 */
class TasksRemoteDataSource private constructor(private  val firebaseUser: FirebaseUser, private val fireStore : FirebaseFirestore) : TasksDataSource {


    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {

    }
    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        // Simulate network by delaying the execution.
        // [START get_all_users]
        fireStore.collection(TASK_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                val tasks = mutableListOf<Task>()
                for (document in result) {
                    val task = document.toObject(Task::class.java)
                    task.taskId = document.id
                    tasks.add(task)
                }
                callback.onTasksLoaded(tasks)
            }
            .addOnFailureListener { exception ->
                Log.w("RemoteDataSource", "Error getting documents.", exception)
                callback.onDataNotAvailable()
            }
        // [END get_all_users]
    }

    /**
     * Note: [GetTaskCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskId]

        // Simulate network by delaying the execution.
        with(Handler()) {
            if (task != null) {
                postDelayed({ callback.onTaskLoaded(task) }, Companion.SERVICE_LATENCY_IN_MILLIS)
            } else {
                postDelayed({ callback.onDataNotAvailable() }, Companion.SERVICE_LATENCY_IN_MILLIS)
            }
        }
    }

    override fun saveTask(task: Task) {
        task.taskId?.let { TASKS_SERVICE_DATA.put(it, task) }
    }

    override fun saveTask(task: Task, callback: TasksDataSource.GetTaskCallback) {

    }

    override fun completeTask(task: Task) {
        val completedTask = Task(task.id, task.taskId, task.title, task.detail, true).apply {
            isCompleted = true
        }
        task.taskId?.let { TASKS_SERVICE_DATA.put(it, completedTask) }
    }

    override fun completeTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        val activeTask = Task(task.id, task.taskId, task.title, task.detail, task.isCompleted)
        task.taskId?.let { TASKS_SERVICE_DATA.put(it, activeTask) }
    }

    override fun activateTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues { !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }


    companion object {

        private const val SERVICE_LATENCY_IN_MILLIS = 5000L
        private const val TASK_COLLECTION_NAME = "tasks"

        private var INSTANCE: TasksRemoteDataSource? = null

        @JvmStatic
        fun getInstance(firebaseUser: FirebaseUser, fireStore: FirebaseFirestore): TasksRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(TasksRemoteDataSource::javaClass) {
                    INSTANCE = TasksRemoteDataSource(firebaseUser, fireStore)
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
