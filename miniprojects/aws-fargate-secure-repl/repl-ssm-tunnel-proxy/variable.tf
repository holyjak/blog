variable "vpc_id" {
  description = "ID of the VPC to run in (and that contains the other EC2 instances we should talk to)"
}

variable "vpc_cidr_block" {
  description = "The cidr block of the VPC provided via `vpc_id`"
}

variable "subnet_id" {
  description = "The subnet of the VPC (provided via `vpc_id`) to run in"
}

variable "region" {
  default = "eu-west-1"
}
