variable "subnet_ids" {
  description = "List of subnet IDs to use for the RDS subnet group"
  type        = list(string)
}

variable "subnet_group_name" {
  description = "Name of the RDS subnet group"
  type        = string
}
