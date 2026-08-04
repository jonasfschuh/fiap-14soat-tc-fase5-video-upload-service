provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project   = "fiap-14soat-fase5-video-upload"
      ManagedBy = "Terraform"
      Component = "Database-VideoUpload"
    }
  }
}

data "terraform_remote_state" "infra" {
  backend = "s3"
  config = {
    bucket = var.terraform_state_bucket
    key    = "infra/terraform.tfstate"
    region = var.aws_region
  }
}

locals {
  vpc_id     = data.terraform_remote_state.infra.outputs.vpc_principal_id
  vpc_cidr   = data.terraform_remote_state.infra.outputs.vpc_principal_cidr
  subnet_ids = data.terraform_remote_state.infra.outputs.subnet_publica_ids
}
