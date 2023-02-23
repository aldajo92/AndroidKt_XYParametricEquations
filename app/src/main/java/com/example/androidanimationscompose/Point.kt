package com.example.androidanimationscompose

import androidx.compose.ui.geometry.Offset

data class Point(val x: Float, val y: Float) {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)
    operator fun times(value: Float) = Point(x * value, y * value)
    operator fun div(value: Float) = Point(x / value, y / value)
}

fun Point.toOffset() = Offset(x, y)
fun Point.translate(a: Float, b: Float) = Point(x + a, y + b)
fun Point.translate(point: Point) = Point(x + point.x, y + point.y)
fun Point.revertTranslate(a: Float, b: Float) = Point(x - a, y - b)
fun Point.revertTranslate(point: Point) = Point(x - point.x, y - point.y)
fun Point.invertYaxis() = Point(x, -y)
operator fun Float.times(point: Point): Point = Point(point.x * this, point.y * this)