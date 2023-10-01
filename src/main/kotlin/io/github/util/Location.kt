package io.github.util

import java.text.DecimalFormat

data class Location(var x: Double, var y: Double, var z: Double) {
    operator fun plus(other: Location) = Location(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun minus(other: Location) = Location(this.x - other.x, this.y - other.y, this.z - other.z)
    operator fun times(value: Double) = Location(this.x * value, this.y * value, this.z * value)

    fun add(other: Location) {
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }

    override fun toString(): String {
        val decimalFormat = DecimalFormat("0.000")
        return "x: ${decimalFormat.format(x)}, y: ${decimalFormat.format(y)}, z: ${decimalFormat.format(z)},"
    }
}
