cmake_minimum_required(VERSION 3.22.1)

project(rootcheck)

add_library(rootcheck SHARED native-root-check.cpp)

find_library(log-lib log)

target_link_libraries(rootcheck ${log-lib})
