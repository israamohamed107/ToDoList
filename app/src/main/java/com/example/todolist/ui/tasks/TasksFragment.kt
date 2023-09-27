package com.example.todolist.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.SortBy
import com.example.todolist.data.Task
import com.example.todolist.databinding.FragmentTasksBinding
import com.example.todolist.utils.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    private val viewModel:TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksBinding.bind(view)
        val myAdapter = TaskAdapter(this)


        binding.apply {
            recyclerTasks.apply {
                adapter = myAdapter
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = myAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onItemSwiped(task)
                }

            }).attachToRecyclerView(recyclerTasks)


            btnAdd.setOnClickListener {
                viewModel.onAddButtonClicked()
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            myAdapter.submitList(it)
            binding.recyclerTasks.adapter = myAdapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEventChannel.collect { event ->
                when (event) {
                    is TaskViewModel.TasksEvent.ShowDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoClicked(event.task)
                            }.show()
                    }
                    is TaskViewModel.TasksEvent.NavigateToAddScreen -> {
                        findNavController().navigate(
                            TasksFragmentDirections.actionTasksFragment2ToAddEditTaskFragment(
                                "New Task",
                                null
                            )
                        )
                    }
                    is TaskViewModel.TasksEvent.NavigateToEditScreen -> {
                        findNavController().navigate(
                            TasksFragmentDirections.actionTasksFragment2ToAddEditTaskFragment(
                                "Edit Task", event.task
                            )
                        )

                    }
                    is TaskViewModel.TasksEvent.AddEditConfirmationMessage -> {

                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }

                    TaskViewModel.TasksEvent.NavigateToDeleteAllCompletedFragment -> {
                        findNavController().navigate(R.id.action_global_deleteAllCompletedFragment)

                    }
                }

            }
        }

        setFragmentResultListener("add_edit_flag") { _, bundle ->
            val flag = bundle.getInt("flag")
            viewModel.onAddEditClicked(flag)

        }

        setHasOptionsMenu(true)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_serach)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_task).isChecked =
                viewModel.preferenceFlow.first().hideCompleted
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_creation_date -> {
                viewModel.sortOrderSelected(SortBy.Date)
                true
            }
            R.id.action_sort_by_name -> {
                viewModel.sortOrderSelected(SortBy.Name)
                true
            }
            R.id.action_hide_completed_task -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompletedSelected(item.isChecked)
                true
            }
            R.id.action_delete_completed -> {
                viewModel.showDeleteConfirmationMessage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTaskClicked(task: Task) {
        viewModel.onTaskClicked(task)
    }

    override fun onTaskChecked(task: Task, isChecked: Boolean) {
        viewModel.onTaskChecked(task, isChecked)
    }

}