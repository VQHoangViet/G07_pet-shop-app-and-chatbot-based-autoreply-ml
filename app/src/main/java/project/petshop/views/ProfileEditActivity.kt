package project.petshop.views

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.address
import kotlinx.android.synthetic.main.activity_profile.dob
import kotlinx.android.synthetic.main.activity_profile.email
import kotlinx.android.synthetic.main.activity_profile.name
import kotlinx.android.synthetic.main.activity_profile.phone
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.view_login_dialog.*
import project.petshop.R
import project.petshop.extensions.Extensions.toast
import project.petshop.objects.User
import project.petshop.utils.FirebaseUtils
import java.text.SimpleDateFormat
import java.util.*

class ProfileEditActivity : AppCompatActivity() {
    val cal: Calendar = Calendar.getInstance()
    var uri : Uri? = null
    var profileUpdated : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Check if email edit is allowed
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey("emailEdit")) {
                if (!bundle.getBoolean("emailEdit")) {
                    email.isEnabled = false
                    email.text = Firebase.auth.currentUser!!.email
                }
            }
        }
        var old_email : String? = null

        // Update form with Firestore data
        User.get(Firebase.auth.currentUser!!.uid)
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
                old_email = user.email
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

        // Init file chooser activity
        val pickerLauncher = registerForActivityResult(
            OpenDocument()
        ) { result ->
            if (result == null) {
                return@registerForActivityResult
            }
            uri = result
            Picasso.get().load(uri).into(avatar)
        }
        avatar.setOnClickListener {
            pickerLauncher.launch(arrayOf("image/*"))
        }

        // Open date dialog when click dob
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        dob.setOnClickListener {
            // FIXME: https://stackoverflow.com/a/14933515
            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dob.setText(format.format(cal.time))
            }

            DatePickerDialog(
                this@ProfileEditActivity, date, cal
                    .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

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
            val address = address.text.toString()

            // Check if email is changed, if yes -> update email
            if (old_email != email && this.email.isEnabled) {
                val view = LayoutInflater.from(this).inflate(R.layout.view_login_dialog, root, false)
                val editText : EditText = view.findViewById(R.id.editTextPassword)
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.email_changing))
                    .setView(view)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.login)) { dialog, i ->
                        run {
                            val pass = editText.text.toString()
                            Firebase.auth
                                .signInWithEmailAndPassword(old_email!!, pass)
                                .addOnSuccessListener {
                                    it.user!!.updateEmail(email)
                                    .addOnSuccessListener { uploadData(name, phone, email, gender, address) }
                                    .addOnFailureListener {
                                        toast(getString(R.string.email_exist))
                                        it.printStackTrace()
                                        Log.e("TAG", "onCreate: " + it.message + "\n" + it::class.java.simpleName)
                                    }
                                }
                                .addOnFailureListener {
                                    toast(getString(R.string.password_retype))
                                    it.printStackTrace()
                                    Log.e("TAG", "onCreate: " + it.message + "\n" + it::class.java.simpleName)
                                }
                        }
                    }
                    .show()

            } else {
                uploadData(name, phone, email, gender, address)
            }
        }
    }

    private fun uploadData(name: String, phone: String, email: String, gender: Long, address: String) {
        User(Firebase.auth.currentUser!!.uid,
            name, cal.time, phone,
            email, gender, address).set()!!
            .addOnSuccessListener {
                if (uri != null) {
                    val ref = FirebaseUtils.storage.reference.child(
                        "avatars/" + Firebase.auth.currentUser!!.uid + ".jpg")
                    Log.i("TAG", "uploadData: " + ref.path)
                    ref.putFile(uri!!)
                }
                finish()
            }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.quitting_edit_profile)
            .setMessage(R.string.quitting_edit_profile_confirm)
            .setPositiveButton(R.string.yes) { dialog, i ->
                run {
                    if (!profileUpdated) {
                        finishAffinity()
                    }
                    super.onBackPressed()
                }
            }
            .setNegativeButton(R.string.no) { dialog, i -> }
            .setCancelable(true)
            .show()
    }
}