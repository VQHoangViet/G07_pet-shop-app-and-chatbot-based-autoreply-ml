package project.petshop.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.mlkit.nl.smartreply.*
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult.STATUS_SUCCESS
import project.petshop.R

class SmartRepliesActivity : AppCompatActivity() {
    lateinit var etSendMessage: EditText
    lateinit var tvRepliesMessage: TextView
    lateinit var btnSendMessage: Button

    lateinit var conversations: ArrayList<TextMessage>
    var userUID = "123456" //in production application its come from user uid

    lateinit var smartReplyGenerator: SmartReplyGenerator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_replies)

        etSendMessage = findViewById(R.id.etSendMessage)
        tvRepliesMessage = findViewById(R.id.tvResulf)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        conversations = ArrayList()
        smartReplyGenerator = SmartReply.getClient()

        btnSendMessage.setOnClickListener {
            val message = etSendMessage.text.toString().trim()

            conversations.add(TextMessage.createForRemoteUser(message, System.currentTimeMillis(), userUID))

            smartReplyGenerator.suggestReplies(conversations).addOnSuccessListener {
                if (it.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    tvRepliesMessage.text = "STATUS_NOT_SUPPORTED_LANGUAGE"
                } else if (it.status == STATUS_SUCCESS) {

                    var reply = ""
                    for (suggestion: SmartReplySuggestion in it.suggestions) {
                        reply = reply + suggestion.text + "\n"
                    }
                    tvRepliesMessage.text = reply
                }

            }.addOnFailureListener {
                tvRepliesMessage.text = "Error ${it.message}"
            }

        }
    }
}