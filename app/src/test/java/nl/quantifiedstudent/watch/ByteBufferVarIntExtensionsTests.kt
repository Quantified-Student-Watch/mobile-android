package nl.quantifiedstudent.watch

import nl.quantifiedstudent.watch.binary.getVarUInt
import nl.quantifiedstudent.watch.binary.putVarUInt
import org.junit.jupiter.api.Assertions.*
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
    fun `Given an empty buffer and value, when put VarInt with value, then buffer contains the VarInt bytes`(value: UInt, expected: ByteArray) {
        // Given
        val buffer = ByteBuffer.allocate(5)

        // When
        buffer.putVarUInt(value)

        // Then
        assertTrue(buffer.position() > 0) // buffer is written to
        assertArrayEquals(expected, buffer.array())
    }

    @ParameterizedTest
    @MethodSource("provideVarIntValues")
    fun `Given a filled buffer with VarInt, when read VarInt, then result is VarInt as UInt`(expected: UInt, value: ByteArray) {
        // Given
        val buffer = ByteBuffer.wrap(value)

        // When
        val result = buffer.getVarUInt()

        // Then
        assertEquals(5, buffer.limit())
        assertTrue(buffer.position() > 0) // buffer is read from
        assertEquals(expected, result)
    }

    companion object {
        @JvmStatic
        private fun provideVarIntValues(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(UInt.MAX_VALUE.toInt(), byteArrayOf(-1, -1, -1, -1, 15)),
                Arguments.of(Int.MAX_VALUE, byteArrayOf(-1, -1, -1, -1, 7)),
                Arguments.of(UShort.MAX_VALUE.toInt(), byteArrayOf(-1, -1, 3, 0, 0)),
                Arguments.of(Short.MAX_VALUE.toInt(), byteArrayOf(-1, -1, 1, 0, 0)),
                Arguments.of(UByte.MAX_VALUE.toInt(), byteArrayOf(-1, 1, 0, 0, 0)),
                Arguments.of(Byte.MAX_VALUE.toInt(), byteArrayOf(127, 0, 0, 0, 0)),
                Arguments.of(0, byteArrayOf(0, 0, 0, 0, 0)),
            )
        }
    }
}