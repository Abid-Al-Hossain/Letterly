package com.example.letterly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

class BoxAdapter(private val boxes: MutableList<Box>) :
    RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_box, parent, false)
        return BoxViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val box = boxes[position]
        holder.includeLetterEditText.setText(box.includeLetter)
        holder.excludeLettersEditText.setText(box.excludeLetters)

        holder.includeLetterEditText.addTextChangedListener {
            val text = it.toString()
            if (text.contains("[^,]{2}".toRegex())) {
                Toast.makeText(holder.itemView.context, "Error: Consecutive letters without a comma", Toast.LENGTH_SHORT).show()
            } else {
                box.includeLetter = text
            }
        }

        holder.excludeLettersEditText.addTextChangedListener {
            val text = it.toString()
            if (text.contains("[^,]{2}".toRegex())) {
                Toast.makeText(holder.itemView.context, "Error: Consecutive letters without a comma", Toast.LENGTH_SHORT).show()
            } else {
                box.excludeLetters = text
            }
        }
    }

    override fun getItemCount() = boxes.size

    class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val includeLetterEditText: EditText = itemView.findViewById(R.id.includeLetterEditText)
        val excludeLettersEditText: EditText = itemView.findViewById(R.id.excludeLettersEditText)
    }
}
