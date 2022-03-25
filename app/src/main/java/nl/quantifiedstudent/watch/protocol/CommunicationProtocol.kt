package nl.quantifiedstudent.watch.protocol

interface CommunicationProtocol {
    fun prepareMessage(command: ProtocolCommand, data: ByteArray = byteArrayOf()): ByteArray
}