cmake_minimum_required(VERSION 3.22.1)

project(tracerpidcheck)

add_library(
    tracerpidcheck
    SHARED
    tracerpid_check.cpp
    tracerpid_inline_syscall.cpp
    tracerpid_ptrace_self.cpp
)

find_library(log-lib log)

target_link_libraries(
    tracerpidcheck
    ${log-lib}
)
