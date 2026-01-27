#!/bin/bash

echo "Searching for serialization usage..."
grep -rE "implements Serializable|@Serializable|import.*serialization|JSONObject|Gson" .
