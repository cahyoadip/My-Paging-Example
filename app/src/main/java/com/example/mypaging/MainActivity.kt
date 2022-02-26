package com.example.mypaging

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mypaging.ui.fragment.FlowRepoFragment
import com.example.mypaging.ui.fragment.LivedataRepoFragment

class MainActivity : AppCompatActivity() {

    private var isFragmentActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragmentVisibility(false)
        findViewById<Button>(R.id.button).setOnClickListener {
            navigate(FlowRepoFragment())
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            navigate(LivedataRepoFragment())
        }
    }

    private fun navigate(fragment: Fragment) {
        try {
            setFragmentVisibility(true)
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    fragment
                ).commitNow()
        } catch (exception: ClassNotFoundException) {
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFragmentVisibility(isVisible: Boolean) {
        isFragmentActive = isVisible
        findViewById<View>(R.id.activity_container).visibility = if (isVisible) View.GONE else View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        setFragmentVisibility(isFragmentActive)
    }

    override fun onBackPressed() {
        if (isFragmentActive) {
            setFragmentVisibility(false)
        } else {
            super.onBackPressed()
        }
    }
}