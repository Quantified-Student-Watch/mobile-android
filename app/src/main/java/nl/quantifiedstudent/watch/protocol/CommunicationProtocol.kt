package nl.quantifiedstudent.watch.protocol

@Deprecated("Belongs to legacy protocol architecture")
interface CommunicationProtocol {
    fun prepareMessage(command: ProtocolCommand, data: ByteArray = byteArrayOf()): ByteArray
}