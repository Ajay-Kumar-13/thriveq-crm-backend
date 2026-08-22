variable "region" {
    description = "The AWS region to deploy resources in"
    type        = string
    default     = "ap-south-1"
}

variable "InstanceType" {
    description = "The type of instance to use for the EC2 instance"
    type        = string
    default     = "c7i-flex.large"
}

variable "key_name" {
    description = "The name of the key pair to use for the EC2 instance"
    type        = string
}

variable "project" {
    description = "Tag prefix for the project"
    type        = string
    default    = "thriveq-crm"
}