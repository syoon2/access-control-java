# Design Document

This document contains justification for design decisions.

## `Logger` and `Loggable` (Observer Design Pattern)

To be able to log all behaviour without excessive printing, as well as to be able to log different kinds of behaviour separately, I wrote `ca.mta.iottestbed.logger.Logger` and an implementation `ca.mta.iottestbed.logger.BufferedLogger`. Classes that implement `ca.mta.iottestbed.logger.Loggable` can write to any number of `Logger` objects. In the Observer
pattern, `Logger` acts as a subscriber, and `Loggable` as a publisher.

## `Connection` and `Listener` (Facade Design Pattern)

`ca.mta.iottestbed.network.Connection` and `ca.mta.iottestbed.network.Listener` follow the "facade" pattern, and are facades for `java.net.Socket` and `java.net.ServerSocket`, respectively.

## Sensor Data as a Sine Wave

Power and water values are generated with the formula $C = |Asin(t)|$, where $C$ is consumption, $A$ is the maximum power/water consumption, and $t$ is the number of seconds since 00:00 GMT on January 1, 1970. This makes the data predictable, which will help with detecting attacks. 