#!/bin/sh

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
    echo "Modyfying user user ${APP_USER} with id ${PUID}:${PGID}"
    usermod -u "${PUID}" -g "${PGID}" "${APP_USER}"
fi

# Check ownership for /config
if [ "$PUID" = "0" ]; then
  echo "Skipping ownership changes for /opt/app/config"
elif [ -e /opt/app/config ]; then
  CURRENT_UID=$(stat -c %u /opt/app/config)
  CURRENT_GID=$(stat -c %g /opt/app/config)

  if [ "$CURRENT_UID" -ne "$PUID" ] || [ "$CURRENT_GID" -ne "$PGID" ]; then
    echo "Fixing ownership of /opt/app/config"
    if ! chown -R "$PUID:$PGID" /opt/app/config 2>/dev/null; then
      echo "Warning: Could not chown /opt/app/config; continuing anyway"
    fi
  else
    echo "/opt/app/config already owned by correct UID/GID, skipping chown"
  fi
else
  echo "/opt/app/config does not exist; skipping ownership check"
fi

# Check ownership for /data
if [ "$PUID" = "0" ]; then
  echo "Skipping ownership changes for /opt/app/data"
elif [ -e /opt/app/data ]; then
  CURRENT_UID=$(stat -c %u /opt/app/data)
  CURRENT_GID=$(stat -c %g /opt/app/data)

  if [ "$CURRENT_UID" -ne "$PUID" ] || [ "$CURRENT_GID" -ne "$PGID" ]; then
    echo "Fixing ownership of /opt/app/data"
    if ! chown -R "$PUID:$PGID" /opt/app/data 2>/dev/null; then
      echo "Warning: Could not chown /opt/app/data; continuing anyway"
    fi
  else
    echo "/opt/app/data already owned by correct UID/GID, skipping chown"
  fi
else
  echo "/opt/app/data does not exist; skipping ownership check"
fi

# Ensure /opt/app/config/logs exists and is owned
if [ "$PUID" = "0" ]; then
  echo "Skipping ownership changes for /opt/app/logs"
elif [ -n "$PUID" ] && [ -n "$PGID" ]; then
  mkdir -p /opt/app/logs 2>/dev/null || true
  if [ -d /opt/app/logs ]; then
    LOG_UID=$(stat -c %u /opt/app/logs)
    LOG_GID=$(stat -c %g /opt/app/logs)
    if [ "$LOG_UID" -ne "$PUID" ] || [ "$LOG_GID" -ne "$PGID" ]; then
      echo "Fixing ownership of /opt/app/logs"
      chown -R "$PUID:$PGID" /opt/app/logs 2>/dev/null || echo "Warning: Could not chown /opt/app/logs"
    fi
  fi
fi

# Drop privileges (when asked to) if root, otherwise run as current user
if [ "$(id -u)" = "0" ] && [ "${PUID}" != "0" ]; then
  exec gosu "${APP_USER}" "$@"
else
  exec "$@"
fi