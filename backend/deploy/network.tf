terraform {
  required_version = "~> 1.3"
}

variable "base_domain" {
  type        = string
  description = "Your base domain"
  default = "mobilispect.com"
}

variable "name" {
  type        = string
  description = "Name of resources"
  default     = "mobilispect-network"
}

data "google_client_config" "current" {}

resource "google_compute_global_address" "default" {
  project = "mobilispect"
  name    = var.name
}

resource "google_dns_managed_zone" "default" {
  project     = "mobilispect"
  name        = var.name
  dns_name    = "${var.name}.${var.base_domain}."
  description = "DNS Zone for web application"
}

resource "google_dns_record_set" "a" {
  project      = "mobilispect"
  name         = google_dns_managed_zone.default.dns_name
  type         = "A"
  ttl          = 300
  managed_zone = google_dns_managed_zone.default.name

  rrdatas = [google_compute_global_address.default.address]
}

resource "google_dns_record_set" "cname" {
  project      = "mobilispect"
  name         = join(".", compact(["www", google_dns_record_set.a.name]))
  type         = "CNAME"
  ttl          = 300
  managed_zone = google_dns_managed_zone.default.name

  rrdatas = [google_dns_record_set.a.name]
}

resource "google_dns_record_set" "api" {
  project      = "mobilispect"
  name         = join(".", compact(["api", google_dns_record_set.a.name]))
  type         = "CNAME"
  ttl          = 300
  managed_zone = google_dns_managed_zone.default.name

  rrdatas = [google_dns_record_set.a.name]
}

output "dns_zone_name_servers" {
  value       = google_dns_managed_zone.default.name_servers
  description = "Write these virtual name servers in your base domain."
}

output "domain" {
  value = trim(google_dns_record_set.a.name, ".")
}
