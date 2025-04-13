cmake_minimum_required(VERSION 3.4.1)
project(oboe LANGUAGES C CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Define the library
add_library(oboe STATIC 
    # Dummy source file just to make CMake happy
    src/dummy.cpp
)

# Includes
target_include_directories(oboe PUBLIC include/)

# Link dependencies
target_link_libraries(oboe PRIVATE
    android
    log
)
