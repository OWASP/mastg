#!/bin/bash

IPC_API_PATTERN="UIPasteboard\\.general|setItems\\(|containerURL\\(forSecurityApplicationGroupIdentifier|NSFileCoordinator|coordinate\\(writingItemAt"

grep -nE "$IPC_API_PATTERN" MastgTest_reversed.swift > output.txt
