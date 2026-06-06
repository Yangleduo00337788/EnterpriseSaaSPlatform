# Fix Flyway checksum mismatch when migrations were applied manually (checksum 0 or NULL).
# Usage: powershell -ExecutionPolicy Bypass -File scripts/fix-flyway-checksum.ps1
# Or with Maven (if available): mvn flyway:repair -pl flowcloud-admin

$ErrorActionPreference = "Stop"

# Checksums must match local migration files under flowcloud-admin/src/main/resources/db/migration/
$repairs = @{
    "5" = 269103232
    "6" = -1858946800
    "7" = -328600221
}

foreach ($version in $repairs.Keys | Sort-Object) {
    $checksum = $repairs[$version]
    Write-Host "Updating flyway_schema_history checksum for version $version -> $checksum"
    mysql -uroot -proot -e "UPDATE flowcloud.flyway_schema_history SET checksum = $checksum WHERE version = '$version';"
}

mysql -uroot -proot -e "SELECT installed_rank, version, description, checksum, success FROM flowcloud.flyway_schema_history ORDER BY installed_rank;"
Write-Host "Done. Restart flowcloud-admin."
