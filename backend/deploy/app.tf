data "google_client_config" "mobilispect-prod" {}

provider "kubernetes" {
  host                   = "https://${google_container_cluster.mobilispect-api-prod.endpoint}"
  token                  = data.google_client_config.mobilispect-prod.access_token
  cluster_ca_certificate = base64decode(google_container_cluster.mobilispect-api-prod.master_auth[0].cluster_ca_certificate)

  ignore_annotations = [
    "^autopilot\\.gke\\.io\\/.*",
    "^cloud\\.google\\.com\\/.*"
  ]
}

resource "kubernetes_deployment_v1" "mobilispect-api-prod" {
  metadata {
    name = "mobilispect-api-prod"
  }

  spec {
    selector {
      match_labels = {
        app = "mobilispect-api-prod"
      }
    }

    template {
      metadata {
        labels = {
          app = "mobilispect-api-prod"
        }
      }

      spec {
        container {
          image = "northamerica-northeast1-docker.pkg.dev/mobilispect/mobilispect-api/mobilispect-api:0.0.10"
          name  = "mobilispect-api-prod"

          port {
            container_port = 8080
            name           = "mobilispect-api"
          }

          security_context {
            allow_privilege_escalation = false
            privileged                 = false
            read_only_root_filesystem  = false

            capabilities {
              add  = []
              drop = ["NET_RAW"]
            }
          }

          liveness_probe {
            http_get {
              path = "/"
              port = "mobilispect-api"
            }

            initial_delay_seconds = 3
            period_seconds        = 3
          }
        }

        security_context {
          run_as_non_root = true
          seccomp_profile {
            type = "RuntimeDefault"
          }
        }

        # Toleration is currently required to prevent perpetual diff:
        # https://github.com/hashicorp/terraform-provider-kubernetes/pull/2380
        toleration {
          effect   = "NoSchedule"
          key      = "kubernetes.io/arch"
          operator = "Equal"
          value    = "amd64"
        }
      }
    }
  }
}

resource "kubernetes_service_v1" "mobilispect-api-prod" {
  metadata {
    name = "mobilispect-api-prod"
    annotations = {
    }
  }

  spec {
    selector = {
      app = kubernetes_deployment_v1.mobilispect-api-prod.spec[0].selector[0].match_labels.app
    }

    ip_family_policy = "SingleStack"

    port {
      port        = 80
      target_port = kubernetes_deployment_v1.mobilispect-api-prod.spec[0].template[0].spec[0].container[0].port[0].name
    }

    type = "LoadBalancer"
  }

  depends_on = [time_sleep.wait_service_cleanup]
}

# Provide time for Service cleanup
resource "time_sleep" "wait_service_cleanup" {
  depends_on = [google_container_cluster.mobilispect-api-prod]
  destroy_duration = "180s"
}
