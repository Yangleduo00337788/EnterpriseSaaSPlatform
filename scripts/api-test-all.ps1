$Base = "http://localhost:8080/api"
$Results = @()
$amp = [char]38

function Test-Api {
    param(
        [string]$Name,
        [string]$Method = "GET",
        [string]$Path,
        [hashtable]$Headers = @{},
        [object]$Body = $null,
        [int[]]$ExpectCodes = @(200),
        [scriptblock]$Assert = $null
    )
    try {
        $params = @{
            Uri = if ($Path.StartsWith("http")) { $Path } else { "$Base$Path" }
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
            ErrorAction = "Stop"
        }
        if ($Body) { $params.Body = ($Body | ConvertTo-Json -Depth 8 -Compress) }
        $resp = Invoke-RestMethod @params
        if ($resp.code -notin $ExpectCodes) { throw "code=$($resp.code) msg=$($resp.message)" }
        if ($Assert) { & $Assert $resp }
        $script:Results += [pscustomobject]@{ Test = $Name; Status = "PASS"; Detail = "" }
        Write-Host "[PASS] $Name" -ForegroundColor Green
        return $resp
    } catch {
        $detail = $_.Exception.Message
        if ($_.ErrorDetails.Message) { $detail = $_.ErrorDetails.Message }
        $script:Results += [pscustomobject]@{ Test = $Name; Status = "FAIL"; Detail = $detail }
        Write-Host "[FAIL] $Name - $detail" -ForegroundColor Red
        return $null
    }
}

function Login-User {
    param([string]$Tenant, [string]$User, [string]$Pass)
    $r = Test-Api -Name "login:$User" -Method POST -Path "/auth/login" -Body @{
        tenantCode = $Tenant; username = $User; password = $Pass
    } -Assert { param($x) if (-not $x.data.token) { throw "no token" } }
    if (-not $r) { return $null }
    return @{ Authorization = "Bearer $($r.data.token)" }
}

Write-Host "=== FlowCloud Full API Test ===" -ForegroundColor Cyan

# --- Auth ---
$adminH = Login-User "demo" "admin" "123456"
if (-not $adminH) { $Results | Format-Table -AutoSize; exit 1 }

Test-Api -Name "auth-me" -Path "/auth/me" -Headers $adminH -Assert {
    param($r) if (-not $r.data.username) { throw "no username" }
}

# --- Report ---
Test-Api -Name "report-dashboard" -Path "/report/dashboard" -Headers $adminH
Test-Api -Name "report-analytics" -Path "/report/analytics" -Headers $adminH -Assert {
    param($r) if ($null -eq $r.data.trend) { throw "trend missing" }
}

# --- System: users ---
Test-Api -Name "users-page" -Path "/system/users?pageNum=1${amp}pageSize=10" -Headers $adminH
Test-Api -Name "users-options" -Path "/system/users/options" -Headers $adminH

# --- System: depts ---
Test-Api -Name "depts-tree" -Path "/system/depts" -Headers $adminH

# --- System: roles ---
Test-Api -Name "roles-list" -Path "/system/roles" -Headers $adminH
Test-Api -Name "roles-options" -Path "/system/roles/options" -Headers $adminH
Test-Api -Name "permissions-tree" -Path "/system/permissions/tree" -Headers $adminH

# --- System: tenant ---
Test-Api -Name "tenant-current" -Path "/system/tenant/current" -Headers $adminH

# --- System: dicts ---
Test-Api -Name "dicts-list" -Path "/system/dicts" -Headers $adminH
Test-Api -Name "dicts-by-code" -Path "/system/dicts/code/approval_category" -Headers $adminH

# --- System: positions ---
Test-Api -Name "positions-list" -Path "/system/positions" -Headers $adminH

# --- System: audit ---
$start = (Get-Date).AddDays(-30).ToString("yyyy-MM-ddTHH:mm:ss")
$end = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
Test-Api -Name "audit-logs" -Path "/system/audit-logs?startTime=$start${amp}endTime=$end${amp}pageNum=1${amp}pageSize=5" -Headers $adminH

# --- Messages ---
Test-Api -Name "messages-list" -Path "/messages?pageNum=1${amp}pageSize=5" -Headers $adminH
Test-Api -Name "messages-unread" -Path "/messages/unread-count" -Headers $adminH
Test-Api -Name "messages-read-all" -Method PUT -Path "/messages/read-all" -Headers $adminH

# --- Message templates ---
Test-Api -Name "message-templates" -Path "/system/message-templates" -Headers $adminH

# --- Approval templates ---
Test-Api -Name "templates-published" -Path "/approval/templates" -Headers $adminH -Assert {
    param($r) if ($r.data.Count -lt 1) { throw "no templates" }
}
Test-Api -Name "templates-all" -Path "/approval/templates/all" -Headers $adminH
$tplDetail = Test-Api -Name "templates-detail" -Path "/approval/templates/1" -Headers $adminH
Test-Api -Name "templates-versions" -Path "/approval/templates/1/versions" -Headers $adminH

# --- Approval instances ---
Test-Api -Name "instances-my" -Path "/approval/instances/my?pageNum=1${amp}pageSize=5" -Headers $adminH
Test-Api -Name "instances-all" -Path "/approval/instances?pageNum=1${amp}pageSize=5" -Headers $adminH
Test-Api -Name "instances-detail" -Path "/approval/instances/1" -Headers $adminH

# --- Approval tasks (manager) ---
$mgrH = Login-User "demo" "manager" "123456"
if ($mgrH) {
    Test-Api -Name "tasks-pending-manager" -Path "/approval/tasks/pending?pageNum=1${amp}pageSize=5" -Headers $mgrH
    Test-Api -Name "tasks-handled-manager" -Path "/approval/tasks/handled?pageNum=1${amp}pageSize=5" -Headers $mgrH
}

# --- Approval tasks (employee) ---
$empH = Login-User "demo" "zhangsan" "123456"
if ($empH) {
    Test-Api -Name "instances-my-zhangsan" -Path "/approval/instances/my?pageNum=1${amp}pageSize=5" -Headers $empH
    Test-Api -Name "templates-for-submit" -Path "/approval/templates" -Headers $empH
}

# --- Attachments list ---
Test-Api -Name "attachments-list" -Path "/attachments?bizType=approval${amp}bizId=1" -Headers $adminH

# --- Role-restricted endpoints (expect 403 for employee) ---
if ($empH) {
    Test-Api -Name "instances-all-denied-employee" -Path "/approval/instances?pageNum=1${amp}pageSize=5" -Headers $empH -ExpectCodes @(403)
}

# --- Manager has dashboard/analytics (dashboard perm) but not template manage ---
if ($mgrH) {
    Test-Api -Name "report-dashboard-allowed-manager" -Path "/report/dashboard" -Headers $mgrH
    Test-Api -Name "report-analytics-allowed-manager" -Path "/report/analytics" -Headers $mgrH
    Test-Api -Name "templates-all-denied-manager" -Path "/approval/templates/all" -Headers $mgrH -ExpectCodes @(403)
}

Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Cyan
$Results | Format-Table -AutoSize -Wrap
$fail = ($Results | Where-Object { $_.Status -eq "FAIL" }).Count
$pass = $Results.Count - $fail
Write-Host "Total: $($Results.Count), Pass: $pass, Fail: $fail"
exit $fail
