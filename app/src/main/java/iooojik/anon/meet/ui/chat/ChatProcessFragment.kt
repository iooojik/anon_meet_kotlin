package iooojik.anon.meet.ui.chat

import android.content.*
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import iooojik.anon.meet.R
import iooojik.anon.meet.activity.MainActivity
import iooojik.anon.meet.data.models.*
import iooojik.anon.meet.data.models.messages.MessageModel
import iooojik.anon.meet.data.models.messages.MessageViewModel
import iooojik.anon.meet.data.models.messages.MessagesViewModel
import iooojik.anon.meet.data.models.user.User
import iooojik.anon.meet.databinding.ChatProcessTopBarBinding
import iooojik.anon.meet.databinding.FragmentChatProcessBinding
import iooojik.anon.meet.databinding.RecyclerViewMessageItemBinding
import iooojik.anon.meet.hideKeyBoard
import iooojik.anon.meet.log
import iooojik.anon.meet.net.sockets.ChatService
import iooojik.anon.meet.net.sockets.SocketConnections
import iooojik.anon.meet.shared.prefs.SharedPreferencesManager
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys
import iooojik.anon.meet.showSnackbar
import iooojik.anon.meet.ui.ConfirmationBottomSheet
import jp.wasabeef.picasso.transformations.CropCircleTransformation


class ChatProcessFragment : Fragment(), ChatProcessLogic {
    private lateinit var binding: FragmentChatProcessBinding
    private lateinit var adapter: MessagesAdapter
    private lateinit var topChatBarBinding: ChatProcessTopBarBinding

    private lateinit var rUuid: String
    private var userListUpdateObserver: Observer<MutableList<MessageModel>> =
        Observer<MutableList<MessageModel>> {
            adapter.updateMessages(it)
            adapter.notifyItemInserted(it.size - 1)
            binding.mainLayout.messagesRecView.scrollToPosition(it.size - 1)
        }

    companion object {
        const val adapterIntentFilterName = "adapterIntentFilter"
        var APPLICATION_STATUS_FLAG = 0 // 0 - on pause 1 - on resume
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SocketConnections.connectToServer(requireContext())
        binding = FragmentChatProcessBinding.inflate(inflater)
        binding.mainLayout.fragment = this
        binding.mainLayout.messageInputLayout.fragment = this

        binding.mainLayout.messagesRecView.layoutManager = MessagesLayoutManager(requireContext())
        adapter = MessagesAdapter(layoutInflater, requireContext())
        binding.mainLayout.messagesRecView.adapter = adapter

        val appBar = (requireActivity() as MainActivity).binding.appBarMain
        topChatBarBinding = appBar.topChatBar
        topChatBarBinding.fragment = this
        topChatBarBinding.root.visibility = View.VISIBLE
        topChatBarBinding.exitChat.setOnClickListener {
            onExitChatClick(it)
        }

        hideBackButton(requireActivity() as AppCompatActivity)
        blockGoBack(requireActivity(), this)
        setHasOptionsMenu(true)

        Intent(requireActivity(), ChatService::class.java).also { intent ->
            val pendingIntent = requireActivity().createPendingResult(100, Intent(), 0)
            intent.putExtra("pendingIntent", pendingIntent)
            requireActivity().applicationContext.startService(intent)
        }

        MessagesViewModel.messages.observe(viewLifecycleOwner, userListUpdateObserver)
        inputMessageTextWatcher()
        return binding.root
    }

    private fun inputMessageTextWatcher() {
        binding.mainLayout.messageInputLayout.messageTextField.editText!!.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    if (s?.toString().isNullOrBlank()) {
                        SocketConnections.sendStompMessage(
                            "/app/typing.$rUuid",
                            Gson().toJson(
                                TypingModel(false)
                            )
                        )
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s?.toString().isNullOrBlank()) {
                        SocketConnections.sendStompMessage(
                            "/app/typing.$rUuid",
                            Gson().toJson(
                                TypingModel(true)
                            )
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.toString().isNullOrBlank()) {
                        SocketConnections.sendStompMessage(
                            "/app/typing.$rUuid",
                            Gson().toJson(
                                TypingModel(false)
                            )
                        )
                    }
                }

            })
    }

    fun onEmptySpaceClick(v: View?) {
        hideKeyBoard(requireActivity(), binding.root)
        binding.mainLayout.messageInputLayout.messageTextField.clearFocus()
    }

    override fun onResume() {
        val preferencesManager = SharedPreferencesManager(requireContext())
        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        val startFragment = preferencesManager.getValue(SharedPrefsKeys.CHAT_START_UP, -1)
        if (startFragment != null) {
            if (startFragment == R.id.filtersFragment) {
                preferencesManager.clearAll()
                findNavController().navigate(R.id.action_chatProcessFragment_to_filtersFragment)
            }
        }
        hideToolBar(activity as MainActivity)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(mMessageReceiver, IntentFilter(adapterIntentFilterName))

        preferencesManager.initPreferences(SharedPrefsKeys.CHAT_PREFERENCES_NAME)
        rUuid = preferencesManager.getValue(SharedPrefsKeys.CHAT_ROOM_UUID, "") as String
        APPLICATION_STATUS_FLAG = 1
        SocketConnections.sendStompMessage(
            "/app/seen.${
                rUuid
            }", Gson().toJson(SeenModel(true))
        )
        super.onResume()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val typing = intent.extras?.getBoolean("typing")
            if (typing != null) {
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

    fun openInterlocutorProfile(view: View) {
        InterlocutorProfileBottomSheet(User()).show(requireActivity().supportFragmentManager, "tag")
    }

    fun onSendMessageClick(v: View?) {
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

    private fun onExitChatClick(v: View?) {
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

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }

    override fun onDestroyView() {
        showToolBar(activity = activity as MainActivity)
        topChatBarBinding.root.visibility = View.GONE
        topChatBarBinding.typingMessage.text = " "
        super.onDestroyView()
    }

    override fun onPause() {
        APPLICATION_STATUS_FLAG = 0
        super.onPause()
    }

    inner class MessagesAdapter(
        private val inflater: LayoutInflater,
        private val context: Context
    ) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
        var messages: MutableList<MessageModel> = mutableListOf()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MessagesAdapter.MessagesViewHolder {
            return MessagesViewHolder(
                RecyclerViewMessageItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: MessagesAdapter.MessagesViewHolder, position: Int) {
            val msgModel = messages[position]
            val messageViewModel = MessageViewModel().updateModel(msgModel)
            holder.itemBinding.message = messageViewModel

            if (msgModel.isMine) {
                setBubbleWidth(holder.itemBinding.myMessageBubbleContent, msgModel.text.length)
            } else {
                holder.itemBinding.myMessageBubble.visibility = View.GONE
                setBubbleWidth(holder.itemBinding.otherMessageBubble, msgModel.text.length)
            }
            holder.itemBinding.root.setOnLongClickListener {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("anon message content", msgModel.text)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.copied),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnLongClickListener true
            }
        }

        private fun setBubbleWidth(bubble: View, textLength: Int) {
            val maxWidth = Resources.getSystem().displayMetrics.widthPixels
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

    inner class MessagesLayoutManager(context: Context) : LinearLayoutManager(context) {
        override fun supportsPredictiveItemAnimations(): Boolean {
            return false
        }
    }
}

