package project.petshop.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import project.petshop.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Start icon animation and launch MainActivity in the end
        val bg: ImageView = findViewById<ImageView?>(R.id.bg).apply {
            translationY = -256f
            alpha = 0f
        }

        val logo: ImageView = findViewById<ImageView?>(R.id.logo).apply {
            alpha = 0f
        }

        val logoEnd: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                Handler(Looper.getMainLooper()).postDelayed({
                    Intent(this@SplashActivity, HomeActivity::class.java).also {
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            this@SplashActivity, android.R.anim.fade_in, android.R.anim.fade_out)
                        startActivity(it, options.toBundle())
                        finish()
                    }
                }, 5000)
            }
        }
        val bgEnd: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                logo.animate().alpha(1f).setDuration(300).setListener(logoEnd)
            }
        }
        bg.animate().translationY(0f).alpha(1f).setDuration(1000).setListener(bgEnd)
    }
}