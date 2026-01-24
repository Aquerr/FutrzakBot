#!/bin/bash

set -e

export PUID="${PUID:-0}"
export PGID="${PGID:-0}"

echo "Running container as ${PUID}:${PGID}"

# Create group if it doesn't exist
if ! getent group "${APP_GROUP}" >/dev/null 2>&1; then
    groupadd -g "${PGID}" "${APP_GROUP}"
else
    groupmod -g "${PGID}" "${APP_GROUP}"
fi

# Create user if it doesn't exist
if ! id "${APP_USER}" >/dev/null 2>&1; then
  echo "Creating user ${APP_USER} with id ${PUID}:${PGID}"
    useradd \
        -u "${PUID}" \
        -g "${PGID}" \
        -s /bin/sh \
        "${APP_USER}"
else
    echo "Modifying user ${APP_USER} with id ${PUID}:${PGID}"
    usermod -u "${PUID}" -g "${PGID}" "${APP_USER}"
fi

if [ "$PUID" = "0" ]; then
  echo "Skipping ownership changes because running as root"
else
  directories=("/opt/app/config" "/opt/app/data" "/opt/app/logs")
  for i in "${directories[@]}" ; do
    echo "Checking ownership of ${i}"
    # Check ownership for /config
    if [ -e "${i}" ]; then
      CURRENT_UID=$(stat -c %u "${i}")
      CURRENT_GID=$(stat -c %g "${i}")

      if [ "$CURRENT_UID" -ne "$PUID" ] || [ "$CURRENT_GID" -ne "$PGID" ]; then
        echo "Fixing ownership of ${i}"
        if ! chown -R "$PUID:$PGID" "${i}" 2>/dev/null; then
          echo "Warning: Could not chown ${i}; continuing anyway"
        fi
      else
        echo "${i} already owned by correct UID/GID, skipping chown"
      fi
    else
      echo "${i} does not exist; skipping ownership check"
    fi
  done
fi

# Drop privileges (when asked to) if root, otherwise run as current user
if [ "$(id -u)" = "0" ] && [ "${PUID}" != "0" ]; then
  exec gosu "${APP_USER}" "$@"
else
  exec "$@"
fi