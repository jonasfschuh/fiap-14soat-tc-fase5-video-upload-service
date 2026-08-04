resource "aws_security_group" "rds_sg" {
  name        = "${{var.db_cluster_identifier}}-sg"
  description = "Security group para RDS PostgreSQL - ${{var.db_cluster_identifier}}"
  vpc_id      = local.vpc_id

  ingress {
    description = "PostgreSQL de dentro da VPC"
    from_port   = var.db_port
    to_port     = var.db_port
    protocol    = "tcp"
    cidr_blocks = [local.vpc_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${{var.db_cluster_identifier}}-sg"
  }
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "${{var.db_cluster_identifier}}-subnet-group"
  subnet_ids = local.subnet_ids

  tags = {
    Name = "${{var.db_cluster_identifier}}-subnet-group"
  }
}

resource "aws_db_instance" "postgres" {
  identifier     = var.db_cluster_identifier
  engine         = "postgres"
  engine_version = var.postgres_engine_version
  instance_class = var.postgres_instance_class

  allocated_storage     = var.allocated_storage
  max_allocated_storage = var.max_allocated_storage
  storage_type          = "gp2"
  storage_encrypted     = true

  db_name  = var.db_name
  username = var.db_master_username
  password = var.db_master_password
  port     = var.db_port

  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  backup_retention_period = var.backup_retention_period
  backup_window           = var.preferred_backup_window
  maintenance_window      = var.preferred_maintenance_window

  skip_final_snapshot       = var.skip_final_snapshot
  final_snapshot_identifier = var.skip_final_snapshot ? null : "${{var.db_cluster_identifier}}-final-snapshot"
  deletion_protection       = var.deletion_protection
  publicly_accessible       = var.db_publicly_accessible

  copy_tags_to_snapshot        = true
  performance_insights_enabled = false

  tags = {
    Name = var.db_cluster_identifier
  }
}
