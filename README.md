# gradle-cmake-plugin

It was created with reference to https://github.com/gradle/native-samples/tree/master/cpp/cmake-library

# Supported

* CMake generate
  - Task: `gradlew cmakeDebug`
  - Task: `gradlew cmakeRelease`

* CMake build
  - There is no dependency on `make` using cmake build, unlike native-sample uses `make` command.
  - Task: `gradlew assembleDebug`
  - Task: `gradlew assembleRelease`

# License

Apache-2.0

See [LICENSE](./LICENSE)
