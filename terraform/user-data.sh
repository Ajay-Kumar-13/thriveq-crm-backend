#!/bin/bash
set -euxo pipefail

# --- install Docker + compose ---
dnf update -y
dnf install -y docker git
systemctl enable --now docker
usermod -aG docker ec2-user

# compose v2 plugin
mkdir -p /usr/local/lib/docker/cli-plugins
curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# --- swap file (cheap insurance for a 4GB box running 3 JVMs) ---
dd if=/dev/zero of=/swapfile bs=1M count=2048
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab

# --- signal that bootstrap is done ---
touch /home/ec2-user/BOOTSTRAP_DONE
chown ec2-user:ec2-user /home/ec2-user/BOOTSTRAP_DONE