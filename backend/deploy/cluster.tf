
terraform {
  required_version = "~> 1.3"
}

provider "google" {}

variable "region" {
  type        = string
  description = "Region where the cluster will be created."
  default     = "northamerica-northeast1"
}

variable "cluster_name" {
  type        = string
  description = "Name of the cluster"
  default     = "mobilispect"
}

resource "google_container_cluster" "default" {
  project          = "mobilispect"
  name             = var.cluster_name
  description      = "mobilispect Cluster"
  location         = var.region
  enable_autopilot = true

  ip_allocation_policy {}
}

output "region" {
  value       = var.region
  description = "Compute region"
}

output "mobilispect" {
  value       = google_container_cluster.default.name
  description = "Cluster name"
}
