# Create a proxy accessible via AWS SSM that can be used to tunnel
# connections to allowed ports in the VPC, primarily to allow REPL access to
# services on Fargate
#
# To find the VPC-private IP of a Fargate task go to https://eu-west-1.console.aws.amazon.com/ecs/home?region=eu-west-1#/clusters/main/tasks
# -> click on the task id for the service of interest

## Setup of the instance

data "template_file" "cloud_config" {
  template = "${file("${path.module}/user_data/cloud_config.tpl")}"
}

data "template_cloudinit_config" "config" {
  gzip          = true
  base64_encode = true

  part {
    content_type = "text/cloud-config"
    content      = "${data.template_file.cloud_config.rendered}"
  }
}

## Access rules
module "ec2_security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "2.17.0"                                   #v3 requires tf 0.12

  name        = "ssm-tunnel-proxy"
  description = "Security group for the SSM Tunnel Proxy for REPL access"
  vpc_id      = "${var.vpc_id}"

  ingress_cidr_blocks = ["${var.vpc_cidr_block}"]

  egress_rules = ["https-443-tcp", "http-80-tcp"]

  egress_with_cidr_blocks = [
    {
      from_port   = 55555
      to_port     = 55555
      protocol    = "tcp"
      description = "Clojure REPL"
      cidr_blocks = "${var.vpc_cidr_block}"
    },
    {
      from_port   = 8081
      to_port     = 8081
      protocol    = "tcp"
      description = "Web port"
      cidr_blocks = "${var.vpc_cidr_block}"
    },
  ]
}

## The instance itself

data "aws_ami" "amazon_linux2" {
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]     # arm64 arch require a1.* instances that do not support micro
  }

  owners = ["amazon"]
}

resource "aws_instance" "tunnel_proxy" {
  ami                         = "${data.aws_ami.amazon_linux2.id}"
  instance_type               = "t3.nano"
  vpc_security_group_ids      = ["${module.ec2_security_group.this_security_group_id}"]
  subnet_id                   = "${var.subnet_id}"
  associate_public_ip_address = false
  user_data                   = "${data.template_cloudinit_config.config.rendered}"
  iam_instance_profile        = "${aws_iam_instance_profile.tunnel_proxy.name}"

  tags = {
    Name = "SSM Tunnel Proxy for REPL access"
  }
}
