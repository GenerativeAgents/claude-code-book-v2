#!/usr/bin/env bash
set -euo pipefail

# 既知の制約：
# 実行時に組み立てるSQL-ID（例：StockDaoの "UPD_STOCK_" + action）は
# 文字列リテラルとしてソースに現れないため、このスクリプトでは見つけられない。

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

if [ "$#" -ne 1 ] || [ -z "$1" ]; then
  echo "使い方: $(basename "$0") <SQL-ID>" >&2
  exit 2
fi

SQL_ID="$1"

cd "$ROOT_DIR"

found=0

for sql_file in */src/main/resources/sql/"${SQL_ID}".sql; do
  if [ -f "$sql_file" ]; then
    printf '%s\tsql-file\t%s\t1\n' "$SQL_ID" "$sql_file"
    found=1
  fi
done

for java_dir in */src/main/java; do
  if [ -d "$java_dir" ]; then
    while IFS=: read -r file line _rest; do
      printf '%s\tjava-literal\t%s\t%s\n' "$SQL_ID" "$file" "$line"
      found=1
    done < <(grep -rn -F "\"${SQL_ID}\"" "$java_dir" || true)
  fi
done

if [ "$found" -eq 1 ]; then
  exit 0
else
  exit 1
fi
