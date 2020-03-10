@file:Suppress("unused", "SpellCheckingInspection",
        "EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package io.jerryc05.pd_detector.core.crypto

import io.jerryc05.pd_detector.core.log.logA
import kotlin.math.ceil
import kotlin.math.floor

/*private const val VALID_CHARS =
 *        "()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
 *        "[\\]^_`abcdefghijklmnopqrstuvwxyz{|"
 */

private const val TAG = "MyBase85"

private const val OFFSET: UByte = 40u // VALID_CHARS[0].toInt()

internal fun ByteArray.encodeB85(): CharArray {
  val sourceSize = this.size
  val result = CharArray(ceil(sourceSize / 4F * 5).toInt())

  var sourceIndex = 0
  var resultIndex = 0
  while (sourceIndex + 4 <= sourceSize) {
    var temp = 0u
    repeat(4) {
      temp = (temp * (UByte.MAX_VALUE + 1u)) or
              this[sourceIndex++].mapToUByte().toUInt()
    }

    var tempIndex = resultIndex + 4
    repeat(5) {
      result[tempIndex--] = ((temp % 85u) + OFFSET).toByte().toChar()
      temp /= 85u
    }
    resultIndex += 5
  }

  if (sourceIndex < sourceSize) {
    var temp = 0u
    val sourceIndex2 = sourceIndex
    while (sourceIndex < sourceSize) {
      temp = (temp shl 8) or this[sourceIndex++].mapToUByte().toUInt()
    }
    temp = temp shl (when (sourceSize - sourceIndex2) {
      1 -> 4
      2 -> 2
      3 -> 1
      else -> throw Exception("sourceSie - sourceIndex2 != 1 or 2 or 3")
    })
    when (sourceSize - sourceIndex2) {
      1 -> {
        result[resultIndex + 1] = ((temp % 85u) + OFFSET).toByte().toChar()
        temp /= 85u
        result[resultIndex + 0] = ((temp % 85u) + OFFSET).toByte().toChar()
      }
      2 -> {
        result[resultIndex + 2] = ((temp % 85u) + OFFSET).toByte().toChar()
        temp /= 85u
        result[resultIndex + 1] = ((temp % 85u) + OFFSET).toByte().toChar()
        temp /= 85u
        result[resultIndex + 0] = ((temp % 85u) + OFFSET).toByte().toChar()
      }
      3 -> {
        result[resultIndex + 3] = ((temp % 85u) + OFFSET).toByte().toChar()
        temp /= 85u
        result[resultIndex + 2] = ((temp % 85u) + OFFSET).toByte().toChar()
        temp /= 85u
        result[resultIndex + 1] = ((temp % 85u) + OFFSET).toByte().toChar()
        temp /= 85u
        result[resultIndex + 0] = ((temp % 85u) + OFFSET).toByte().toChar()
      }
      else -> throw Exception("sourceSie - sourceIndex2 != 1 or 2 or 3")
    }
  }

  return result
}

internal fun CharArray.decodeB85(): ByteArray {
  val sourceSize = this.size
  val result = ByteArray(floor(sourceSize / 5F * 4).toInt())

  var sourceIndex = 0
  var resultIndex = 0
  while (sourceIndex + 5 <= sourceSize) {
    var temp = 0u
    repeat(5) {
      temp = temp * 85u + (this[sourceIndex++].toInt().toUInt() - OFFSET)
    }

    var tempIndex = resultIndex + 3
    repeat(4) {
      result[tempIndex--] = (temp % (UByte.MAX_VALUE + 1u))
              .toUByte().mapToByte()
      temp /= (UByte.MAX_VALUE + 1u)
    }
    resultIndex += 4
  }

  if (sourceIndex < sourceSize) {
    var temp = 0u
    val sourceIndex2 = sourceIndex
    while (sourceIndex < sourceSize) {
      temp = temp * 85u + (this[sourceIndex++].toInt().toUByte() - OFFSET)
    }
    temp = temp shr (when (sourceSize - sourceIndex2) {
      2 -> 4
      3 -> 2
      4 -> 1
      else -> throw Exception("sourceSie - sourceIndex2 != 2 or 3 or 4")
    })
    when (sourceSize - sourceIndex2) {
      2 -> {
        result[resultIndex + 0] = (temp % (UByte.MAX_VALUE + 1u)).toUByte().mapToByte()
      }
      3 -> {
        result[resultIndex + 1] = (temp % (UByte.MAX_VALUE + 1u)).toUByte().mapToByte()
        temp /= (UByte.MAX_VALUE + 1u)
        result[resultIndex + 0] = (temp % (UByte.MAX_VALUE + 1u)).toUByte().mapToByte()
      }
      4 -> {
        result[resultIndex + 2] = (temp % (UByte.MAX_VALUE + 1u)).toUByte().mapToByte()
        temp /= (UByte.MAX_VALUE + 1u)
        result[resultIndex + 1] = (temp % (UByte.MAX_VALUE + 1u)).toUByte().mapToByte()
        temp /= (UByte.MAX_VALUE + 1u)
        result[resultIndex + 0] = (temp % (UByte.MAX_VALUE + 1u)).toUByte().mapToByte()
      }
      else -> throw Exception("sourceSie - sourceIndex2 != 2 or 3 or 4")
    }
  }

  return result
}

private const val startMark1 = '#'
private const val startMark2 = '$'
private const val endMark = startMark1

internal fun CharArray.wrapB85Array(): CharArray {
  val charsSize = this.size

  if (charsSize >= 3 && this[0] == startMark1 &&
          this[1] == startMark2 && this[charsSize - 1] == endMark)
    return this

  val resultSize = charsSize + 3
  val result = CharArray(resultSize)
  result[0] = startMark1
  result[1] = startMark2
  result[resultSize - 1] = endMark
  this.copyInto(result, 2)
  return result
}

internal fun CharArray.unwrapB85Array(): CharArray {
  val start = this.lastIndexOf(startMark2) + 1
  if (start < 0 || this[start - 2] != startMark1 || this[start - 1] != startMark2)
    throw Exception("Invalid start mark!")

  val end = start + this.sliceArray(start until this.size)
          .indexOf(endMark) - 1
  if (end < 0)
    throw Exception("Invalid end mark!")
  return this.sliceArray(start..end)
}

internal fun CharArray.tryUnwrapB85Array(): CharArray {
  return try {
    this.unwrapB85Array()
  } catch (e: Exception) {
    // Ignore this exception
    logA(TAG,"tryUnwrapB85Array: ", e)
    this
  }
}

private fun Byte.mapToUByte(): UByte {
  return (this - Byte.MIN_VALUE).toUByte()
}

private fun UByte.mapToByte(): Byte {
  return (this.toInt() + Byte.MIN_VALUE).toByte()
}