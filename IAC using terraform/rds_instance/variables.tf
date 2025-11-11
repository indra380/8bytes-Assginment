variable "instance_identifier" {
  description = "The identifier for the RDS instance"
  type        = string
}

variable "db_subnet_group_name" {
  description = "The name of the DB subnet group"
  type        = string
}

variable "engine" {
  description = "The database engine to use"
  type        = string
  default     = "postgresql"
}

variable "instance_class" {
  description = "The instance class for the RDS instance"
  type        = string
  default     = "db.t3.micro"
}

variable "username" {
  description = "Master username for the RDS instance"
  type        = string
}

variable "password" {
  description = "Master password for the RDS instance"
  type        = string
}

variable "allocated_storage" {
  description = "The allocated storage for the RDS instance"
  type        = number
  default     = 20
}


variable "skip_final_snapshot" {}