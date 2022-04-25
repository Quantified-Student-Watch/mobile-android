package nl.quantifiedstudent.watch.protocol

@ExperimentalUnsignedTypes
interface CommunicationProtocol {
    fun prepareMessage(command: ProtocolCommand, data: UByteArray = ubyteArrayOf()): UByteArray
}