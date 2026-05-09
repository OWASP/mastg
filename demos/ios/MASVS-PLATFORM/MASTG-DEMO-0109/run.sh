#!/bin/bash

grep -nE "UIPasteboard\\.general|setItems\\(|containerURL\\(forSecurityApplicationGroupIdentifier|NSFileCoordinator|coordinate\\(writingItemAt" MastgTest_reversed.swift > output.txt
