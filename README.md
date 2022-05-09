# Quantified Student - Smartwatch

Quantified Student (QS for short) Watch is a component of the Quantified Student system.
The project is intended on the one hand to collect physical data from a student using smartwatches and on the other hand to provide performance insights to a student, generated from that collected data.

To achieve said goal, it is required to have proper communication with a compatible smartwatch.
This communication may be used to gather, among other things, stress and fitness data from students.

The communication is setup with the help from an Android application, of which the source code is located in this repository.
This application handles the communication with a compatible smartwatch, which means it can request the mentioned data from the smartwatch.
That being said, the application acts as the middleman between the smartwatch and data collection endpoint.

## Features

### Multi-protocol

An architecture is set in place to support multiple communication protocols, which allows for scalability when introducing new protocols.
This means that more protocols can be added more easily to the application, which results in an increased number of students that can use the platform.

## Compatibility

### Huawei

The application implements version 2 of the Huawei Link protocol, of which documentation can be found in the [Wiki](https://github.com/quantified-student/smartwatch-mobile-android/wiki).
This means that the application is technically able to support all Huawei smartwatches which make use of this protocol.
However, to ensure reliability with the application, only tested Huawei smartwatches are considered compatible.
This list currently consists of the following smartwatches:

- Huawei Band 6

Please note that it is possible to skip this compatibility check for development or beta purposes.

## Setup

### Development environment

The recommended IDE for Android development is [Android Studio](https://developer.android.com/studio).
Android Studio contains all the necessary tools and frameworks to get started quickly and efficiently.

### Cloning

To clone the source code, execute the following command:

```
git clone https://github.com/quantified-student/smartwatch-mobile-android.git
```

## Build

The project uses [Gradle](https://docs.gradle.org/current/userguide/what_is_gradle.html) for handling dependencies and other building processes.
To build the source code, ensure that the root directory is open, then execute the following command:

```
./gradlew build
```
