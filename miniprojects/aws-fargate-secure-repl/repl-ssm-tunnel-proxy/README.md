repl-ssm-tunnel-proxy
=====================

Create a proxy accessible via AWS SSM that can be used to tunnel
connections from localhost to allowed ports in the VPC, primarily to allow REPL access to
services on Fargate.

Usage
-----

(1) Ensure you have `aws-ssm-tools` on you machine:

    pip3 install aws-ssm-tools

(2) Start `vaulted`, e.g.

    vaulted shell minbedrift-stage-account

(3) Connect to the proxy

    x

To find the VPC-private IP of a Fargate task go to https://eu-west-1.console.aws.amazon.com/ecs/home?region=eu-west-1#/clusters/main/tasks
-> click on the task id for the service of interest
pip3 install aws-ssm-tools

## FIXME

[   52.756286] cloud-init[2927]: yum-config-manager --save --setopt=<repoid>.skip_if_unavailable=true

[   52.768270] cloud-init[2927]: Cannot find a valid baseurl for repo: amzn2-core/2/x86_64

[   52.784791] cloud-init[2927]: Could not retrieve mirrorlist http://amazonlinux.eu-west-1.amazonaws.com/2/core/latest/x86_64/mirror.list error was

[   52.800305] cloud-init[2927]: 12: Timeout on http://amazonlinux.eu-west-1.amazonaws.com/2/core/latest/x86_64/mirror.list: (28, 'Connection timed out after 5000 milliseconds')

[
