
cmake_minimum_required(VERSION 3.4.1)

project(AndroidMedia)

# set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI})

# 搜索当前目录下的所有.cpp文件
aux_source_directory(src/jni SRC_LIST)

add_library(
        # lib name
        android_media
        # Sets the library as a shared library.
        SHARED
        # cpp file path
        ${SRC_LIST}
)

find_library(log-lib log)

target_link_libraries(
        android_media
        # Android native
        android
        GLESv3
        EGL
        ${log-lib}
)