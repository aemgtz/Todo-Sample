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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aemgtz.todo.addedittask.AddEditTaskActivity.Companion.EXTRA_TASK
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.databinding.FragmentAddEditTaskBinding

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment(), AddEditTaskContract.View {


    override var presenter: AddEditTaskContract.Presenter? = null
    private var _binding: FragmentAddEditTaskBinding? = null

    private val binding get() = _binding!!
    private var task: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener {
            val title = binding.addTaskTitle.text.toString()
            val detail = binding.addTaskDescription.text.toString()
            if (title.isEmpty()){
                return@setOnClickListener
            }
            saveTask(title, detail)
        }
        binding.deleteButton.visibility = View.INVISIBLE
        binding.deleteButton.setOnClickListener {
            task?.let {
                deleteTask(it)
            }
        }

        task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_TASK, Task::class.java)
        } else {
            arguments?.getParcelable(EXTRA_TASK) as Task?
        }

        task?.let {
            binding.addTaskTitle.setText(it.title)
            binding.addTaskDescription.setText(it.detail)
            binding.deleteButton.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun saveTask(title: String, detail: String) {
        val saveTask = if (task != null) {
            Task(task!!.id, title, detail, task!!.isCompleted)
        }else{
            Task(null, title, detail, false)
        }
        presenter?.saveTask(saveTask)
    }

    private fun deleteTask(task: Task){
        presenter?.deleteTask(task)
        activity?.finish()
    }

    override fun onTaskSaved(task: Task) {
        activity?.finish()
    }

    companion object {
        fun newInstance() = AddEditTaskFragment()
    }
}
