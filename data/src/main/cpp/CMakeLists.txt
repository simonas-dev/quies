cmake_minimum_required(VERSION 3.22.1)
project("dev.simonas.quies")

add_library(
        q_secrets # name of the library
        SHARED
        q_secrets.cpp
)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(
        q_secrets
        # List libraries link to the target library
        android
        log
)
