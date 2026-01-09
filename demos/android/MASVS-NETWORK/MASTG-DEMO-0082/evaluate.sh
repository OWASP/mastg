#!/bin/bash

# Define insecure URI schemes
insecure_schemes=("http" "ws" "sip" "ldap" "ftp" "rtsp" "stun")

# Read the JSONC file and process each JSON object
jq -c '.' "$1" 2>/dev/null | while IFS= read -r line; do
    # Extract class and inputParameters
    class=$(echo "$line" | jq -r '.class')

    # Check each inputParameter value
    echo "$line" | jq -r '.inputParameters[]?.value' 2>/dev/null | while IFS= read -r value; do
        # Skip if value is "void" or empty
        if [[ "$value" == "void" || -z "$value" ]]; then
            continue
        fi

        # Check if value starts with any insecure scheme
        for scheme in "${insecure_schemes[@]}"; do
            if [[ "$value" == "$scheme"* ]]; then
                # Extract scheme name without colon
                scheme_name="${scheme}"
                echo "[!] $class was used to create an insecure URI with the scheme '$scheme_name'"
                break
            fi
        done
    done
done | sort | uniq > evaluation.txt