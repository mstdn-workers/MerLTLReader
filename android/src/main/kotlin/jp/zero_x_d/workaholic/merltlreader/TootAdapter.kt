package jp.zero_x_d.workaholic.merltlreader

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by D on 17/08/09.
 */
class TootAdapter(
        private val toots: ArrayList<Toot>
): RecyclerView.Adapter<TootAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.toot_card, parent, false)
        // set the view's size, margins, paddings and layout parameters

        val vh = ViewHolder(v as CardView)
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val toot = synchronized(toots) { toots[position] }
        holder.displayName.text = toot.displayName
        holder.content.text = toot.content
    }

    override fun getItemCount(): Int {
        return synchronized(toots) { toots.size }
    }

    class ViewHolder(cardView: CardView): RecyclerView.ViewHolder(cardView) {
        val displayName = cardView.findViewById<TextView>(R.id.account_display_name)!!
        val content = cardView.findViewById<TextView>(R.id.toot_content)!!
    }

    data class Toot(val displayName: String, val content: String)
}