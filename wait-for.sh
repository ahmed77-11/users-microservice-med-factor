#!/bin/sh
# Usage: ./wait-for.sh kafka:9092 -- your command

HOST_PORT="$1"
shift

HOST=$(echo "$HOST_PORT" | cut -d: -f1)
PORT=$(echo "$HOST_PORT" | cut -d: -f2)

echo "Waiting for $HOST:$PORT..."

until nc -z "$HOST" "$PORT"; do
  echo "Waiting for $HOST:$PORT..."
  sleep 2
done

echo "$HOST:$PORT is up!"
exec "$@"
