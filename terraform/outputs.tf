output "postgres_instance_id" {
  description = "ID da instancia RDS"
  value       = aws_db_instance.postgres.id
}

output "postgres_instance_endpoint" {
  description = "Endpoint da instancia RDS (host:port)"
  value       = aws_db_instance.postgres.endpoint
}

output "postgres_instance_port" {
  description = "Porta da instancia RDS"
  value       = aws_db_instance.postgres.port
}

output "postgres_database_name" {
  description = "Nome do banco de dados"
  value       = aws_db_instance.postgres.db_name
}

output "postgres_master_username" {
  description = "Username master"
  value       = aws_db_instance.postgres.username
  sensitive   = true
}

output "postgres_security_group_id" {
  description = "ID do Security Group do RDS"
  value       = aws_security_group.rds_sg.id
}
