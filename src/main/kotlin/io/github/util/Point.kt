package io.github.util

import kotlinx.serialization.Serializable

val EMPTY_POINT = Point(0, 0, 0)

@Serializable
data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "x: $x, y: $y, z: $z"
    }
}
