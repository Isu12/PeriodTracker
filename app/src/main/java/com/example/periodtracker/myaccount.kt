package com.example.periodtracker
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.tabs.TabLayout

class myaccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myaccount)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val childPoseImage: ImageView = findViewById(R.id.imageView4)


        childPoseImage.setOnClickListener {
            val intent = Intent(this@myaccount, ChildPose::class.java)  // Proper context and starting activity
            startActivity(intent)
        }

        // TabLayout listener for switching between activities
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            val intent = Intent(this@myaccount, MainActivity3::class.java)
                            startActivity(intent)
                        }
                        1 -> {
                            val intent = Intent(this@myaccount, settings::class.java)
                            startActivity(intent)
                        }
                        2 -> {

                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
}
