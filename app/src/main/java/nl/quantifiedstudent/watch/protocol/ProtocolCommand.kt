package nl.quantifiedstudent.watch.protocol

@Deprecated("Belongs to legacy protocol architecture")
interface ProtocolCommand {
    var bytes: ByteArray
    var parameterCount: Int
}