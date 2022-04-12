package com.example.homework2.customviews

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

val View.measuredWidthWithMargins: Int
    get() {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        return measuredWidth + params.rightMargin + params.leftMargin
    }
val View.measuredHeightWithMargins: Int
    get() {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        return measuredHeight + params.topMargin + params.bottomMargin
    }

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.dpToPx(dp: Int): Int {
    return this.dpToPx(dp.toFloat()).toInt()
}

fun RecyclerView.addOnPageScrollListener(onScrollToNewPage: () -> Unit) {
    var position = -1
    when (val layoutManager = layoutManager) {
        is LinearLayoutManager -> {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val visiblePosition = layoutManager.findLastVisibleItemPosition()
                    if (visiblePosition == adapter?.getLastPosition() && visiblePosition != position) {
                        onScrollToNewPage.invoke()
                        position = visiblePosition
                    }
                }
            })
        }
        else -> {
            throw IllegalStateException("Illegal layoutManager " + layoutManager.toString())
        }
    }
}

fun RecyclerView.Adapter<*>.getLastPosition(): Int {
    return itemCount - 6
}
