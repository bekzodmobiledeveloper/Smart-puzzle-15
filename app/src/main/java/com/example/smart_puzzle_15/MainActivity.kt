package com.example.smart_puzzle_15

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_puzzle_15.database.SharedPref
import com.example.smart_puzzle_15.databinding.ActivityMainBinding
import com.example.smart_puzzle_15.dialog.Listener
import com.example.smart_puzzle_15.dialog.MyDialog
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private var arrayNumber = Array(4) { IntArray(4) }
    private var puzzleNumber = Array(4) { arrayOfNulls<TextView>(4) }
    lateinit var step: TextView
    lateinit var record: TextView
    lateinit var memory: SharedPref
    var emptyI = 3
    var emptyJ = 3
    var count = 0
    var min = Int.MAX_VALUE

    //  var list_item: android.widget.ImageView? = null
    var mediaPlayer: MediaPlayer? = null
    var isOn = true

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer.create(this, R.raw.osen)
        loadView()
        loadNumbers()
        loadData()
        binding.musicOn.setOnClickListener {
            pauseOrContinue()
        }

    }


    override fun onPause() {
        super.onPause()
        mediaPlayer!!.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer!!.start()
    }

    private fun pauseOrContinue() {
        isOn = if (isOn) {
            binding.musicOn!!.setImageResource(R.drawable.music_off)
            mediaPlayer!!.pause()
            false
        } else {
            binding.musicOn!!.setImageResource(R.drawable.music)
            mediaPlayer!!.start()
            true
        }
    }

    fun loadView() {
        memory = SharedPref(this)
        //  refresh = findViewById(R.id.refresh)
        record = findViewById(R.id.record)
        step = findViewById(R.id.step)
        record.text = "Record: " + memory.getRecord()
        val vg = findViewById<ViewGroup>(R.id.buttons)
        for (i in 0 until vg.childCount) {
            puzzleNumber[i / 4][i % 4] = vg.getChildAt(i) as TextView?
            val finalI = i
            puzzleNumber[i / 4][i % 4]?.setOnClickListener {
                onClickNumber(finalI / 4, finalI % 4)
            }
        }
        binding.refresh.setOnClickListener { reload() }
    }

    fun onClickNumber(i: Int, j: Int) {
        if ((abs((emptyI - i)) == 1 && emptyJ == j) || abs((emptyJ - j)) == 1 && emptyI == i) {
            puzzleNumber[emptyI][emptyJ]?.text = arrayNumber[i][j].toString()
            puzzleNumber[emptyI][emptyJ]?.visibility = View.VISIBLE
            puzzleNumber[i][j]?.visibility = View.INVISIBLE
            arrayNumber[emptyI][emptyJ] = arrayNumber[i][j]
            arrayNumber[i][j] = 0
            emptyI = i
            emptyJ = j
            count++
            step.text = "Count:  $count"
            if (i == 3 && j == 3) {
                if (isWin()) {
                    if (count < min) {
                        memory.saveRecord(count)
                        record.text = "record: " + memory.getRecord()
                        min = memory.getRecord()!!
                    }
                    val myDialog = MyDialog(this@MainActivity, object : Listener {
                        override fun onClick() {
                            reload()
                        }
                    })
                    myDialog.show()
                }
            }
        }
    }

    fun loadNumbers() {
        if (memory.isDataSave() == true) {
            arrayNumber = memory.getNumber()
            count = memory.getCount()!!
        } else {
            shuffleNums()
        }
    }

    fun loadData() {
        for (i in arrayNumber.indices) {
            Log.d("TTT", "loadData: ${arrayNumber[i].contentToString()}")
            for (j in 0 until arrayNumber[i].size) {
                if (arrayNumber[i][j] == 0) {
                    emptyI = i
                    emptyJ = j
                    puzzleNumber[i][j]?.visibility = View.INVISIBLE
                } else {
                    puzzleNumber[i][j]?.visibility = View.VISIBLE
                    puzzleNumber[i][j]?.text = arrayNumber[i][j].toString()
                }
            }
        }
        step.text = count.toString()
    }

    fun reload() {
        shuffleNums()
        loadData()
    }

    private fun shuffleNums() {
        val list = mutableListOf<Int>()
        for (i in 1..15) {
            list.add(i)
        }
        do {
            list.shuffle()
        } while (!isSolvable(list as ArrayList<Int>))
        for (i in list.indices) {
            arrayNumber[i / 4][i % 4] = list[i]
        }
        arrayNumber[3][3] = 0
        count = 0
    }

    private fun isSolvable(puzzle: ArrayList<Int>): Boolean {
        var parity = 0
        val gridWidth = sqrt(puzzle.size.toDouble()).toInt()
        var row = 0 // the current row we are on
        var blankRow = 0 // the row with the blank tile
        for (i in puzzle.indices) {
            if (i % gridWidth == 0) { // advance to next row
                row++
            }
            if (puzzle[i] == 0) { // the blank tile
                blankRow = row // save the row on which encountered
                continue
            }
            for (j in i + 1 until puzzle.size) {
                if (puzzle[i] > puzzle[j] && puzzle[j] != 0) {
                    parity++
                }
            }
        }
        return if (gridWidth % 2 == 0) { // even grid
            if (blankRow % 2 == 0) { // blank on odd row; counting from bottom
                parity % 2 == 0
            } else { // blank on even row; counting from bottom
                parity % 2 != 0
            }
        } else { // odd grid
            parity % 2 == 0
        }
    }

    private fun isWin(): Boolean {
        for (i in 0 until 15) {
            if (arrayNumber[i / 4][i % 4] != (i + 1)) return false
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        memory.alreadyGames()
        memory.saveCount(count)
        memory.saveNumber(arrayNumber)
    }

}