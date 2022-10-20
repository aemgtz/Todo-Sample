/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.aemgtz.todo.data

import android.content.Context
import com.aemgtz.todo.data.source.local.TasksLocalDataSource
import com.aemgtz.todo.data.source.local.ToDoDatabase
import com.aemgtz.todo.data.source.remote.TasksRemoteDataSource
import com.aemgtz.todo.utils.AppExecutors
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Enables injection of mock implementations for
 * [TasksDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {

    fun provideTasksRepository(context: Context, firebaseUser: FirebaseUser, fireStore: FirebaseFirestore): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(firebaseUser, fireStore),
                TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao()))
    }
}
