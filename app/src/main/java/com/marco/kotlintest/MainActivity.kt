package com.marco.kotlintest

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.marco.kotlintest.coroutine.generator.GeneratorActivity
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun toGeneratorActivity(view: View) {
        startActivity(Intent(this, GeneratorActivity::class.java))
    }

}