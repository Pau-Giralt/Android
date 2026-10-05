package com.example.imc_app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private var gender: String? = null
private var height = 0
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cardMale = findViewById<LinearLayout>(R.id.cardMale)
        val cardFemale = findViewById<LinearLayout>(R.id.cardFemale)
        cardMale.setOnClickListener {
            cardMale.isSelected = true
            cardFemale.isSelected = false
            gender = "MALE"
        }
        cardFemale.setOnClickListener {
            cardFemale.isSelected = true
            cardMale.isSelected = false
            gender = "FEMALE"
        }
        val tvHeight = findViewById<TextView>(R.id.tvHeight)
        val seekHeight = findViewById<SeekBar>(R.id.seekHeight)

        seekHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                height = progress
                tvHeight.text = height.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


}