package dev.joguenco.roqui.util

object StringUtils {
    private val hexchars =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    fun byte2hex(binput: ByteArray): String {
        val sb = StringBuilder(binput.size * 2)
        for (i in binput.indices) {
            val high = ((binput[i].toInt() and 0xF0) shr 4)
            val low = (binput[i].toInt() and 0x0F)
            sb.append(hexchars[high])
            sb.append(hexchars[low])
        }
        return sb.toString()
    }
}
