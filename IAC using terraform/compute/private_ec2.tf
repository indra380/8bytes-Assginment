# resource "aws_instance" "private-servers" {
#   count                       = var.env == "Production" ? 3 : 1
#   ami                         = lookup(var.amis, var.aws_region)
#   instance_type               = var.instance_type
#   key_name                    = var.key_name
#   iam_instance_profile = var.iam_instance_profile
#   subnet_id                   = element(var.private_subnets, count.index)
#   vpc_security_group_ids      = [var.sg_id]
#   associate_public_ip_address = false
#   tags = {
#     Name        = "${var.vpc_name}-Private-Server-${count.index + 1}"
#     environment = "${var.env}"
#     Project = "${local.pname}"
#   }
#     user_data = <<-EOF
#   	#!/bin/bash
#     sudo apt update
#     sudo apt install nginx -y
#     sudo apt install git -y
#     sudo git clone -b DevOpsB29 https://github.com/mavrick202/webhooktesting.git
#     sudo rm -rf /var/www/html/index.nginx-debian.html
#     sudo cp webhooktesting/index.html /var/www/html/index.nginx-debian.html
#     sudo cp webhooktesting/style.css /var/www/html/style.css
#     sudo cp webhooktesting/scorekeeper.js /var/www/html/scorekeeper.js
#   	#echo "<div><h1>${var.vpc_name}-Private-Server-${count.index + 1}</h1></div>" >> /var/www/html/index.nginx-debian.html
#     sed -i '29i <center><div id="container"><h1>${var.vpc_name}-Private-Server-${count.index + 1}</h1></div></center>' /var/www/html/index.nginx-debian.html
#   EOF
# }