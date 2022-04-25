package nl.quantifiedstudent.watch.extensions

import android.util.SparseArray
import androidx.core.util.forEach

fun <E> SparseArray<E>.toMap(): Map<Int, E> {
    val map = mutableMapOf<Int, E>()
    this.forEach { key, value -> map[key] = value }

    return map.toMap()
}