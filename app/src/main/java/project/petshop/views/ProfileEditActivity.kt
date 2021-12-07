package project.petshop.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.address
import kotlinx.android.synthetic.main.activity_profile.dob
import kotlinx.android.synthetic.main.activity_profile.email
import kotlinx.android.synthetic.main.activity_profile.name
import kotlinx.android.synthetic.main.activity_profile.phone
import kotlinx.android.synthetic.main.activity_profile_edit.*
import project.petshop.R
import project.petshop.extensions.Extensions.toast
import project.petshop.objects.User
import project.petshop.utils.FirebaseUtils
import project.petshop.utils.FirebaseUtils.firebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileEditActivity : AppCompatActivity() {
    var profileUpdated : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Update form with Firestore data
        User.get(firebaseAuth.currentUser!!.uid)
            .addOnSuccessListener { docUser ->
                profileUpdated = docUser.exists()
                if (!profileUpdated) {
                    return@addOnSuccessListener
                }

                val user = User(docUser)
                val _dob = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    .format(user.dob!!)

                name.text = user.name
                dob.text = _dob
                phone.text = user.phone
                email.text = user.email
                gender_0.isChecked = false
                gender_1.isChecked = false
                gender_2.isChecked = false
                when (user.gender.toString()) {
                    "0" -> gender_0.isChecked = true
                    "1" -> gender_1.isChecked = true
                    else -> gender_2.isChecked = true
                }
                address.text = user.address
            }

        // TODO: Open date dialog when click dob


        // Update button
        update.setOnClickListener {
            // Check if any field is empty
            val list = listOf(name, phone, email, dob, address)
            for (field in list) {
                if (field.text.toString().isEmpty()) {
                    toast(getString(R.string.required, field.hint))
                    return@setOnClickListener
                }
            }

            val name = name.text.toString()
            val phone = phone.text.toString()
            val email = email.text.toString()
            val gender : Long = when {
                gender_0.isChecked -> 0
                gender_1.isChecked -> 1
                else -> 2
            }
            val dob = dob.text.toString()
            val address = address.text.toString()

//            User(firebaseAuth.currentUser!!.uid,
//                name, null, phone, email,
//                gender, address).set()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.quitting_edit_profile)
            .setMessage(R.string.quitting_edit_profile_confirm)
            .setPositiveButton(R.string.yes) { dialog, i ->
                run {
                    if (!profileUpdated) {
                        firebaseAuth.signOut()
                    }
                    super.onBackPressed()
                }
            }
            .setNegativeButton(R.string.no) { dialog, i -> }
            .setCancelable(true)
            .show()
    }
}