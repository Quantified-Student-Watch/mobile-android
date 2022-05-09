package nl.quantifiedstudent.watch

import android.bluetooth.le.ScanFilter
import nl.quantifiedstudent.watch.protocol.BluetoothProtocol
import nl.quantifiedstudent.watch.protocol.BluetoothProtocolCollection
import nl.quantifiedstudent.watch.protocol.PeripheralType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class BluetoothProtocolCollectionTests {
    private val testPeripheralOne = PeripheralType.TEST_PERIPHERAL_ONE
    private val testManufacturerDataOne = mapOf(
        testPeripheralOne.manufacturerId to testPeripheralOne.manufacturerData
    )

    @Test
    fun `Given no protocols in exists, when determining protocol, then the returned protocol is null`() {
        // Given
        val protocolCollection = BluetoothProtocolCollection(emptyList())

        // When
        val protocol = protocolCollection.determineProtocol(testManufacturerDataOne)

        // Then
        assertNull(protocol)
    }


    @Test
    fun `Given a compatible protocol exists, when determining protocol, then the associated protocol is returned`() {
        // Given
        val mockBluetoothProtocol = mock<BluetoothProtocol> {
            on { compatiblePeripherals }.thenReturn(listOf(testPeripheralOne))
        }

        val protocolCollection = BluetoothProtocolCollection(listOf(mockBluetoothProtocol))

        // When
        val protocol = protocolCollection.determineProtocol(testManufacturerDataOne)

        // Then
        assertNotNull(protocol)
        assertEquals(protocol, mockBluetoothProtocol)
    }

    @Test
    fun `Given unknown manufacturer data, when determining protocol, then the returned protocol is null`() {
        // Given
        val mockBluetoothProtocol = mock<BluetoothProtocol> {
            on { compatiblePeripherals }.thenReturn(listOf(PeripheralType.TEST_PERIPHERAL_TWO))
        }

        val protocolCollection = BluetoothProtocolCollection(listOf(mockBluetoothProtocol))

        // When
        val protocol = protocolCollection.determineProtocol(testManufacturerDataOne)

        // Then
        assertNull(protocol)
    }

    @Test
    fun `Given multiple protocols exists, when determining protocol, then the compatible protocol is returned`() {
        // Given
        val mockCompatibleBluetoothProtocol = mock<BluetoothProtocol> {
            on { compatiblePeripherals }.thenReturn(listOf(testPeripheralOne))
        }

        val mockIncompatibleBluetoothProtocol = mock<BluetoothProtocol> {
            on { compatiblePeripherals }.thenReturn(emptyList())
        }

        val protocolCollection = BluetoothProtocolCollection(
            listOf(
                mockIncompatibleBluetoothProtocol,
                mockCompatibleBluetoothProtocol,
                mockIncompatibleBluetoothProtocol
            )
        )

        // When
        val protocol = protocolCollection.determineProtocol(testManufacturerDataOne)

        // Then
        assertNotNull(protocol)
        assertEquals(protocol, mockCompatibleBluetoothProtocol)
    }

    @Test
    fun `Given multiple compatible protocols exists, when build scan filters, then the list of scan filters are returned`() {
        // Given
        val mockScanFilter = mock<ScanFilter> { }
        val mockScanFilters = listOf(mockScanFilter, mockScanFilter, mockScanFilter)

        val mockBluetoothProtocol = mock<BluetoothProtocol> {
            on { createScanFilters() }.thenReturn(mockScanFilters)
        }
        val mockProtocols = listOf(mockBluetoothProtocol, mockBluetoothProtocol, mockBluetoothProtocol)

        val protocolCollection = BluetoothProtocolCollection(mockProtocols)

        // When
        val scanFilters = protocolCollection.buildScanFilters()

        // Then
        assertNotNull(scanFilters)
        assertEquals(9, scanFilters.count())
        assertTrue(scanFilters.all { it == mockScanFilter })
        verify(mockBluetoothProtocol, times(3)).createScanFilters()
    }

    @Test
    fun `Given no protocols exists in the collection, when build scan filters, then the returned list of scan filters contains no entries`() {
        // Given
        val protocolCollection = BluetoothProtocolCollection(emptyList())

        // When
        val scanFilters = protocolCollection.buildScanFilters()

        // Then
        assertNotNull(scanFilters)
        assertEquals(0, scanFilters.count())
    }
}