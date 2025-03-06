@echo off
:: 设定任务名称
set taskName=Scheduled_Shutdown
set shutdownTime=18:30
set shutdownDate=2025/03/07

:: 计算任务时间（东京时间）
echo 计划在东京时间 %shutdownDate% %shutdownTime% 强制关机...

:: 创建计划任务
schtasks /create /tn "%taskName%" /tr "shutdown /s /f /t 0" /sc once /st %shutdownTime% /sd %shutdownDate% /ru SYSTEM

:: 检查任务是否创建成功
if %errorlevel% equ 0 (
    echo 计划任务已成功创建！
) else (
    echo 计划任务创建失败，请检查权限！
)

:: 提供取消任务选项
echo.
echo 如果要取消关机任务，请运行以下命令：
echo schtasks /delete /tn "%taskName%" /f
pause
