package com.github.kosher9.mycheckbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kosher9.library.NiceCheckBox

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val s = findViewById<NiceCheckBox>(R.id.nice)
        val e = s.isChecked
    }
}