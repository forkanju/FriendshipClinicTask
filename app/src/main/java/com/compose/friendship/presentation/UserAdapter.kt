package com.compose.friendship.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.compose.friendship.Constants.Companion.ACTIVE
import com.compose.friendship.R
import com.compose.friendship.databinding.ItemUserBinding
import com.compose.friendship.model.User
import java.util.Locale
import java.util.Random

class UserAdapter(
    private val list: List<User.UserInfo>,
    private val onItemClicked: (User.UserInfo) -> Unit,
    private val onEditClicked: (User.UserInfo) -> Unit,
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(

        private val binding: ItemUserBinding,
        private val context: Context,

        ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bindView(item: User.UserInfo) {
            binding.apply {
                cardText.text = item.name.getFirstLetter()
                tvName.text = item.name
                tvGender.text = item.gender.capitalizeFirstLetter()
                tvEmail.text = item.email
                itemCard.setOnClickListener {
                    onItemClicked(item)
                }
                edit.setOnClickListener {
                    onEditClicked(item)
                }


                if (item.status == ACTIVE){
                    cardCircle.setCardBackgroundColor(getRandomColor())
                }else{
                    cardCircle.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.inactive
                        )
                    )
                }

            }
        }
    }

    private fun getRandomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        parent.context
    )

    override fun getItemCount(): Int = list.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item)
    }
}

fun String.getFirstLetter(): String {
    return if (isNotEmpty()) {
        this[0].uppercaseChar().toString()
    } else {
        ""
    }
}

fun String.capitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        this.substring(0, 1).uppercase(Locale.ROOT) + this.substring(1)
    } else {
        ""
    }
}

