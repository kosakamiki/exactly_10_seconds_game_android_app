package com.example.exactly_10_seconds_game_android_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exactly_10_seconds_game_android_app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val INTERVAL_MILLISECOND: Long = 10 // 処理実行周期
    }

    var time: Long = 0
    val dateFormat = SimpleDateFormat("ss.S秒", Locale.getDefault())

    val handler = Handler(Looper.getMainLooper())
    private val timer = object : Runnable {
        override fun run() {
            // 一定間隔で処理実行
            time += INTERVAL_MILLISECOND
            binding.timeTextView.text = dateFormat.format(time)
            // INTERVAL_MILLISECONDで指定した秒数毎にrunメソッドが実行される
            handler.postDelayed(this, INTERVAL_MILLISECOND)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Hide the status bar.
        //TODO:'setter for systemUiVisibility'とSYSTEM_UI_FLAG_FULLSCREENはJavaで非推奨になっているため変える
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // スタート・ストップ・リトライボタン押下
        binding.button.setOnClickListener {
            when (binding.button.text.toString()) {
                getString(R.string.button_text_start) -> {
                    // スタートボタン押下
                    handler.post(timer)
                    binding.button.text = getString(R.string.button_text_stop)
                }

                getString(R.string.button_text_stop) -> {
                    // ストップボタン押下
                    handler.removeCallbacks(timer)
                    binding.button.text = getString(R.string.button_text_retry)
                }

                else -> {
                    binding.button.text = getString(R.string.button_text_start)
                    time = 0
                    binding.timeTextView.text = dateFormat.format(time)
                }
            }
        }
    }
}