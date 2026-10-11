package com.example.imc_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private var gender: String? = null
private var height = 0

private var weight = 60
private var age = 18
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

        //----------------------------
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


        //----------------------------
        val tvWeight = findViewById<TextView>(R.id.tvWeight)
        val tvAge = findViewById<TextView>(R.id.tvAge)


        findViewById<ImageButton>(R.id.btnWeightMinus).setOnClickListener {
            if (weight > 1) {
                weight--
                tvWeight.text = weight.toString()
            }
        }
        findViewById<ImageButton>(R.id.btnWeightPlus).setOnClickListener {
            weight++
            tvWeight.text = weight.toString()
        }
        findViewById<ImageButton>(R.id.btnAgeMinus).setOnClickListener {
            if (age > 1) {
                age--
                tvAge.text = age.toString()
            }
        }
        findViewById<ImageButton>(R.id.btnAgePlus).setOnClickListener {
            age++
            tvAge.text = age.toString()
        }
        findViewById<Button>(R.id.btnCalculate).setOnClickListener {
            if (height == 0) {
                Toast.makeText(this, getString(R.string.error_height), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("HEIGHT", height)
            intent.putExtra("WEIGHT", weight)
            intent.putExtra("AGE", age)
            intent.putExtra("GENDER", gender)
            startActivity(intent)
        }
    }


}