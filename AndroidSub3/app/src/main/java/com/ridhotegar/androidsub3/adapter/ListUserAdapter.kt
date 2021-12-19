package com.ridhotegar.androidsub3.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ridhotegar.androidsub3.databinding.ItemRowUserBinding
import com.ridhotegar.androidsub3.model.domain.User

class ListUserAdapter : RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    private val listUser = mutableListOf<User>()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(users: List<User>) {
        listUser.clear()
        listUser.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemRowUserBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val binding = holder.binding

        val data = listUser[position]

        binding.tvItemUsername.text = data.username
        binding.tvItemName.text = data.name

        Glide.with(binding.root.context)
            .load(data.photo)
            .circleCrop()
            .into(binding.imgItemPhoto)

        binding.root.setOnClickListener {
            onItemClickCallback?.onItemClicked(listUser[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listUser.size

    class ListViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(user: User)
    }

}