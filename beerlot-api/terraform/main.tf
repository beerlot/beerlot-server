module "image_buckets" {
  source = "./modules/storage"
  project = var.project
  region = var.region
  for_each = toset(var.image_buckets)
  bucket_name = "${each.key}-images-${var.project}"
}