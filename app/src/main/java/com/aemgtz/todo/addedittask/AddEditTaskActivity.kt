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

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aemgtz.todo.R
import com.aemgtz.todo.data.Injection
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.utils.replaceFragmentInActivity
import com.aemgtz.todo.databinding.ActivityAddEditTaskBinding

/**
 * Displays an add or edit task screen.
 */
class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var addEditTaskPresenter: AddEditTaskPresenter
    private lateinit var binding: ActivityAddEditTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        title = ""


        //val task = intent?.getParcelableExtra(EXTRA_TASK) as Task?

        val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent?.getParcelableExtra(EXTRA_TASK) as Task?
        }

        val addEditTaskFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                        ?: AddEditTaskFragment.newInstance().also {
                    replaceFragmentInActivity(it, R.id.contentFrame) }
        // Create the presenter
        addEditTaskFragment.arguments = Bundle().apply {
            this.putParcelable(EXTRA_TASK, task)
        }
        addEditTaskPresenter = AddEditTaskPresenter(null, addEditTaskFragment,  Injection.provideTasksRepository(applicationContext))
    }

    companion object {
        const val EXTRA_TASK = "extra_task"
    }
}