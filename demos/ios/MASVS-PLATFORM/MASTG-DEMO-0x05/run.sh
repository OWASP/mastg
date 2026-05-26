#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-ios-evaluate-javascript-dom-write.yaml ./MastgTest.swift --text -o output.txt
