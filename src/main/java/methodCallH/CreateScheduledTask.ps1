# CreateScheduledTask.ps1

# 任务名称
$taskName = "CheckWiFiConnection"

# 获取PowerShell脚本的完整路径
$scriptPath = "C:\work\to\CheckWifi.ps1"

# 创建触发器，每15分钟运行一次
$trigger = New-ScheduledTaskTrigger -RepetitionInterval (New-TimeSpan -Minutes 15) -RepeatIndefinitely -AtStartup

# 创建操作，运行PowerShell脚本无窗口模式
$action = New-ScheduledTaskAction -Execute "powershell.exe" -Argument "-WindowStyle Hidden -NoProfile -ExecutionPolicy Bypass -File `"$scriptPath`""

# 创建任务主体
$principal = New-ScheduledTaskPrincipal -UserId "SYSTEM" -LogonType ServiceAccount -RunLevel Highest
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable

# 注册计划任务
Register-ScheduledTask -TaskName $taskName -Trigger $trigger -Action $action -Principal $principal -Settings $settings

Write-Host "Scheduled task '$taskName' created successfully."
