package clients

class ResourceNotExistsException(msg: String = "the resource does not exists!")
  extends RuntimeException(msg)