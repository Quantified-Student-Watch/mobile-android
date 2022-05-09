package nl.quantifiedstudent.watch

import nl.quantifiedstudent.watch.binary.getVarUInt
import nl.quantifiedstudent.watch.binary.putVarUInt
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.ByteBuffer
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class ByteBufferVarIntExtensionsTests {
    @ParameterizedTest
    @MethodSource("provideVarIntValues")
    fun `Given an empty buffer and value, when put VarInt with value, then buffer contains the VarInt bytes`(
        value: UInt,
        expected: ByteArray,
        expectedPosition: Int
    ) {
        // Given
        val buffer = ByteBuffer.allocate(5)

        // When
        buffer.putVarUInt(value)

        // Then
        assertEquals(expectedPosition, buffer.position())
        assertArrayEquals(expected, buffer.array())
    }

    @ParameterizedTest
    @MethodSource("provideVarIntValues")
    fun `Given a filled buffer with VarInt, when read VarInt, then result is VarInt as UInt`(
        expected: UInt,
        value: ByteArray,
        expectedPosition: Int
    ) {
        // Given
        val buffer = ByteBuffer.wrap(value)

        // When
        val result = buffer.getVarUInt()

        // Then
        assertEquals(5, buffer.limit())
        assertEquals(expectedPosition, buffer.position())
        assertEquals(expected, result)
    }

    companion object {
        @JvmStatic
        private fun provideVarIntValues(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(UInt.MAX_VALUE.toInt(), byteArrayOf(-1, -1, -1, -1, 15), 5),
                Arguments.of(Int.MAX_VALUE, byteArrayOf(-1, -1, -1, -1, 7), 5),
                Arguments.of(UShort.MAX_VALUE.toInt(), byteArrayOf(-1, -1, 3, 0, 0), 3),
                Arguments.of(Short.MAX_VALUE.toInt(), byteArrayOf(-1, -1, 1, 0, 0), 3),
                Arguments.of(UByte.MAX_VALUE.toInt(), byteArrayOf(-1, 1, 0, 0, 0), 2),
                Arguments.of(Byte.MAX_VALUE.toInt(), byteArrayOf(127, 0, 0, 0, 0), 1),
                Arguments.of(0, byteArrayOf(0, 0, 0, 0, 0), 1),
            )
        }
    }
}