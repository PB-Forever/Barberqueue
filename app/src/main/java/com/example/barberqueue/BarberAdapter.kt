import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.barberqueue.Barber
import com.example.barberqueue.BarberOnClickListener
import com.example.barberqueue.R


class BarberAdapter(private val itemList: List<Barber>,private val listener: BarberOnClickListener) : RecyclerView.Adapter<BarberAdapter.MyViewHolder>() {

    private var selectedPosition = -1
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.barber_name_reserve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reserve_barber, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.radioButton.text = item.name
        holder.radioButton.isChecked = position == selectedPosition

        holder.radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && position != selectedPosition) {
                val oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                // Use post to ensure the RecyclerView has finished any ongoing layout or scroll operations
                holder.radioButton.post {
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                }
                listener.onClick(item.name)
            }
        }
    }

    override fun getItemCount() = itemList.size
}