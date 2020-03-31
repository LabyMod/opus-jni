###################
# Policy settings #
###################
cmake_policy(SET CMP0091 NEW)
cmake_policy(SET CMP0048 NEW)

#################
# Project setup #
#################
project(opus-jni-native)
cmake_minimum_required(VERSION 3.1)

##############################
# Set global compile options #
##############################
if(UNIX OR MINGW)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC -fstack-protector")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -fstack-protector")
endif()

############################
# Add 3rd party libraries #
###########################
add_subdirectory(3rdparty)

################
# Requires JNI #
################
find_package(JNI REQUIRED)

#####################
# Configure project #
#####################
if(NOT DEFINED GENERATED_JNI_HEADERS_DIR)
    message(FATAL_ERROR "Please run cmake with -DGENERATED_JNI_HEADERS_DIR=/path/to/generated/headers or simply use the"
            " gradle build task")
endif()

###################
# Project sources #
###################
set(OPUS_JNI_NATIVE_SOURCES
        src/opus_jni.c)

##############
# Add target #
##############
add_library(opus-jni-native SHARED ${OPUS_JNI_NATIVE_SOURCES})
target_link_libraries(opus-jni-native PUBLIC opus ${JNI_LIBRARIES})
target_include_directories(opus-jni-native PUBLIC ${JNI_INCLUDE_DIRS} ${GENERATED_JNI_HEADERS_DIR})

if(MSVC)
    set_property(
            TARGET opus
            PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreadedDebug"
    )

    set_property(
            TARGET opus-jni-native
            PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreadedDebug"
    )
endif()