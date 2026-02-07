variable "project" {
  type = string
}

variable "region" {
  type = string
}

variable "image_buckets" {
  type = list(string)
}

variable "project_members" {
  type = list(string)
}

variable "project_security_admin" {
  type = list(string)
}

variable "image_tag" {
  type = string
}

variable "docker_file" {
  type = string
}