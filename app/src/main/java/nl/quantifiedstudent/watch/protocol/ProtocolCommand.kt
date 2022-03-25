package nl.quantifiedstudent.watch.protocol

interface ProtocolCommand {
    var bytes: ByteArray
    var parameterCount: Int
}