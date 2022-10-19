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

package com.aemgtz.todo.addedittask

import com.aemgtz.todo.data.Task
import com.aemgtz.todo.data.TasksDataSource


class AddEditTaskPresenter(
    private val taskId: Int?,
    private val view: AddEditTaskContract.View?,
    private val tasksRepository: TasksDataSource) : AddEditTaskContract.Presenter {

    init {
        view?.presenter = this
    }

    override fun saveTask(task: Task) {
        tasksRepository.saveTask(task, object : TasksDataSource.GetTaskCallback{
            override fun onTaskLoaded(task: Task) {
               view?.onTaskSaved(task)
            }

            override fun onDataNotAvailable() {

            }
        })
    }

    override fun deleteTask(task: Task) {
        task.id?.let {
            tasksRepository.deleteTask(it)
        }
    }
}
