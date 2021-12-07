package project.petshop.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.home
import project.petshop.R
import project.petshop.objects.User
import project.petshop.utils.FirebaseUtils
import project.petshop.utils.FirebaseUtils.firebaseAuth
import project.petshop.utils.FirebaseUtils.firebaseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_profile.avatar as avatar1

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        home.setOnClickListener {
            onBackPressed()
        }

        sign_out.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this@ProfileActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        User.get(firebaseUser!!.uid)
            .addOnSuccessListener { docUser ->
                val user = User(docUser)
                val _gender : String = when (user.gender.toString()) {
                    "0" -> "Male"
                    "1" -> "Female"
                    else -> "Other"
                }
                val _dob = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    .format(user.dob!!)

                name.text = user.name
                dob.text = _dob
                phone.text = user.phone
                email.text = user.email
                gender.text = _gender
                address.text = user.address
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}