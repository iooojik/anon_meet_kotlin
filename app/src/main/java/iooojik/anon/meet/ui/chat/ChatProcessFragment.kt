package iooojik.anon.meet.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.databinding.FragmentChatProcessBinding
import iooojik.anon.meet.databinding.RecyclerViewMessageItemBinding
import iooojik.anon.meet.models.MessageModel
import iooojik.anon.meet.models.User
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.net.sockets.SocketService
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys


class ChatProcessFragment : Fragment(), ChatProcessLogic {
    private lateinit var binding: FragmentChatProcessBinding
    private lateinit var adapter: MessagesAdapter

    companion object {
        const val adapterIntentFilterName = "adapterIntentFilter"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatProcessBinding.inflate(inflater)
        binding.messagesRecView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessagesAdapter(layoutInflater)
        binding.messagesRecView.adapter = adapter
        hideBackButton(requireActivity() as AppCompatActivity)
        blockGoBack(requireActivity(), this)
        setHasOptionsMenu(true)
        setListeners(binding)
        Intent(requireActivity(), SocketService::class.java).also { intent ->
            requireActivity().applicationContext.startService(intent)
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(mMessageReceiver, IntentFilter(adapterIntentFilterName))
        return binding.root
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val typing = intent.extras?.getBoolean("typing")
            if (typing != null && typing == true) {
                if (typing) {

                }else{
                    
                }
            } else if (typing == null){
                adapter.notifyItemInserted(MessagesViewModel.messages.size - 1)
            }
            val endChat = intent.extras?.getBoolean("endChat")
            if (endChat != null) {
                if (endChat) {
                    findNavController().navigate(R.id.filtersFragment)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.send_message -> {
                val preferencesManager = SharedPreferencesManager(requireContext())
                preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                val rUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
                val messageText = binding.messageInputLayout.messageTextField.editText!!.text
                if (messageText.trim().isNotBlank()) {
                    SocketConnections.sendStompMessage(
                        "/app/send.message.$rUuid",
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
    private val inflater: LayoutInflater
) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesAdapter.MessagesViewHolder {
        return MessagesViewHolder(RecyclerViewMessageItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MessagesAdapter.MessagesViewHolder, position: Int) {
        val msgModel = MessagesViewModel.messages[position]
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
        return MessagesViewModel.messages.size
    }

    inner class MessagesViewHolder(val itemBinding: RecyclerViewMessageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

}