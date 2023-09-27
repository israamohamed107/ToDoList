package com.example.todolist.ui.dialoge

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.todolist.ui.tasks.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedFragment:DialogFragment() {
    private val taskViewModel: TaskViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Do your really want delete all complete tasks?")
            .setNegativeButton("No",null)
            .setPositiveButton("Yes"){_,_ ->
                taskViewModel.deleteCompleted()
            }.create()
    }
}