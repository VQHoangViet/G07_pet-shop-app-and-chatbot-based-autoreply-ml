package project.petshop.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.home
import project.petshop.R
import project.petshop.objects.User
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

        editBtn.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        sign_out.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        refresh()
    }

    private fun refresh() {
        User.get(Firebase.auth.currentUser!!.uid)
            .addOnSuccessListener { docUser ->
                if (!docUser.exists()) {
                    val intent = Intent(this, ProfileEditActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean("emailEdit", false)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    return@addOnSuccessListener
                }

                val user = User(docUser)
                val _gender : String = when (user.gender.toString()) {
                    "0" -> "Male"
                    "1" -> "Female"
                    else -> "Other"
                }
                val _dob = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    .format(user.dob!!)

                user.setAvatar(this, avatar)
                name.text = user.name
                dob.text = _dob
                phone.text = user.phone
                email.text = user.email
                gender.text = _gender
                address.text = user.address
            }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}