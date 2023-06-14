# Design Document

This document contains justification for design decisions.

## Loggers

To be able to log all behaviour without excessive printing, as well as to be able to log different kinds of behaviour separately, I wrote `ca.mta.iottestbed.tools.Logger` and an implementation `ca.mta.iottestbed.tools.BufferedLogger`. Different classes include an optional constructor that takes a `Logger`, and objects created with a `Logger` will log events to it.

## Facade Design Pattern

`ca.mta.iottestbed.network.Connection` and `ca.mta.iottestbed.network.Listener` follow the "facade" pattern, and are facades for `java.net.Socket` and `java.net.ServerSocket`, respectively.