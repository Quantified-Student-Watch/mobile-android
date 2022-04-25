package nl.quantifiedstudent.watch.protocol

@ExperimentalUnsignedTypes
interface ProtocolCommand {
    var bytes: UByteArray
    var parameterCount: Int
}