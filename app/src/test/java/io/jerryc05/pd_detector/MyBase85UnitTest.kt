package io.jerryc05.pd_detector

import io.jerryc05.pd_detector.core.crypto.decodeB85
import io.jerryc05.pd_detector.core.crypto.encodeB85
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class MyBase85UnitTest {

  companion object {
    const val n = Byte.SIZE_BITS * 2
  }

  @Test
  fun testBase85Size0() {
    testBase85Internal(generateRandomByteArray(0))
  }

  @Test
  fun testBase85Size1() {
    testBase85Internal(ByteArray(1))
    repeat(n) {
      testBase85Internal(generateRandomByteArray(1))
    }
  }

  @Test
  fun testBase85Size2() {
    testBase85Internal(ByteArray(2))
    repeat(n) {
      testBase85Internal(generateRandomByteArray(2))
    }
  }

  @Test
  fun testBase85Size4() {
    testBase85Internal(ByteArray(4))
    repeat(n) {
      testBase85Internal(generateRandomByteArray(4))
    }
  }

  @Test
  fun testBase85Size5() {
    testBase85Internal(ByteArray(5))
    repeat(n) {
      testBase85Internal(generateRandomByteArray(5))
    }
  }

  @Test
  fun testBase85SizeN() {
    repeat(n) {
      val n = Random.nextInt(6, n * n)
      testBase85Internal(ByteArray(n))
      repeat(n) {
        testBase85Internal(generateRandomByteArray(n))
      }
    }
  }

  private fun generateRandomByteArray(n: Int): ByteArray {
    if (n < 0)
      throw Exception("byte array size cannot be $n!")

    val result = ByteArray(n)
    if (n > 0)
      Random.nextBytes(result)

    return result
  }

  private fun testBase85Internal(bytes: ByteArray) {
    val processed = bytes.encodeB85().decodeB85()

    println(bytes.contentToString())
    println(processed.contentToString())
    println("-".repeat(Byte.SIZE_BITS * Byte.SIZE_BITS))

    assertEquals(bytes.contentToString(), processed.contentToString())
  }
}