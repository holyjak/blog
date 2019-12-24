output "this_security_group_id" {
  description = "The security group of the proxy EC2 instance"
  value       = "${module.ec2_security_group.this_security_group_id}"
}
