package com.example.aicomsapp.viewmodels.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aicomsapp.databinding.ItemTodoBinding
import com.example.aicomsapp.viewmodels.shared.SharedUserViewModel

class TodoAdapter(private val viewModel: SharedUserViewModel) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var todoList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val task = todoList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = todoList.size

    fun submitList(list: MutableList<String>) {
        todoList = list
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: String) {
            binding.todoText.text = task

            binding.deleteButton.setOnClickListener {
                viewModel.removeTodoItem(task)
            }
        }
    }
}
