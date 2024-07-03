import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.barberqueue.DateOnClickListener
import com.example.barberqueue.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DateAdapter(private val itemList: List<Date>,private val listener: DateOnClickListener) : RecyclerView.Adapter<DateAdapter.MyViewHolder>() {

    private var selectedPosition = -1
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.date_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reserve_date, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        val dateText = SimpleDateFormat("EEEE\nd MMM", Locale.getDefault()).format(item)
        holder.radioButton.text = dateText
        holder.radioButton.isChecked = position == selectedPosition
        holder.radioButton.isEnabled = position%2 == 0
        holder.radioButton.refreshDrawableState()

        holder.radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && position != selectedPosition) {
                val oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                // Use post to ensure the RecyclerView has finished any ongoing layout or scroll operations
                holder.radioButton.post {
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                }
                listener.onClick(item)
            }
        }
    }

    override fun getItemCount() = itemList.size
}
