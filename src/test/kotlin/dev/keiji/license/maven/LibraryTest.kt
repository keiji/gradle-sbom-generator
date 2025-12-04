package dev.keiji.license.maven

import dev.keiji.license.maven.gradle.entity.Library
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LibraryTest {

    @Test
    fun parseTest1() {
        val expected = Library("com.google.android.gms", "play-services-wearable", "18.0.0", 0)
        val data = "+--- com.google.android.gms:play-services-wearable:18.0.0"
        val actual = Library.parseLine(data)

        assertEquals(expected, actual)
    }

    @Test
    fun parseTest2() {
        val expected = Library("com.google.j2objc", "j2objc-annotations", "1.3", 5)
        val data = "|    |    |    |    |    \\--- com.google.j2objc:j2objc-annotations:1.3"
        val actual = Library.parseLine(data)

        assertEquals(expected, actual)
    }

    @Test
    fun parseTest3() {
        val expected = Library("androidx.compose.ui", "ui-graphics", "1.5.0-alpha03", 1)
        val data = "|    +--- androidx.compose.ui:ui-graphics:1.4.3 -> 1.5.0-alpha03 (c)\n"
        val actual = Library.parseLine(data)

        assertEquals(expected, actual)
    }

    @Test
    fun parseTest4() {
        val expected = Library("androidx.compose.ui", "ui", "1.5.0-alpha03", 1)
        val data = "|    +--- androidx.compose.ui:ui -> 1.5.0-alpha03"
        val actual = Library.parseLine(data)

        assertEquals(expected, actual)
    }
}
