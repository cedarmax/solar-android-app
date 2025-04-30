package com.example.solar_bottom_view_navigation.ui.notifications

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_bottom_view_navigation.R
import com.google.firebase.auth.FirebaseAuth
import java.text.DateFormat
import java.util.Date
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.fragment.app.FragmentActivity


class ScheduleAdapter(
    private val items: List<NotificationsFragment.ScheduleListItem>,
    private val onDeleteComplete: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ENTRY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationsFragment.ScheduleListItem.Header -> TYPE_HEADER
            is NotificationsFragment.ScheduleListItem.Entry -> TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_schedule_entry, parent, false)
            EntryViewHolder(view, onDeleteComplete)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotificationsFragment.ScheduleListItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is NotificationsFragment.ScheduleListItem.Entry -> (holder as EntryViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            val textView = itemView as TextView
            textView.text = title
            if (title.equals("Past", ignoreCase = true)) {
                textView.setTextColor(Color.RED)
            } else {
                textView.setTextColor(Color.BLACK)
            }
        }
    }


    class EntryViewHolder(
        itemView: View,
        private val onDeleteComplete: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(entry: NotificationsFragment.ScheduleListItem.Entry) {
            val label = itemView.findViewById<TextView>(R.id.scheduleLabel)
            val time = itemView.findViewById<TextView>(R.id.scheduleTime)
            val state = itemView.findViewById<TextView>(R.id.scheduleState)
            val deleteButton = itemView.findViewById<Button>(R.id.deleteButton)

            label.text = entry.switchLabel
            time.text = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", Date(entry.timeMillis))
            state.text = if (entry.state) "On" else "Off"

            // Red text for past items
            if (entry.isPast) {
                label.setTextColor(Color.RED)
                time.setTextColor(Color.RED)
                state.setTextColor(Color.RED)
            } else {
                label.setTextColor(Color.BLACK)
                time.setTextColor(Color.BLACK)
                state.setTextColor(Color.BLACK)
            }

            deleteButton.setOnClickListener {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                val db = FirebaseFirestore.getInstance()

                db.collection("users")
                    .document(userId)
                    .collection("schedules")
                    .document(entry.docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(itemView.context, "Schedule deleted", Toast.LENGTH_SHORT).show()
                        onDeleteComplete()
                    }
                    .addOnFailureListener {
                        Toast.makeText(itemView.context, "Failed to delete schedule", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

}
