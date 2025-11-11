# Infrastructure as Code (IaC) using Terraform

This directory contains Terraform configuration files for provisioning and managing cloud infrastructure on AWS using Infrastructure as Code principles.

## 📋 Overview

This Terraform project automates the deployment of a complete AWS infrastructure stack including:
- **VPC & Networking**: Virtual Private Cloud with public and private subnets
- **Compute Resources**: EC2 instances (public and private)
- **Database**: RDS instances for persistent data storage
- **Security**: Security groups for network access control
- **Database Subnet Groups**: Custom subnet grouping for RDS placement

## 📁 Directory Structure

```
./
├── network/              # VPC and networking configuration
│   ├── vpc.tf           # VPC definition
│   ├── public_subnets.tf    # Public subnet configuration
│   ├── private_subnets.tf   # Private subnet configuration
│   ├── routing.tf       # Route tables and routing rules
│   ├── variables.tf     # Network-specific variables
│   ├── locals.tf        # Local values for network module
│   └── outputs.tf       # Network outputs
├── compute/             # EC2 instance configuration
│   ├── public_ec2.tf    # Public EC2 instances
│   ├── private_ec2.tf   # Private EC2 instances
│   ├── variables.tf     # Compute-specific variables
│   ├── locals.tf        # Local values for compute module
│   └── outputs.tf       # Compute outputs
├── sg/                  # Security groups configuration
│   ├── sg.tf            # Security group rules
│   ├── variables.tf     # SG-specific variables
│   ├── locals.tf        # Local values for security groups
│   └── outputs.tf       # SG outputs
├── rds_instance/        # RDS database configuration
│   ├── rds.tf           # RDS instance definition
│   ├── variables.tf     # RDS-specific variables
│   └── outputs.tf       # RDS outputs
├── subnet_group/        # RDS subnet group configuration
│   ├── subnet_group.tf  # DB subnet group definition
│   ├── variables.tf     # Subnet group variables
│   └── outputs.tf       # Subnet group outputs
└── README.md            # This file
```

## 🚀 Getting Started

### Prerequisites
- [Terraform](https://www.terraform.io/downloads.html) (v1.0+)
- AWS CLI configured with appropriate credentials
- AWS IAM permissions for EC2, RDS, VPC, and related resources

### Quick Start

1. **Initialize Terraform**:
   ```bash
   terraform init
   ```

2. **Validate Configuration**:
   ```bash
   terraform validate
   ```

3. **Plan Infrastructure Changes**:
   ```bash
   terraform plan -out=tfplan
   ```

4. **Apply Configuration**:
   ```bash
   terraform apply tfplan
   ```

5. **Destroy Infrastructure** (when needed):
   ```bash
   terraform destroy
   ```

## 🔧 Module Descriptions

### Network Module
Manages VPC, subnets, and routing:
- Creates a VPC with configurable CIDR block
- Provisions public and private subnets across multiple availability zones
- Sets up NAT gateways and internet gateways
- Configures route tables for proper traffic flow

### Compute Module
Manages EC2 instances:
- Deploys public EC2 instances for web tier
- Deploys private EC2 instances for application tier
- Configures security groups for instance access
- Uses auto-scaling groups for availability

### Security Groups Module
Manages network access control:
- Defines security group rules for inbound/outbound traffic
- Restricts access between layers
- Manages SSH, HTTP, HTTPS, and application-specific ports

### RDS Instance Module
Manages relational database:
- Provisions RDS instance with specified engine (MySQL, PostgreSQL, etc.)
- Configures backup and maintenance windows
- Sets up multi-AZ deployment for high availability
- Manages database parameters and options

### Subnet Group Module
Manages RDS subnet placement:
- Creates DB subnet group for RDS deployment
- Ensures RDS is deployed in private subnets
- Spans multiple availability zones for redundancy

## 📊 Variables

Each module has its own `variables.tf` file. Key variables include:
- `project_name`: Project identifier
- `environment`: Deployment environment (dev, staging, prod)
- `aws_region`: AWS region for deployment
- `vpc_cidr`: VPC CIDR block
- `instance_type`: EC2 instance type
- `db_instance_class`: RDS instance class
- `db_engine`: Database engine type

## 📤 Outputs

Outputs are defined in each module's `outputs.tf` file and include:
- VPC ID and subnet IDs
- EC2 instance IPs and IDs
- RDS endpoint and port
- Security group IDs
- Network interface details

## 🏗️ Locals

Local values are used in `locals.tf` files to:
- Create consistent naming conventions
- Compute derived values
- Reduce code duplication
- Improve maintainability

## 🔐 Security Best Practices

- Use private subnets for sensitive resources (RDS, private EC2)
- Restrict security group rules to minimal required access
- Enable encryption for RDS instances
- Use VPC endpoints for AWS service access
- Enable VPC Flow Logs for monitoring
- Regularly rotate IAM credentials
- Use AWS Systems Manager Parameter Store for secrets

## 📝 State Management

- Terraform state is stored locally by default
- For team environments, use remote state storage (S3 + DynamoDB)
- Enable state locking to prevent concurrent modifications
- Always backup state files before operations

## 🔄 Updating Infrastructure

To update any configuration:
1. Modify the relevant `.tf` file
2. Run `terraform plan` to review changes
3. Review the plan carefully
4. Run `terraform apply` to apply changes

## 🆘 Troubleshooting

- **Resource creation timeout**: Check AWS service limits and increase if needed
- **Permission denied**: Verify IAM credentials and permissions
- **State conflicts**: Use `terraform state list` and `terraform state show` to inspect state
- **Syntax errors**: Run `terraform fmt` and `terraform validate`

## 📚 Additional Resources

- [Terraform AWS Provider Documentation](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS Architecture Reference](https://aws.amazon.com/architecture/)
- [Terraform Best Practices](https://www.terraform.io/docs/cloud/guides/recommended-practices.html)

## 📄 License

This IaC configuration is provided as-is for infrastructure automation purposes.

## ✏️ Contributing

When modifying infrastructure:
- Always plan before applying
- Document changes in git commits
- Use consistent naming conventions
- Follow the existing code structure
- Test in non-production environments first
