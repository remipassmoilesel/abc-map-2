#!/usr/bin/env bash

# Generate HTML documents from Markdown
# Need pandoc
# See http://pandoc.org/MANUAL.html#templates

for file in $(ls *.md); do pandoc -f markdown -t html "${file}" --template template.html -s -o "../${file%md}html"; done