#cloud-config
repo_update: true
repo_upgrade: all

packages:
- socat   # for port -> remote port tunnel

runcmd:
# Upgrade SSM Agent to support port forwarding as the baked in - 2.3.662 - doesn't support it (e.g. 2.3.760 does)
- [ yum, install, -y, "https://s3.amazonaws.com/ec2-downloads-windows/SSMAgent/latest/linux_amd64/amazon-ssm-agent.rpm"]
