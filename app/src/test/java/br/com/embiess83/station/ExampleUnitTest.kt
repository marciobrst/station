package br.com.embiess83.station

import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun convert_isCorrect() {
        val decFormat = DecimalFormat("'R$' 0.00")
        val value = BigDecimal(2.43)
        val convert = decFormat.format(value)
        assertEquals("R$ 2,43", convert)
    }
}