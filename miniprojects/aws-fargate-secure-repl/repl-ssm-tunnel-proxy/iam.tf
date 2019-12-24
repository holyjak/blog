## Create an IAM Instance Profile that allows the instance to be managed by SSM
resource "aws_iam_role" "tunnel_proxy" {
  name = "tunnel_proxy"

  assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "sts:AssumeRole",
            "Principal": {
                "Service": [
                   "ec2.amazonaws.com",
                   "ssm.amazonaws.com"
                 ]
            },
            "Effect": "Allow",
            "Sid": ""
        }
    ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "instance_ssm_core" {
  role       = "${aws_iam_role.tunnel_proxy.id}"
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "tunnel_proxy" {
  name = "repl-ssm-tunnel-proxy"
  role = "${aws_iam_role.tunnel_proxy.name}"
}

data "aws_iam_policy_document" "ssm_required_for_vpc_endpoint" {
  statement {
    effect = "Allow"

    actions = [
      "s3:GetObject",
    ]

    resources = [
      "arn:aws:s3:::aws-ssm-${var.region}/*",
      "arn:aws:s3:::aws-windows-downloads-${var.region}/*",
      "arn:aws:s3:::amazon-ssm-${var.region}/*",
      "arn:aws:s3:::amazon-ssm-packages-${var.region}/*",
      "arn:aws:s3:::${var.region}-birdwatcher-prod/*",
      "arn:aws:s3:::patch-baseline-snapshot-${var.region}/*",
    ]
  }
}

resource "aws_iam_role_policy" "ssm_required_for_vpc_endpoint" {
  name   = "tunnel_proxy_ssm_required_for_vpc_endpoint"
  role   = "${aws_iam_role.tunnel_proxy.name}"
  policy = "${data.aws_iam_policy_document.ssm_required_for_vpc_endpoint.json}"
}

module "ssm-agent-policy" {
  #source  = "telia-oss/ssm-agent-policy/aws"
  #version = "1.0.0"
  source = "github.com/telia-oss/terraform-aws-ssm-agent-policy?ref=f3c4143e8ad9356cf7ccc0cf90a2e248fd277566"

  name_prefix = "tunnel_proxy_"
  role        = "${aws_iam_role.tunnel_proxy.name}"
}
