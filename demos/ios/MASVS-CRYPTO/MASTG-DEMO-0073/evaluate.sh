jq -s 'add | map(select(.refname | test("^sym[.]imp[.](rand|srand|drand48)")))' output.json > evaluation.json
