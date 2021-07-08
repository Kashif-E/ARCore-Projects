package com.infinity.shapefactory

import android.view.View
import android.widget.Button
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class NameViewHolder(view: View) : RecyclerViewHolder<String>(view) {
    val namebtn = view.findViewById<Button>(R.id.button)
}