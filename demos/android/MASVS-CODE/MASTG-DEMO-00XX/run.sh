#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-sql-injection-contentprovider.yml ./MastgTest_reversed.java > output.txt