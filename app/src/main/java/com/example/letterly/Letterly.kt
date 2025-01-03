package com.example.letterly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.recyclerview.widget.GridLayoutManager

class Letterly : AppCompatActivity() {

    private lateinit var boxRecyclerView: RecyclerView
    private lateinit var wordsRecyclerView: RecyclerView
    private lateinit var boxAdapter: BoxAdapter
    private lateinit var wordAdapter: WordAdapter
    private val boxes = mutableListOf<Box>()
    private val wordSuggestions = mutableListOf<Pair<String, String>>() // Pair of Word and Meaning

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letterly)

        val addBoxButton: Button = findViewById(R.id.addBoxButton)
        val removeBoxButton: Button = findViewById(R.id.removeBoxButton)
        val findWordsButton: Button = findViewById(R.id.findWordsButton)
        boxRecyclerView = findViewById(R.id.recyclerView)
        wordsRecyclerView = findViewById(R.id.wordsRecyclerView)

        boxAdapter = BoxAdapter(boxes)
        boxRecyclerView.layoutManager = GridLayoutManager(this,2)
        boxRecyclerView.adapter = boxAdapter

        wordAdapter = WordAdapter(wordSuggestions)
        wordsRecyclerView.layoutManager = LinearLayoutManager(this)
        wordsRecyclerView.adapter = wordAdapter

        addBoxButton.setOnClickListener {
            boxes.add(Box())
            boxAdapter.notifyItemInserted(boxes.size - 1)
        }

        removeBoxButton.setOnClickListener {
            if (boxes.isNotEmpty()) {
                val removedIndex = boxes.size - 1
                boxes.removeAt(removedIndex)
                boxAdapter.notifyItemRemoved(removedIndex)
            }
        }

        findWordsButton.setOnClickListener {
            val words = generateWordList()
            wordSuggestions.clear()
            wordSuggestions.addAll(words.toList())
            wordAdapter.notifyDataSetChanged()
        }
    }

    private fun loadDictionary(context: Context): Map<String, String> {
        val inputStream = context.assets.open("dictionary.json")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = bufferedReader.use { it.readText() }
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(jsonString, type)
    }




    private fun generateWordList(): Map<String, String> {
        val dictionary = loadDictionary(this)

        // Sort the dictionary by meaning (value)
        val sortedDictionary = dictionary.toList().sortedBy { (value, _) -> value }.toMap()

        // Apply filters based on the boxes
        val filteredWords = sortedDictionary.filter { (word, _) ->
            // Ensure the word length matches the size of the boxes
            if (word.length != boxes.size) {
                return@filter false
            }

            // Iterate over each box and apply filters
            boxes.indices.all { index ->
                val box = boxes[index]
                val letterAtPosition = word.getOrNull(index)?.toString()?.lowercase() ?: ""

                // Check includeLetter condition (if it's not empty)
                if (box.includeLetter.isNotEmpty()) {
                    val includeLetters = box.includeLetter.split(',').map { it.trim().lowercase() }
                    // Ensure the letter at this position is one of the includeLetters
                    if (!includeLetters.contains(letterAtPosition)) {
                        return@filter false
                    }
                }

                // Check excludeLetters condition (if it's not empty)
                if (box.excludeLetters.isNotEmpty()) {
                    val excludeLetters = box.excludeLetters.split(',').map { it.trim().lowercase() }
                    // Ensure the letter at this position is NOT one of the excludeLetters
                    if (excludeLetters.contains(letterAtPosition)) {
                        return@filter false
                    }
                }

                true
            }
        }

        return filteredWords
    }


}

data class Box(var includeLetter: String = "", var excludeLetters: String = "")
