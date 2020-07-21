package com.github.kosher9.mycheckbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.kosher9.library.NiceCheckBox

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val s = findViewById<NiceCheckBox>(R.id.nice)
        s.setOnClickListener {
            Toast.makeText(this, "${s.isChecked}", Toast.LENGTH_LONG).show()
        }
    }
}