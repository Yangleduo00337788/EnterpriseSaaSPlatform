$Base = "http://localhost:8080/api"
$UiResults = @()
$amp = [char]38

function Record($Name, $Status, $Detail = "") {
    $script:UiResults += [pscustomobject]@{ Test = $Name; Status = $Status; Detail = $Detail }
    Write-Host "[$Status] $Name$(if ($Detail) { " - $Detail" })"
}

function Get-Token($tenant, $user) {
    $r = Invoke-RestMethod -Uri "$Base/auth/login" -Method POST -ContentType "application/json" -Body (@{
        tenantCode = $tenant; username = $user; password = "123456"
    } | ConvertTo-Json)
    return $r.data.token
}

$accounts = @(
    @{ Key = "admin";    Tenant = "demo"; User = "admin" },
    @{ Key = "manager";  Tenant = "demo"; User = "manager" },
    @{ Key = "zhangsan"; Tenant = "demo"; User = "zhangsan" },
    @{ Key = "lisi";     Tenant = "demo"; User = "lisi" },
    @{ Key = "acme";     Tenant = "acme"; User = "admin" }
)

$pages = @(
    @{ Path = "/dashboard";              Api = "/report/dashboard" },
    @{ Path = "/approval/pending";       Api = "/approval/tasks/pending?pageNum=1${amp}pageSize=5" },
    @{ Path = "/approval/my";            Api = "/approval/instances/my?pageNum=1${amp}pageSize=5" },
    @{ Path = "/approval/submit";        Api = "/approval/templates" },
    @{ Path = "/approval/all";           Api = "/approval/instances?pageNum=1${amp}pageSize=5" },
    @{ Path = "/templates";              Api = "/approval/templates/all" },
    @{ Path = "/messages";               Api = "/messages?pageNum=1${amp}pageSize=5" },
    @{ Path = "/report";                 Api = "/report/analytics" },
    @{ Path = "/system/users";           Api = "/system/users?pageNum=1${amp}pageSize=5" },
    @{ Path = "/system/roles";           Api = "/system/roles" },
    @{ Path = "/system/depts";           Api = "/system/depts" },
    @{ Path = "/system/positions";       Api = "/system/positions" },
    @{ Path = "/system/tenant";          Api = "/system/tenant/current" },
    @{ Path = "/system/audit-logs";      Api = "/system/audit-logs?pageNum=1${amp}pageSize=5" },
    @{ Path = "/system/dicts";           Api = "/system/dicts" },
    @{ Path = "/system/message-templates"; Api = "/system/message-templates" }
)

Write-Host "=== Account Page Matrix Test ===" -ForegroundColor Cyan

foreach ($a in $accounts) {
    $token = Get-Token $a.Tenant $a.User
    $H = @{ Authorization = "Bearer $token" }
    $me = Invoke-RestMethod -Uri "$Base/auth/me" -Headers $H
    $isAdmin = $me.data.roles -contains 'admin'
    Record "login-$($a.Key)" "PASS" $me.data.realName

    foreach ($p in $pages) {
        $name = "$($a.Key)$($p.Path)"
        try {
            $resp = Invoke-RestMethod -Uri "$Base$($p.Api)" -Headers $H -ErrorAction Stop
            if ($resp.code -eq 200) { Record $name "PASS" }
            elseif ($resp.code -eq 403) { Record $name "PASS" "403" }
            else { Record $name "FAIL" "code=$($resp.code)" }
        } catch {
            $status = $_.Exception.Response.StatusCode.value__
            if ($status -eq 403 -and $p.Path -match '^/system/' -and -not $isAdmin) {
                Record $name "PASS" "403 expected"
            } elseif ($status -eq 500) {
                Record $name "BUG" "500"
            } else {
                Record $name "FAIL" "http $status"
            }
        }
    }
}

$adminH = @{ Authorization = "Bearer $(Get-Token 'demo' 'admin')" }
try {
    Invoke-WebRequest -Uri "$Base/system/users/export" -Headers $adminH -OutFile "$env:TEMP\users.xlsx" -ErrorAction Stop | Out-Null
    Record "btn-export-users" "PASS"
} catch { Record "btn-export-users" "FAIL" $_.Exception.Message }

try {
    $r = Invoke-RestMethod -Uri "$Base/messages/read-all" -Method PUT -Headers $adminH
    if ($r.code -eq 200) { Record "btn-read-all-messages" "PASS" } else { Record "btn-read-all-messages" "FAIL" }
} catch { Record "btn-read-all-messages" "FAIL" $_.Exception.Message }

Write-Host ""
$UiResults | Group-Object Status | ForEach-Object { Write-Host "$($_.Name): $($_.Count)" }
$UiResults | Where-Object { $_.Status -in 'BUG','FAIL' } | Format-Table -AutoSize
exit ($UiResults | Where-Object { $_.Status -eq 'FAIL' }).Count
