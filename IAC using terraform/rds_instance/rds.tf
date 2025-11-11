resource "aws_db_instance" "rds_instance" {
  identifier          = var.instance_identifier
  skip_final_snapshot = var.skip_final_snapshot
  engine              = var.engine
  instance_class      = var.instance_class
  username            = var.username
  password            = var.password
  allocated_storage   = var.allocated_storage
  db_subnet_group_name = var.db_subnet_group_name

  # Additional configurations like multi-az, backup retention, etc., can be added here.
}