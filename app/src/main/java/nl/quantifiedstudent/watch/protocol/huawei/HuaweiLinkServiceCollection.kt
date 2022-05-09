package nl.quantifiedstudent.watch.protocol.huawei

class HuaweiLinkServiceCollection(
    val services: Array<HuaweiLinkService> = arrayOf()
) {
    fun determineService(serviceId: Byte): HuaweiLinkService? {
        return services.firstOrNull { service -> service.serviceId == serviceId }
    }

    inline fun <reified TService : HuaweiLinkService> getService(): TService {
        return services.firstOrNull { service -> service is TService } as TService
    }
}