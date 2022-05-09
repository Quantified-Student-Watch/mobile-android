package nl.quantifiedstudent.watch.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nl.quantifiedstudent.watch.protocol.BluetoothProtocolCollection
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkBluetoothProtocol

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalUnsignedTypes
class BluetoothModule {

    // When new protocols are introduced, add them in the list below
    @Provides
    fun provideBluetoothProtocolCollection(): BluetoothProtocolCollection = BluetoothProtocolCollection(
        listOf(
            HuaweiLinkBluetoothProtocol()
        )
    )
}