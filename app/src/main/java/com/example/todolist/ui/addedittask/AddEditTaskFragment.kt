package com.example.todolist.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentAddEditTaskBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)


        binding.apply {
            edTxtTaskName.setText(viewModel.taskName)
            checkImportant.isChecked = viewModel.taskImportance
            txtDate.isVisible = (viewModel.task != null)
            "Created at: ${viewModel.task?.creationTime}".also { txtDate.text = it }

            edTxtTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }
            checkImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }
            btnSave.setOnClickListener {
                viewModel.onSaveBtnCliched()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addEditTaskChannel.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBack -> {
//                        binding.edTxtTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_flag",
                            bundleOf("flag" to event.flag)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }

            }
        }

    }


}