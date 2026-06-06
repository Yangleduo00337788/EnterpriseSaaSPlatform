$Base = "http://localhost:8080/api"
$Results = @()
$amp = [char]38

function Record($Name, $Status, $Detail = "") {
    $script:Results += [pscustomobject]@{ Test = $Name; Status = $Status; Detail = $Detail }
    $color = switch ($Status) { "PASS" { "Green" } "FAIL" { "Red" } "SKIP" { "Yellow" } default { "White" } }
    Write-Host "[$Status] $Name$(if ($Detail) { " - $Detail" })" -ForegroundColor $color
}

function Login($tenant, $user, $pass = "123456") {
    try {
        $r = Invoke-RestMethod -Uri "$Base/auth/login" -Method POST -ContentType "application/json" -Body (@{
            tenantCode = $tenant; username = $user; password = $pass
        } | ConvertTo-Json) -ErrorAction Stop
        if ($r.code -ne 200 -or -not $r.data.token) { return $null }
        return @{ Token = $r.data.token; Data = $r.data; Headers = @{ Authorization = "Bearer $($r.data.token)" } }
    } catch {
        return $null
    }
}

function Api($Name, $Method, $Path, $Headers, $Body = $null, $ExpectFail = $false) {
    try {
        $p = @{ Uri = "$Base$Path"; Method = $Method; Headers = $Headers; ContentType = "application/json"; ErrorAction = "Stop" }
        if ($Body) { $p.Body = ($Body | ConvertTo-Json -Depth 8) }
        $r = Invoke-RestMethod @p
        if ($ExpectFail) { Record $Name "FAIL" "expected failure but succeeded" } else { Record $Name "PASS" }
        return $r
    } catch {
        if ($ExpectFail) { Record $Name "PASS" "correctly denied" } else { Record $Name "FAIL" $_.Exception.Message }
        return $null
    }
}

function ApiCheckCode($Name, $Method, $Path, $Headers, $ExpectCode, $Body = $null) {
    try {
        $p = @{ Uri = "$Base$Path"; Method = $Method; Headers = $Headers; ContentType = "application/json"; ErrorAction = "Stop" }
        if ($Body) { $p.Body = ($Body | ConvertTo-Json -Depth 8) }
        $r = Invoke-RestMethod @p
        if ($r.code -eq $ExpectCode) { Record $Name "PASS" "code=$($r.code)" } else { Record $Name "FAIL" "expected $ExpectCode got $($r.code)" }
        return $r
    } catch {
        Record $Name "FAIL" $_.Exception.Message
        return $null
    }
}

Write-Host "=== FlowCloud Full Test ===" -ForegroundColor Cyan

# ---- All accounts login ----
$accounts = @(
    @{ Tenant = "demo"; User = "admin";   Role = "admin" },
    @{ Tenant = "demo"; User = "manager"; Role = "approver" },
    @{ Tenant = "demo"; User = "hr";      Role = "approver" },
    @{ Tenant = "demo"; User = "finance"; Role = "approver" },
    @{ Tenant = "demo"; User = "zhangsan";Role = "employee" },
    @{ Tenant = "demo"; User = "lisi";    Role = "employee" },
    @{ Tenant = "demo"; User = "wangwu";  Role = "employee" },
    @{ Tenant = "demo"; User = "sunqi";   Role = "employee" },
    @{ Tenant = "acme"; User = "admin";   Role = "acme-admin" }
)

$sessions = @{}
foreach ($a in $accounts) {
    $key = "$($a.Tenant)/$($a.User)"
    $s = Login $a.Tenant $a.User
    if ($s) { Record "login-$key" "PASS"; $sessions[$key] = $s } else { Record "login-$key" "FAIL" "login failed" }
}

# Wrong password
$bad = Login "demo" "admin" "wrong"
if (-not $bad) { Record "login-bad-password" "PASS" } else { Record "login-bad-password" "FAIL" "should reject" }

$admin = $sessions["demo/admin"]
$zhang = $sessions["demo/zhangsan"]
$manager = $sessions["demo/manager"]
$acme = $sessions["acme/admin"]
if (-not $admin) { $Results | Format-Table; exit 1 }

$H = $admin.Headers

# ---- Core APIs (admin) ----
Api "admin-me" GET "/auth/me" $H
Api "admin-dashboard" GET "/report/dashboard" $H
Api "admin-analytics" GET "/report/analytics" $H
Api "admin-roles" GET "/system/roles" $H
Api "admin-permissions" GET "/system/permissions/tree" $H
Api "admin-dicts" GET "/system/dicts" $H
Api "admin-users" GET "/system/users?pageNum=1${amp}pageSize=20" $H
Api "admin-depts" GET "/system/depts" $H
Api "admin-positions" GET "/system/positions" $H
Api "admin-tenant" GET "/system/tenant/current" $H
$start = (Get-Date).AddDays(-30).ToString("yyyy-MM-ddTHH:mm:ss")
$end = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
Api "admin-audit" GET "/system/audit-logs?startTime=$start${amp}endTime=$end${amp}pageNum=1${amp}pageSize=10" $H
Api "admin-msg-templates" GET "/system/message-templates" $H
Api "admin-messages" GET "/messages?pageNum=1${amp}pageSize=10" $H
Api "admin-messages-read-all" PUT "/messages/read-all" $H
Api "admin-templates-all" GET "/approval/templates/all" $H
Api "admin-templates-pub" GET "/approval/templates" $H
Api "admin-pending" GET "/approval/tasks/pending?pageNum=1${amp}pageSize=10" $H
Api "admin-handled" GET "/approval/tasks/handled?pageNum=1${amp}pageSize=10" $H
Api "admin-my" GET "/approval/instances/my?pageNum=1${amp}pageSize=10" $H
Api "admin-all-instances" GET "/approval/instances?pageNum=1${amp}pageSize=10" $H

# ---- Employee permissions (zhangsan) ----
if ($zhang) {
    $zH = $zhang.Headers
    Api "emp-me" GET "/auth/me" $zH
    Api "emp-submit-templates" GET "/approval/templates" $zH
    Api "emp-my" GET "/approval/instances/my?pageNum=1${amp}pageSize=10" $zH
    Api "emp-pending" GET "/approval/tasks/pending?pageNum=1${amp}pageSize=10" $zH
    ApiCheckCode "emp-roles-denied" GET "/system/roles" $zH 403
    ApiCheckCode "emp-users-denied" GET "/system/users?pageNum=1${amp}pageSize=10" $zH 403
    $empReport = ApiCheckCode "emp-report-access" GET "/report/analytics" $zH 200
    if ($empReport) { Record "emp-report-perm-note" "SKIP" "employee has dashboard perm so analytics allowed by design" }
}

# ---- Approver (manager) ----
if ($manager) {
    $mH = $manager.Headers
    Api "mgr-pending" GET "/approval/tasks/pending?pageNum=1${amp}pageSize=10" $mH
    Api "mgr-handled" GET "/approval/tasks/handled?pageNum=1${amp}pageSize=10" $mH
}

# ---- Cross-tenant isolation ----
if ($acme) {
    $aH = $acme.Headers
    Api "acme-me" GET "/auth/me" $aH
    $acmeMe = Invoke-RestMethod -Uri "$Base/auth/me" -Headers $aH
    if ($acmeMe.data.tenantName -match "ACME") { Record "acme-tenant-isolation" "PASS" } else { Record "acme-tenant-isolation" "FAIL" "wrong tenant" }
}

# ---- Approval flow: zhangsan submit -> manager approve ----
if ($zhang -and $manager) {
    $zH = $zhang.Headers
    $mH = $manager.Headers
    $tpls = Invoke-RestMethod -Uri "$Base/approval/templates" -Headers $zH
    if ($tpls.data.Count -gt 0) {
        $tplId = $tpls.data[0].id
        $sub = Api "flow-submit" POST "/approval/instances" $zH @{
            templateId = $tplId; title = "自动化测试-$(Get-Date -Format 'HHmmss')"; formData = '{"reason":"test"}'
        }
        if ($sub -and $sub.data) {
            $instId = $sub.data
            Api "flow-detail" GET "/approval/instances/$instId" $zH
            Start-Sleep -Seconds 1
            $pending = Invoke-RestMethod -Uri "$Base/approval/tasks/pending?pageNum=1${amp}pageSize=20" -Headers $mH
            $task = $pending.data.records | Where-Object { $_.instanceId -eq $instId } | Select-Object -First 1
            if ($task) {
                Api "flow-approve" POST "/approval/tasks/complete" $mH @{ taskId = $task.id; action = "approve"; comment = "OK" }
                Api "flow-remind" POST "/approval/tasks/$($task.id)/remind" $zH
            } else {
                Record "flow-approve" "SKIP" "no pending task for manager"
            }
        }
    } else {
        Record "flow-submit" "SKIP" "no published templates"
    }
}

# ---- User export ----
try {
    $r = Invoke-WebRequest -Uri "$Base/system/users/export" -Headers $H -OutFile "$env:TEMP\users-test.xlsx" -ErrorAction Stop
    if (Test-Path "$env:TEMP\users-test.xlsx") { Record "admin-user-export" "PASS" } else { Record "admin-user-export" "FAIL" }
} catch { Record "admin-user-export" "FAIL" $_.Exception.Message }

# ---- Template data check ----
try {
    $tplAll = Invoke-RestMethod -Uri "$Base/approval/templates/all" -Headers $H
    $badStatus = $tplAll.data | Where-Object { $null -eq $_.statusLabel -and $_.status -notin 0,1,2 }
    if ($badStatus) { Record "template-status-label" "FAIL" "invalid status on $($badStatus.Count) templates" }
    else { Record "template-status-label" "PASS" "count=$($tplAll.data.Count)" }
} catch { Record "template-status-label" "FAIL" $_.Exception.Message }

Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Cyan
$Results | Format-Table -AutoSize
$fail = ($Results | Where-Object { $_.Status -eq "FAIL" }).Count
$pass = ($Results | Where-Object { $_.Status -eq "PASS" }).Count
$skip = ($Results | Where-Object { $_.Status -eq "SKIP" }).Count
Write-Host "Total: $($Results.Count), Pass: $pass, Fail: $fail, Skip: $skip"
exit $fail
