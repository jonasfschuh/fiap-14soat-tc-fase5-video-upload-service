terraform {
  backend "s3" {
    bucket  = "fiap-14soat-fase4-jonasfschuh"
    key     = "video-upload/database/terraform.tfstate"
    region  = "us-east-1"
    encrypt = true
  }
}
