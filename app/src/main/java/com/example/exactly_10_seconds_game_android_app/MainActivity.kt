package com.example.exactly_10_seconds_game_android_app

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.exactly_10_seconds_game_android_app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dateFormat = SimpleDateFormat("ss.S秒", Locale.getDefault())

    companion object {
        const val INTERVAL_MILLISECOND: Long = 10 // 処理実行周期
    }

    var time: Long = 0

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

        // API 30以上の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.apply {
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            // API 29以下の場合
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

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
                    binding.speechBubble.isVisible = false
                }

                getString(R.string.button_text_stop) -> {
                    // ストップボタン押下
                    handler.removeCallbacks(timer)
                    binding.button.text = getString(R.string.button_text_retry)
                    binding.speechBubble.isVisible = true
                    // 計測した時間をString型からLong型に変換
                    val countTimeStringOnDecimalPointAndUnit = binding.timeTextView.text.toString()
                    val countTimeString = countTimeStringOnDecimalPointAndUnit.replace(
                        regex = "[.秒]".toRegex(),
                        replacement = ""
                    )
                    val countTimeInt = countTimeString.toInt()
                    val countTimeLong = countTimeInt.toLong()
                    binding.resultTextView.text = getResultComment(countTimeLong)
                }

                else -> {
                    binding.button.text = getString(R.string.button_text_start)
                    time = 0
                    binding.timeTextView.text = dateFormat.format(time)
                    binding.speechBubble.isVisible = false
                }
            }
        }
    }

    /**
     * 結果に対するコメントを取得する.
     *
     * @param countTime 計った時間
     * @return 結果に対するコメント
     */
    private fun getResultComment(countTime: Long): String {
        val successTime = 100L
        val smallCloseTime = 95L
        val largeCloseTime = 105L

        when (countTime) {
            successTime -> {
                return getString(R.string.speech_bubble_success)
            }

            in smallCloseTime..largeCloseTime -> {
                return getString(R.string.speech_bubble_close)
            }

            else -> {
                return getString(R.string.speech_bubble_failure)
            }
        }
    }
}