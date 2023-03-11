package com.example.rxjavaandretrofitusing.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rxjavaandretrofitusing.R
import com.example.rxjavaandretrofitusing.model.GetTestResponse
import com.example.rxjavaandretrofitusing.view.TestDetailActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.test_recycler_row.view.*

class TestAdapter(val testList : ArrayList<GetTestResponse>,
                           private val listener : TestAdapter.Listener
) : RecyclerView.Adapter<TestAdapter.PostHolder>() {


    interface Listener {
        fun onTestItemClick(blog : GetTestResponse)
    }

    class PostHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.test_recycler_row, parent, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {


        Picasso.get().load(testList.get(position).field5).into(holder.itemView.recyclerImageView)
        holder.itemView.recyclerNameText.text = testList.get(position).field2
        holder.itemView.setOnClickListener {
            listener.onTestItemClick(testList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return testList.size
    }
}