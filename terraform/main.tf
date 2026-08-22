terraform {
  required_providers {
    aws = {
        source = "hashicorp/aws"
        version = "~> 5.0"
    }
    
    http = {
        source  = "hashicorp/http"
        version = "~> 3.0"
    }
  }
}

provider "aws" {
  region = var.region
}

data "http" "my_ip" {
    url = "https://ifconfig.me/ip"
}

data "aws_vpc" "default" {
    default = true
}

data "aws_subnets" "default" {
    filter {
        name   = "vpc-id"
        values = [data.aws_vpc.default.id]
    }
}

data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }


  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}

resource "aws_security_group" "crm" {
  name        = "${var.project}-sg"
  description = "CRM: SSH and Envoy, locked to my IP"
  vpc_id      = data.aws_vpc.default.id

  # Allow SSH from my IP
  ingress {
    description = "SSH from my IP"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["${chomp(data.http.my_ip.response_body)}/32"]
  }

  # all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Project = var.project
  }
}

resource "aws_instance" "crm" {
  ami           = data.aws_ami.al2023.id
  instance_type = var.InstanceType
  key_name     = var.key_name
  subnet_id     = data.aws_subnets.default.ids[0]
  vpc_security_group_ids = [aws_security_group.crm.id]

  # standard credit mode — NOT unlimited (avoids surprise CPU-burst charges)
  credit_specification {
    cpu_credits = "standard"
  }

  root_block_device {
    volume_size = 30 # GB
    volume_type = "gp3"
  }

  # the bootstrap script runs on first boot
  user_data = file("${path.module}/user-data.sh")

  tags = {
    Name    = "${var.project}-instance"
    Project = var.project
  }
}