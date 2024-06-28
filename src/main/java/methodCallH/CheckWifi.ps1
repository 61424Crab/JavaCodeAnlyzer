# CheckWifi.ps1

# 指定的WiFi名称
$wifiName = "Hirosawa_201_5G"

# 检查WiFi连接状态的函数
function Check-WifiConnection {
    $connectedWifi = (netsh wlan show interfaces) -match 'SSID' | Out-String
    return $connectedWifi -contains $wifiName
}

# 连接到指定WiFi的函数
function Connect-ToWifi {
    netsh wlan connect name=$wifiName
    Start-Sleep -Seconds 10 # 等待10秒以确保连接成功
}

# 30秒后强制重新启动系统的函数
function Force-Restart {
    shutdown /r /f /t 30
}

# 主逻辑
if (Check-WifiConnection) {
    #Write-Host "WiFi is connected." # 在控制台输出脚本运行状态
} else {
    #Write-Host "WiFi is not connected. Attempting to reconnect..." # 在控制台输出脚本运行状态
    $attempt = 0
    $maxAttempts = 3
    $connected = $false

    while ($attempt -lt $maxAttempts -and -not $connected) {
        $attempt++
        Connect-ToWifi
        if (Check-WifiConnection) {
            $connected = $true
            #Write-Host "Successfully reconnected to WiFi." # 在控制台输出脚本运行状态
        } else {
            #Write-Host "Attempt $attempt failed. Retrying..." # 在控制台输出脚本运行状态
        }
    }

    if (-not $connected) {
        #Write-Host "Unable to reconnect to WiFi after $maxAttempts attempts. Forcing system restart." # 在控制台输出脚本运行状态
        Force-Restart
    }
}
