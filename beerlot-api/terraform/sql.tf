resource "google_sql_database_instance" "beerlot_database_instance" {
  name             = "beerlot-database-instance"
  database_version = "POSTGRES_14"
  region           = var.region

  settings {
    tier = "db-f1-micro"
  }
  deletion_protection = true
}

resource "google_sql_database" "beerlot_database" {
  name     = "beerlot_db"
  instance = google_sql_database_instance.beerlot_database_instance.name
}