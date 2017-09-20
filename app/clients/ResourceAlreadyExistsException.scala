package clients

class ResourceAlreadyExistsException(msg: String = "the resource already exists!")
  extends RuntimeException(msg)
