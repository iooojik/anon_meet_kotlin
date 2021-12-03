package iooojik.anon.meet.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import ua.naiksoftware.stomp.dto.StompMessage
import java.text.SimpleDateFormat
import java.time.LocalDate
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
        return binding.root
    }

    private fun checkAndConnectToChat() {
        val prefs = SharedPreferencesManager(requireContext())
        SocketConnections.connectToServer(
            prefs.getValue(SharedPrefsKeys.TOKEN_HEADER, "").toString() + " " + prefs.getValue(SharedPrefsKeys.USER_TOKEN,
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
        if (topicMessage.payload.trim().isNotBlank()){
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
                    findNavController().popBackStack()
                    SocketConnections.resetSubscriptions()
                }
                else -> {
                    val msg = Gson().fromJson(topicMessage.payload, MessageModel::class.java)
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    msg.date = sdf.format(Date())
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
        return MessagesViewHolder(RecyclerViewMessageItemBinding.inflate(inflater))
    }

    fun addMessages(newMessages: List<MessageModel>) {
        newMessages.forEach {
            messages.add(it)
            notifyItemInserted(messages.size - 1)
        }
    }

    override fun onBindViewHolder(holder: MessagesAdapter.MessagesViewHolder, position: Int) {
        val msgModel = messages[position]
        holder.itemBinding.messageText.text = msgModel.text
        holder.itemBinding.timeText.text = msgModel.date
        log(msgModel.author.uuid)
        if (msgModel.isMine) {
            holder.itemBinding.messageLayout.background =
                AppCompatResources.getDrawable(context, R.drawable.my_message_bubble)
            holder.itemBinding.messageLayout.setHorizontalGravity(Gravity.END)
        } else {
            holder.itemBinding.messageLayout.background =
                AppCompatResources.getDrawable(context, R.drawable.other_message_bubble)
            holder.itemBinding.messageLayout.setHorizontalGravity(Gravity.START)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessagesViewHolder(val itemBinding: RecyclerViewMessageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

}