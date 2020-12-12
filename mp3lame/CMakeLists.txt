
cmake_minimum_required(VERSION 3.4.1)

project(mp3lame)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI})

# 搜索当前目录下的所有.cpp文件
aux_source_directory(src/cpp SRC_LIST)
aux_source_directory(src/cpp/lame SRC_LAME_LIST)

add_library(
        # lib name
        mp3lame
        # Sets the library as a shared library.
        SHARED
        # cpp file path
        ${SRC_LIST}
        ${SRC_LAME_LIST}
)

find_library(log-lib log)

target_link_libraries(
        mp3lame
        # Android native
        ${log-lib}
)