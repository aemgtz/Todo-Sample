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

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

/**
 * Implementation of the data source that adds a latency simulating network.
 */
class TasksRemoteDataSource private constructor() : TasksDataSource {

    private val firebaseFireStore by lazy { FirebaseFirestore.getInstance() }

    init {

    }
    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        // Simulate network by delaying the execution.
        val user = Firebase.auth.currentUser
        // [START get_all_users]
        firebaseFireStore.collection(TASK_COLLECTION_NAME)
            .whereEqualTo(UUID, user?.uid)
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
                Log.w(TAG, "Error getting documents.", exception)
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

    }

    override fun saveTask(task: Task) {

    }

    override fun saveTask(task: Task, callback: TasksDataSource.GetTaskCallback) {

        val user = Firebase.auth.currentUser
        val insertTask = hashMapOf(
            "uuid" to user?.uid,
            "title" to task.title,
            "detail" to task.detail,
            "isCompleted" to task.isCompleted
        )
        val operation = if (task.taskId != null){
            firebaseFireStore.collection(TASK_COLLECTION_NAME).document(task.taskId!!).set(insertTask)
        }else{
            firebaseFireStore.collection(TASK_COLLECTION_NAME).add(insertTask)
        }
        operation.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            if (documentReference != null){
                task.taskId = (documentReference as DocumentReference).id
                callback.onTaskLoaded(task)
            }else{
                callback.onTaskLoaded(task)
            }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document ${exception.localizedMessage}")
                callback.onDataNotAvailable()
            }
    }

    override fun completeTask(task: Task) {

    }

    override fun completeTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {

    }

    override fun activateTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {

    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
     }

    override fun deleteTask(taskId: String, callback: TasksDataSource.TaskCallback<Boolean>) {
        firebaseFireStore.collection(TASK_COLLECTION_NAME).document(taskId).delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")

            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e)

            }
    }


    companion object {

        private const val TASK_COLLECTION_NAME = "tasks"
        private const val UUID = "uuid"
        private const val TAG = "TasksRemoteDataSource"

        private var INSTANCE: TasksRemoteDataSource? = null

        @JvmStatic
        fun getInstance(): TasksRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(TasksRemoteDataSource::javaClass) {
                    INSTANCE = TasksRemoteDataSource()
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
