#!/usr/bin/env bash
set -euo pipefail

# 検証用DBの catalog と orders を作り直し、テーブルと初期データを入れ直す。
# Dev Containerのappコンテナから、psqlでdbコンテナへ接続して実行する。

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

export PGHOST="${PGHOST:-db}"
export PGUSER="${PGUSER:-ec}"
export PGPASSWORD="${PGPASSWORD:-ec}"

reset_database() {
  local db_name="$1"
  local schema_file="$2"
  local data_file="$3"

  # サービスが接続していても作り直せるよう、WITH (FORCE) で接続を切る
  psql -q -v ON_ERROR_STOP=1 -h "$PGHOST" -d postgres \
    -c "DROP DATABASE IF EXISTS ${db_name} WITH (FORCE);" \
    -c "CREATE DATABASE ${db_name};"
  psql -q -v ON_ERROR_STOP=1 -h "$PGHOST" -d "$db_name" -f "$schema_file"
  psql -q -v ON_ERROR_STOP=1 -h "$PGHOST" -d "$db_name" -f "$data_file"
  echo "reset: ${db_name}"
}

reset_database "catalog" \
  "${ROOT_DIR}/catalog-service/src/main/resources/db/schema.sql" \
  "${SCRIPT_DIR}/initdb/data/catalog-data.sql"
reset_database "orders" \
  "${ROOT_DIR}/order-service/src/main/resources/db/schema.sql" \
  "${SCRIPT_DIR}/initdb/data/orders-data.sql"
