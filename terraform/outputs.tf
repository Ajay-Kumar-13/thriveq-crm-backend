output "public_ip" {
    description = "The public IP address of the instance"
    value       = aws_instance.crm.public_ip
}

output "ssh_command" {
    description = "The SSH command to connect to the instance"
    value       = "ssh -i ~/certs/${var.key_name}.pem ec2-user@${aws_instance.crm.public_ip}"
}

output "envoy_url" {
  description = "Where the API will be reachable once the app is running"
  value       = "http://${aws_instance.crm.public_ip}:8080"
}