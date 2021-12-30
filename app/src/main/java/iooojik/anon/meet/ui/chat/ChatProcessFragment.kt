package iooojik.anon.meet.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import iooojik.anon.meet.R
import iooojik.anon.meet.activity.MainActivity
import iooojik.anon.meet.data.models.MessageModel
import iooojik.anon.meet.data.models.MessagesViewModel
import iooojik.anon.meet.data.models.SeenModel
import iooojik.anon.meet.data.models.User
import iooojik.anon.meet.databinding.ChatProcessTopBarBinding
import iooojik.anon.meet.databinding.FragmentChatProcessBinding
import iooojik.anon.meet.databinding.RecyclerViewMessageItemBinding
import iooojik.anon.meet.net.sockets.ChatService
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.ui.ConfirmationBottomSheet


class ChatProcessFragment : Fragment(), ChatProcessLogic {
    private lateinit var binding: FragmentChatProcessBinding
    private lateinit var adapter: MessagesAdapter
    private lateinit var topChatBarBinding: ChatProcessTopBarBinding
    private var userListUpdateObserver: Observer<MutableList<MessageModel>> =
        Observer<MutableList<MessageModel>> {
            adapter.updateMessages(it)
            adapter.notifyItemInserted(it.size - 1)

            //recyclerView.updateUserList(userArrayList)
        }

    companion object {
        const val adapterIntentFilterName = "adapterIntentFilter"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatProcessBinding.inflate(inflater)
        binding.mainLayout.messagesRecView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessagesAdapter(layoutInflater, requireContext())
        binding.mainLayout.messagesRecView.adapter = adapter
        topChatBarBinding = (requireActivity() as MainActivity).binding.appBarMain.topChatBar
        topChatBarBinding.root.visibility = View.VISIBLE
        hideBackButton(requireActivity() as AppCompatActivity)
        blockGoBack(requireActivity(), this)
        setHasOptionsMenu(true)
        setListeners(binding, topChatBarBinding)

        Intent(requireActivity(), ChatService::class.java).also { intent ->
            val pendingIntent = requireActivity().createPendingResult(100, Intent(), 0)
            intent.putExtra("pendingIntent", pendingIntent)
            requireActivity().applicationContext.startService(intent)
        }

        MessagesViewModel.messages.observe(viewLifecycleOwner, userListUpdateObserver)
        return binding.root
    }


    override fun onResume() {
        hideToolBar(activity as MainActivity)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(mMessageReceiver, IntentFilter(adapterIntentFilterName))
        val preferencesManager = SharedPreferencesManager(requireContext())
        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        val rUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
        SocketConnections.sendStompMessage(
            "/app/seen.$rUuid",
            Gson().toJson(
                SeenModel(true)
            )
        )
        super.onResume()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val typing = intent.extras?.getBoolean("typing")
            if (typing != null && typing == true) {
                if (typing) {
                    topChatBarBinding.typingMessage.text = resources.getString(R.string.typing)
                } else {
                    topChatBarBinding.typingMessage.text = " "
                }
            }
            val endChat = intent.extras?.getBoolean("endChat")
            if (endChat != null) {
                if (endChat) {
                    findNavController().navigate(R.id.filtersFragment)
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }

    override fun onDestroyView() {
        showToolBar(activity = activity as MainActivity)
        topChatBarBinding.root.visibility = View.GONE
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.send_message -> {
                val preferencesManager = SharedPreferencesManager(requireContext())
                preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                val rUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "")
                val messageText =
                    binding.mainLayout.messageInputLayout.messageTextField.editText!!.text
                if (messageText.trim().isNotBlank()) {
                    SocketConnections.sendStompMessage(
                        "/app/send.message.$rUuid",
                        Gson().toJson(
                            MessageModel(
                                id = -1,
                                author = User(),
                                text = messageText.toString()
                            )
                        )
                    )
                    binding.mainLayout.messageInputLayout.messageTextField.editText!!.text.clear()
                }
            }
            R.id.messages_rec_view -> {
                binding.mainLayout.messageInputLayout.messageTextField.clearFocus()

            }
            R.id.exit_chat -> {
                ConfirmationBottomSheet(message = resources.getString(R.string.finish_chat_confirmation)) {
                    val prefs = SharedPreferencesManager(requireContext())
                    prefs.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
                    SocketConnections.sendStompMessage(
                        "/app/end.chat.${
                            prefs.getValue(
                                SharedPrefsKeys.CHAT_ROOM_UUID,
                                ""
                            ).toString()
                        }", Gson().toJson(User())
                    )
                    prefs.clearAll()

                }.show(requireActivity().supportFragmentManager, ConfirmationBottomSheet.TAG)
            }
        }
    }
}

class MessagesAdapter(
    private val inflater: LayoutInflater,
    private val context: Context
) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
    var messages: MutableList<MessageModel> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesAdapter.MessagesViewHolder {
        return MessagesViewHolder(RecyclerViewMessageItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MessagesAdapter.MessagesViewHolder, position: Int) {
        val msgModel = messages[position]
        //val msgModel = MessagesViewModel.messages[position]
        if (msgModel.isMine) {
            holder.itemBinding.messageText.text = msgModel.text
            holder.itemBinding.timeText.text = msgModel.date
            holder.itemBinding.otherMessageBubble.visibility = View.GONE
            if (msgModel.seen)
                holder.itemBinding.messageSeenMyBubble.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.outline_done_all_24, null
                    )
                )
            else
                holder.itemBinding.messageSeenMyBubble.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.outline_done_24, null
                    )
                )
            setBubbleWidth(holder.itemBinding.myMessageBubble, msgModel.text.length)
        } else {
            holder.itemBinding.otherMessageText.text = msgModel.text
            holder.itemBinding.otherTimeText.text = msgModel.date
            holder.itemBinding.myMessageBubble.visibility = View.GONE
            setBubbleWidth(holder.itemBinding.otherMessageBubble, msgModel.text.length)
        }
    }

    private fun setBubbleWidth(bubble: View, textLength: Int) {
        val maxWidth = Resources.getSystem().displayMetrics.widthPixels
        //if (textLength >= 40)
        bubble.layoutParams.width = (maxWidth * (textLength / 100f) + maxWidth / 8).toInt()
    }

    fun updateMessages(list: MutableList<MessageModel>) {
        messages = list
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessagesViewHolder(val itemBinding: RecyclerViewMessageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

}