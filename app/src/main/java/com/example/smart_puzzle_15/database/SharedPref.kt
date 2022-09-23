package com.example.smart_puzzle_15.database

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    var preferences: SharedPreferences? = null

    init {
        if (preferences == null) {
            preferences = context.getSharedPreferences("MY_FILE", Context.MODE_PRIVATE);
        }
    }

    fun isDataSave(): Boolean? = preferences?.getBoolean("Save", false)

    fun alreadyGames(): Unit? = preferences?.edit()?.putBoolean("Save", true)?.apply()
//check
    fun saveNumber(list: Array<IntArray>) {
        for (i in list.indices) {
            for (j in 0 until list[i].size) {
                preferences?.edit()?.putInt("" + i + j, list[i][j])?.apply()
            }
        }
    }


    fun getNumber(): Array<IntArray> {
        val array = Array(4) { IntArray(4) }
        for (i in array.indices) {
            for (j in 0 until array[i].size) {
                array[i][j] = preferences?.getInt("" + i + j, 4 * i + j)!!
            }
        }
        return array
    }

    fun saveCount(count: Int) {
        preferences?.edit()?.putInt("Count", count)?.apply()
    }

    fun getCount(): Int? {
        return preferences?.getInt("Count", 0)
    }

    fun saveRecord(count: Int) {
        preferences?.edit()?.putInt("Record", count)?.apply()
    }

    fun getRecord(): Int? {
        return preferences?.getInt("Record", 0)
    }
}