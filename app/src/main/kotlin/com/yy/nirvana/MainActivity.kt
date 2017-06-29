package com.yy.nirvana

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.yy.pillar.ffmpeg.FFmpegNative
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit private var show : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTest.setOnClickListener { mText.text = FFmpegNative.avcodecinfo() }
        mClick.setOnClickListener { toExec(mEdit.text.toString()) }
    }

    private fun toExec(cmd: String) {
        show = ProgressDialog.show(this@MainActivity, null, "执行中...", true)
        val cmds = cmd.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        FFmpegNative.execute(cmds) { ret ->
            runOnUiThread {
                Toast.makeText(this@MainActivity, "执行完成=" + ret, Toast.LENGTH_SHORT).show()
                show.dismiss()
            }
        }
    }

}
