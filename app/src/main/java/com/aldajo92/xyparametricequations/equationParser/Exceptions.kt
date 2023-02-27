package com.aldajo92.xyparametricequations.equationParser

class BadSyntaxException(msg: String = "Bad syntax") : Exception(msg)

class DomainException(msg: String = "Domain error") : Exception(msg)

class ImaginaryException(msg: String = "Imaginary number not supported") : Exception(msg)

class BaseNotFoundException(msg: String = "Base not Found") : Exception(msg)