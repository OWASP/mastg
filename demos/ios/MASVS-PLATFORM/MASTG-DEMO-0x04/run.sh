#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-ios-evaluate-javascript-without-content-world.yaml ./MastgTest.swift --text -o output.txt
