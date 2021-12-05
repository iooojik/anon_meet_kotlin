package iooojik.anon.meet.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentChatProcessBinding
import iooojik.anon.meet.databinding.RecyclerViewMessageItemBinding
import iooojik.anon.meet.log
import iooojik.anon.meet.models.MessageModel
import iooojik.anon.meet.models.TypingModel
import iooojik.anon.meet.models.User
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import ua.naiksoftware.stomp.dto.StompMessage
import java.text.SimpleDateFormat
import java.util.*


class ChatProcessFragment : Fragment(), ChatProcessLogic {
    private lateinit var binding: FragmentChatProcessBinding
    private lateinit var preferencesManager: SharedPreferencesManager
    private lateinit var adapter: MessagesAdapter

    companion object {
        var ROOM_UUID = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatProcessBinding.inflate(inflater)
        binding.messagesRecView.layoutManager = LinearLayoutManager(requireContext())
        checkAndConnectToChat()
        adapter = MessagesAdapter(MessagesViewModel.messages, layoutInflater, requireContext())
        binding.messagesRecView.adapter = adapter
        hideBackButton(requireActivity() as AppCompatActivity)
        blockGoBack(requireActivity(), this)
        setHasOptionsMenu(true)
        setListeners(binding)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    private fun checkAndConnectToChat() {
        val prefs = SharedPreferencesManager(requireContext())
        SocketConnections.connectToServer(
            prefs.getValue(SharedPrefsKeys.TOKEN_HEADER, "").toString() + " " + prefs.getValue(
                SharedPrefsKeys.USER_TOKEN,
                ""
            ).toString()
        )
        preferencesManager = SharedPreferencesManager(requireContext())
        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        val roomUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
        if (roomUuid != null && roomUuid.toString().trim().isNotBlank())
            ROOM_UUID = roomUuid.toString()
        SocketConnections.connectToTopic("/topic/$ROOM_UUID/message.topic", ::onMessageReceived)
    }

    private fun onMessageReceived(topicMessage: StompMessage) {
        if (topicMessage.payload.trim().isNotBlank()) {
            when {
                topicMessage.payload.contains("typing") -> {
                    val typingModel = Gson().fromJson(topicMessage.payload, TypingModel::class.java)
                    TypingModel.mTyping = typingModel.typing
                    TypingModel.mTypingUser = typingModel.typingUser
                }
                topicMessage.payload.contains("endChat") -> {
                    val preferencesManager = SharedPreferencesManager(requireContext())
                    preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                    preferencesManager.clearAll()
                    findNavController().navigate(R.id.filtersFragment)
                    SocketConnections.resetSubscriptions()
                }
                else -> {
                    val msg = Gson().fromJson(topicMessage.payload, MessageModel::class.java)
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    msg.date = sdf.format(Date())
                    msg.isMine = User.mUuid == msg.author.uuid
                    adapter.addMessages(listOf(msg))
                    binding.messagesRecView.smoothScrollToPosition(adapter.messages.size - 1)
                }
            }
        }
    }

    override fun onDestroyView() {
        SocketConnections.resetSubscriptions()
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.send_message -> {
                val messageText = binding.messageInputLayout.messageTextField.editText!!.text
                if (messageText.trim().isNotBlank()) {
                    SocketConnections.sendStompMessage(
                        "/app/send.message.$ROOM_UUID",
                        Gson().toJson(
                            MessageModel(
                                author = User(),
                                text = messageText.toString()
                            )
                        )
                    )
                    binding.messageInputLayout.messageTextField.editText!!.text.clear()
                }
            }
            R.id.messages_rec_view -> {
                binding.messageInputLayout.messageTextField.clearFocus()

            }
        }
    }
}

class MessagesAdapter(
    val messages: MutableList<MessageModel> = mutableListOf(),
    private val inflater: LayoutInflater,
    private val context: Context
) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesAdapter.MessagesViewHolder {
        return MessagesViewHolder(RecyclerViewMessageItemBinding.inflate(inflater, parent, false))
    }

    fun addMessages(newMessages: List<MessageModel>) {
        newMessages.forEach {
            messages.add(it)
            notifyItemInserted(messages.size - 1)
        }
    }

    override fun onBindViewHolder(holder: MessagesAdapter.MessagesViewHolder, position: Int) {
        val msgModel = messages[position]
        log(msgModel.author.uuid)
        if (msgModel.isMine) {
            holder.itemBinding.messageText.text = msgModel.text
            holder.itemBinding.timeText.text = msgModel.date
            holder.itemBinding.otherMessageBubble.visibility = View.GONE
        } else {
            holder.itemBinding.otherMessageText.text = msgModel.text
            holder.itemBinding.otherTimeText.text = msgModel.date
            holder.itemBinding.myMessageBubble.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessagesViewHolder(val itemBinding: RecyclerViewMessageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

}