package dev.mirrorkt.metainf.services.example

import dev.mirrorkt.metainf.services.MetaInfServices

@MetaInfServices(SomeInterface2::class)
class SomeImplClass2 : SomeInterface, SomeInterface2
