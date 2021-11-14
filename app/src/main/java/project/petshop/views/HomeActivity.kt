package project.petshop.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import project.petshop.R
import project.petshop.extensions.Extensions.toast
import project.petshop.utils.FirebaseUtils.firebaseAuth

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
// sign out a user

        btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            toast("signed out")
            finish()
        }


    }
}