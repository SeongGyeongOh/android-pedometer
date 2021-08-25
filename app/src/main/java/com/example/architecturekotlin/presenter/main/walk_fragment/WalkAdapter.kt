package com.example.architecturekotlin.presenter.main.walk_fragment

//import androidx.navigation.Navigation.findNavController
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.architecturekotlin.R
import com.example.architecturekotlin.databinding.ItemDailyWalkBinding
import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.presenter.main.MainViewModel
import javax.inject.Inject

class WalkAdapter @Inject constructor(

) : ListAdapter<WalkModel, RecyclerView.ViewHolder>(WalkDiffCallback()) {
    var viewModel: MainViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemDailyWalkBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_daily_walk,
            parent,
            false
        )

        val holder = WalkHolder(binding)
        binding.holder = holder

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as WalkHolder).bind(getItem(position))
    }

    inner class WalkHolder(
        private val binding: ItemDailyWalkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkModel) {
            binding.apply {
                walk = item
                executePendingBindings()
            }
        }
    }
}

private class WalkDiffCallback : DiffUtil.ItemCallback<WalkModel>() {
    override fun areItemsTheSame(oldItem: WalkModel, newItem: WalkModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WalkModel, newItem: WalkModel): Boolean {
        return oldItem == newItem
    }
}