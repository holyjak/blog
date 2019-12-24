variable "vpc_id" {
  description = "ID of the VPC to run in (and that contains the other EC2 instances we should talk to)"
}

variable "region" {
  default = "eu-west-1"
}

variable "subnet_cidrs" {
  description = "AWS Subnet CIDR ranges"
  type        = "list"
  default     = []
}
