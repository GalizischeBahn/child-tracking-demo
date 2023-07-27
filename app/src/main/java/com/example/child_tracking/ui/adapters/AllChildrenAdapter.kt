package com.example.child_tracking.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.child_tracking.data.Child
import com.example.child_tracking.databinding.ChildItemBinding

class AllChildrenAdapter(private val onItemClicked: (Child) -> Unit) :
    ListAdapter<Child, AllChildrenAdapter.ChildrenViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenViewHolder {
       val  viewHolder = ChildrenViewHolder(
          ChildItemBinding.inflate(LayoutInflater.from(parent.context),
          parent,
          false)
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.layoutPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }
companion object {
    private val DiffCallback = object : DiffUtil.ItemCallback<Child>() {
        override fun areItemsTheSame(oldItem: Child, newItem: Child): Boolean {
            return oldItem.childName == newItem.childName
        }

        override fun areContentsTheSame(oldItem: Child, newItem: Child): Boolean {
            return oldItem.childName == newItem.childName
        }
    }}




    override fun getItemCount(): Int {
        return currentList.size
    }


    override fun onBindViewHolder(holder: ChildrenViewHolder, position: Int) {

        val child = getItem(position)
        holder.apply {
            bind(child, onItemClicked)
        }
    }

     class ChildrenViewHolder(
        private var binding: ChildItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind (child: Child, onItemClicked: (Child) -> Unit) {
            binding.childNameTextView.text = child.childName
            binding.selectChildBtn.setOnClickListener{
                onItemClicked(child)

            }
        }
    }
}


