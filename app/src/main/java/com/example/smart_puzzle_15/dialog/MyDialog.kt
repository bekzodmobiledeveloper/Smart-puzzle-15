package com.example.smart_puzzle_15.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import com.example.smart_puzzle_15.R

class MyDialog(context: Context, val listener: Listener) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog)
        val btn: Button = findViewById(R.id.reload)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        btn.setOnClickListener {
            listener.onClick()
            dismiss()
        }
    }
}

interface Listener {
    fun onClick()
}
