resource "aws_security_group" "allow_all" {
  name        = "${var.vpc_name}-allow-all"
  description = "Allow all inbound traffic"
  vpc_id      = var.vpc_id

  dynamic "ingress" {
    for_each = var.service_ports
    content {
      from_port   = ingress.value
      to_port     = ingress.value
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }

  dynamic "egress" {
    for_each = toset(local.ports_out)
    # Pass a list value to toset to convert it to a set, 
    # which will remove any duplicate elements and discard the ordering of the elements.
    #Good for for_each as its dont use index values. Not good for element function.
    content {
      from_port   = egress.value
      to_port     = egress.value
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
    }  

  tags = {
    Name        = "${var.vpc_name}-allow-all-sg"
    environment = "${var.environment}"
  }
}