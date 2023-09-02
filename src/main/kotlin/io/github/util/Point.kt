package io.github.util

val EMPTY_POINT = Point(0, 0, 0)

data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "x: $x, y: $y, z: $z"
    }
}
