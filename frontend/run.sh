#!/bin/bash
set -e

echo "Starting RWA QA UI...."

echo "export const environment = {
  production: $production_flag,
  apiEndpoint: '$backend_url',
  pageSizes: $page_sizes
};" > ./src/environments/environment.ts

ng serve --host 0.0.0.0 --disable-host-check
