resource "google_storage_bucket" "image_bucket" {
  project  = var.project
  location = var.region

  name                        = var.bucket_name
  uniform_bucket_level_access = true
}
