package project.petshop.views
/** include your package here **/

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
/** fix missing imports **/
import kotlinx.android.synthetic.main.activity_sign_in.*
import project.petshop.R
import project.petshop.extensions.Extensions.toast
import project.petshop.utils.FirebaseUtils.firebaseAuth

class SignInActivity : AppCompatActivity() {
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInInputsArray = arrayOf(etSignInEmail, etSignInPassword)
        btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = etSignInEmail.text.toString().trim()
        signInPassword = etSignInPassword.text.toString().trim()
        btnSignIn.isEnabled = false
        btnCreateAccount.isEnabled = false

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        // startActivity(Intent(this, HomeActivity::class.java))
                        toast(getString(R.string.sign_in_success))
                        finish()
                    } else {
                        toast(getString(R.string.sign_in_failed))
                        btnSignIn.isEnabled = true
                        btnCreateAccount.isEnabled = true
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = getString(R.string.required, input.hint)
                }
            }
            btnSignIn.isEnabled = true
            btnCreateAccount.isEnabled = true
        }
    }
}