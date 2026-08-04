variable "aws_region" {
  description = "Regiao AWS"
  type        = string
  default     = "us-east-1"
}

variable "terraform_state_bucket" {
  description = "Bucket S3 do tfstate da infraestrutura"
  type        = string
  default     = "fiap-14soat-fase4-jonasfschuh"
}

variable "db_cluster_identifier" {
  description = "Identificador da instancia RDS"
  type        = string
  default     = "raceforce-video-upload-db"
}

variable "db_name" {
  description = "Nome do banco de dados"
  type        = string
  default     = "video_upload_db"
}

variable "db_master_username" {
  description = "Username master do banco"
  type        = string
  default     = "postgres"
}

variable "db_master_password" {
  description = "Senha master do banco"
  type        = string
  sensitive   = true
}

variable "db_port" {
  type    = number
  default = 5432
}

variable "postgres_engine_version" {
  type    = string
  default = "16"
}

variable "postgres_instance_class" {
  type    = string
  default = "db.t3.micro"
}

variable "allocated_storage" {
  type    = number
  default = 20
}

variable "max_allocated_storage" {
  type    = number
  default = 20
}

variable "skip_final_snapshot" {
  type    = bool
  default = true
}

variable "deletion_protection" {
  type    = bool
  default = false
}

variable "db_publicly_accessible" {
  type    = bool
  default = false
}

variable "backup_retention_period" {
  type    = number
  default = 0
}

variable "preferred_backup_window" {
  type    = string
  default = "03:00-04:00"
}

variable "preferred_maintenance_window" {
  type    = string
  default = "sun:04:00-sun:05:00"
}
