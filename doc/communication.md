# Communication Between Sensors and Meters

This document outlines the mechanics of communication between sensors and meters.

## Message Format

Messages sent between entities should follow this format:

`[ID]::_::[command]::_::[arguments delimited by ::_::]`

Example of a sensor `sensor1` reporting a water reading of 1 and a power reading of 10:

`sensor1::_::report::_::w:1::_::e:2`

## Ports

Device | Sends On | Listens On
---|---|---
Meter | 5005 | 5006
Sensor | 5006 | 5005
