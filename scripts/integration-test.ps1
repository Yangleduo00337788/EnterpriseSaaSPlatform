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
        [scriptblock]$Assert
    )
    try {
        $params = @{
            Uri = "$Base$Path"
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
            ErrorAction = "Stop"
        }
        if ($Body) { $params.Body = ($Body | ConvertTo-Json -Depth 6) }
        $resp = Invoke-RestMethod @params
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

Write-Host "=== FlowCloud Integration Test ===" -ForegroundColor Cyan

$login = Test-Api -Name "login" -Method POST -Path "/auth/login" -Body @{
    tenantCode = "demo"; username = "admin"; password = "123456"
} -Assert {
    param($r)
    if ($r.code -ne 200) { throw $r.message }
    if (-not $r.data.token) { throw "no token" }
    if (-not $r.data.enabledFeatures) { throw "enabledFeatures missing" }
}

if (-not $login) { $Results | Format-Table -AutoSize; exit 1 }
$token = $login.data.token
$H = @{ Authorization = "Bearer $token" }

Test-Api -Name "me" -Path "/auth/me" -Headers $H -Assert {
    param($r)
    if ($r.data.enabledFeatures.Count -lt 1) { throw "enabledFeatures empty" }
}

Test-Api -Name "report-analytics" -Path "/report/analytics" -Headers $H -Assert {
    param($r)
    if ($null -eq $r.data.trend) { throw "trend missing" }
}

Test-Api -Name "roles" -Path "/system/roles" -Headers $H -Assert {
    param($r)
    if ($r.data.Count -lt 1) { throw "no roles" }
}

Test-Api -Name "permissions-tree" -Path "/system/permissions/tree" -Headers $H
Test-Api -Name "dicts" -Path "/system/dicts" -Headers $H

$start = (Get-Date).AddDays(-30).ToString("yyyy-MM-ddTHH:mm:ss")
$end = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
$auditPath = "/system/audit-logs?startTime=$start${amp}endTime=$end${amp}pageNum=1${amp}pageSize=5"
Test-Api -Name "audit-logs-time-filter" -Path $auditPath -Headers $H

Test-Api -Name "messages" -Path "/messages?pageNum=1${amp}pageSize=5" -Headers $H
Test-Api -Name "messages-read-all" -Method PUT -Path "/messages/read-all" -Headers $H

Test-Api -Name "message-templates" -Path "/system/message-templates" -Headers $H -Assert {
    param($r)
    if ($r.data.Count -lt 1) { throw "no templates" }
}

Test-Api -Name "dashboard" -Path "/report/dashboard" -Headers $H

$zhangLogin = Invoke-RestMethod -Uri "$Base/auth/login" -Method POST -ContentType "application/json" -Body (@{
    tenantCode = "demo"; username = "zhangsan"; password = "123456"
} | ConvertTo-Json)
$zH = @{ Authorization = "Bearer $($zhangLogin.data.token)" }
$myPath = "$Base/approval/instances/my?status=pending${amp}pageNum=1${amp}pageSize=5"
$my = Invoke-RestMethod -Uri $myPath -Headers $zH
if ($my.data.records.Count -gt 0) {
    $instId = $my.data.records[0].id
    $detail = Invoke-RestMethod -Uri "$Base/approval/instances/$instId" -Headers $zH
    $pendingTask = $detail.data.tasks | Where-Object { $_.status -eq "pending" } | Select-Object -First 1
    if ($pendingTask) {
        Test-Api -Name "remind-task" -Method POST -Path "/approval/tasks/$($pendingTask.id)/remind" -Headers $zH
    } else {
        Write-Host "[SKIP] remind: no pending task" -ForegroundColor Yellow
    }
} else {
    Write-Host "[SKIP] remind: no pending submissions" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Cyan
$Results | Format-Table -AutoSize
$fail = ($Results | Where-Object { $_.Status -eq "FAIL" }).Count
Write-Host "Total: $($Results.Count), Pass: $($Results.Count - $fail), Fail: $fail"
exit $fail
