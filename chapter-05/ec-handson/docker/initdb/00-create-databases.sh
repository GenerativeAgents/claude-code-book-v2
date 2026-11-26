#!/usr/bin/env bash
set -euo pipefail

# postgres公式イメージの /docker-entrypoint-initdb.d から自動実行される前提のスクリプト。
# データファイルは同じ initdb/ 配下の data/ ディレクトリに置く
# （/docker-entrypoint-initdb.d の直下ではないため、自動実行の対象にならず、このスクリプトからのみ読み込む）。

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

create_database() {
  local db_name="$1"
  local schema_file="$2"
  local data_file="$3"

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres \
    -c "CREATE DATABASE ${db_name};"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db_name" \
    -f "$schema_file"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db_name" \
    -f "$data_file"
}

create_database "catalog" "/schemas/catalog/schema.sql" "${SCRIPT_DIR}/data/catalog-data.sql"
create_database "orders" "/schemas/orders/schema.sql" "${SCRIPT_DIR}/data/orders-data.sql"
