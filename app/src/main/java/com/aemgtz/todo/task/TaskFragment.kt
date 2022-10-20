package com.aemgtz.todo.task

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aemgtz.todo.addedittask.AddEditTaskActivity
import com.aemgtz.todo.addedittask.AddEditTaskActivity.Companion.EXTRA_TASK
import com.aemgtz.todo.data.Injection
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.databinding.FragmentTaskBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TaskFragment() : Fragment(), TaskContract.View {

//    private val viewModel: TaskViewModel by viewModels()

    private var taskAdapter: TaskAdapter? = null
    override var presenter: TaskContract.Presenter? = null

    private var _binding: FragmentTaskBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args : TaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        setupTaskList()
        setupListeners()
    }

    private fun checkUser(){
        val currentUser = args.user
        if (currentUser != null){
            Toast.makeText(requireContext(), "Welcome : ${currentUser.email}", Toast.LENGTH_LONG).show()
            presenter = TaskPresenter(this, Injection.provideTasksRepository(requireContext().applicationContext, currentUser))
        }
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            showAddTask(null)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter?.fetchTasks()
        }
    }

    private fun setupTaskList() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.setHasFixedSize(false)
        binding.taskRecyclerView.isNestedScrollingEnabled = false
        binding.taskRecyclerView.layoutManager = linearLayoutManager

        taskAdapter = TaskAdapter(taskActionListener)
        val tasks = mutableListOf<Task>()
        taskAdapter?.dataSource = tasks
        binding.taskRecyclerView.adapter = taskAdapter
//
//        viewModel.tasks.observe(viewLifecycleOwner) {
//            taskAdapter?.dataSource = it
//        }
    }

    private val taskActionListener = object : TaskAdapter.TaskItemActionsListener {
        override fun onTaskClicked(task: Task) {
            showAddTask(task)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.fetchTasks()
    }

    override fun onTaskLoaded(tasks: List<Task>) {
        taskAdapter?.dataSource = tasks
    }

    override fun onTaskUpdate() {

    }

    override fun setLoadingIndicator(isActive: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = isActive
    }

    private fun showAddTask(task: Task?) {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        intent.putExtra(EXTRA_TASK, task)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}