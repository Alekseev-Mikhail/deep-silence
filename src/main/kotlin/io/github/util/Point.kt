package io.github.util

import kotlinx.serialization.Serializable

val EMPTY_POINT = Point(0, 0, 0, true)

@Serializable
data class Point(val x: Int, val y: Int, val z: Int, val isEmpty: Boolean = false) {
    override fun toString(): String {
        return "x: $x, y: $y, z: $z"
    }
}
