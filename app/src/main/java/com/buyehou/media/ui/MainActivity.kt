package com.buyehou.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buyehou.media.databinding.ActivityMainBinding

/**
 * @author buyehou
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}