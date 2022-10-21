package com.aemgtz.todo.task

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aemgtz.todo.MainActivity
import com.aemgtz.todo.R
import com.aemgtz.todo.data.Task
import com.aemgtz.todo.databinding.FragmentTaskBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TaskFragment : Fragment() {

    private var taskAdapter: TaskAdapter? = null
    private var _binding: FragmentTaskBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val args : TaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false).apply {
            viewModel = (activity as MainActivity).obtainViewModel()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        checkUser()
        setupTaskList()
        setupListeners()
        setupMenu()
    }

    private fun setupMenu(){
        // Add menu items without overriding methods in the Activity
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_logout  -> {
                        signOut()
                        true
                    }
                    R.id.action_refresh -> {
                        binding.viewModel?.fetchTasks(true)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun signOut(){
        Firebase.auth.signOut()
        val direction = TaskFragmentDirections.actionTaskFragmentToLoginFragment()
        findNavController().navigate(direction)
    }

    private fun checkUser(){
        val currentUser = args.user
        if (currentUser != null){
            Toast.makeText(requireContext(), "Welcome : ${currentUser.email}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            showAddTask(null)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.viewModel?.fetchTasks(true)
        }
    }

    private fun setupTaskList() {

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.taskRecyclerView.setHasFixedSize(false)
        binding.taskRecyclerView.isNestedScrollingEnabled = false
        binding.taskRecyclerView.layoutManager = linearLayoutManager

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = taskAdapter?.dataSource?.get(viewHolder.adapterPosition)
                task?.let {
                    binding.viewModel?.deleteTask(task)
                }
            }
        }).attachToRecyclerView(binding.taskRecyclerView)


        taskAdapter = TaskAdapter(taskActionListener)
        binding.taskRecyclerView.adapter = taskAdapter

        binding.viewModel?.tasks?.observe(viewLifecycleOwner) {
            taskAdapter?.dataSource = it
        }

        binding.viewModel?.fetchTasks(false)
    }

    private val taskActionListener = object : TaskAdapter.TaskItemActionsListener {
        override fun onTaskClicked(task: Task) {
            showAddTask(task)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun showAddTask(task: Task?) {
        val direction = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(task)
        findNavController().navigate(direction)
    }

    private fun hideSystemUI() {
        val activity = activity as AppCompatActivity?
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}