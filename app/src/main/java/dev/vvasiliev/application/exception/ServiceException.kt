package dev.vvasiliev.application.exception

open class ServiceException(
    override val message: String = "Service Exception"
) : Exception()

class ServiceNotBoundException(serviceClass: Class<*>) :
    ServiceException(message = "Service $serviceClass is not bound")

class ServiceFinishedWithError(serviceClass: Class<*>) :
    ServiceException(message = "Service $serviceClass was finished with exception")