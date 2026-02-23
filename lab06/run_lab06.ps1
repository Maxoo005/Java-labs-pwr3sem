$baseDir = $PSScriptRoot

Write-Host "Starting lab06 System..." -ForegroundColor Green

# 1. Start Tailor (Background)
Write-Host "1. Launching Tailor..."
Start-Process java -ArgumentList "-cp `"$baseDir\lab06-tailor\target\classes;$baseDir\lab06-common\target\classes`" org.example.tailor.Tailor 1099 Tailor" -WindowStyle Variable

# Give Tailor a moment to start
Start-Sleep -Seconds 2

# 2. Start Environment
Write-Host "2. Launching Environment..."
Start-Process java -ArgumentList "-cp `"$baseDir\lab06-environment\target\classes;$baseDir\lab06-common\target\classes`" org.example.EnvironmentApp localhost 1099 Tailor Environment1" -WindowStyle Variable

Start-Sleep -Seconds 1

# 3. Start Control Center
Write-Host "3. Launching Control Center..."
Start-Process java -ArgumentList "-cp `"$baseDir\lab06-control-center\target\classes;$baseDir\lab06-common\target\classes;$baseDir\flood\floodlib\target\floodlib-1.0-SNAPSHOT.jar`" org.example.ControlCenterApp localhost 1099 Tailor ControlCenter1" -WindowStyle Variable

Start-Sleep -Seconds 1

# 4. Start Retention Basin
Write-Host "4. Launching Retention Basin..."
Start-Process java -ArgumentList "-cp `"$baseDir\lab06-retension-basin\target\classes;$baseDir\lab06-common\target\classes`" org.example.RetensionBasinApp localhost 1099 Tailor Basin1 River1 ControlCenter1" -WindowStyle Variable

Start-Sleep -Seconds 1

# 5. Start River Section
Write-Host "5. Launching River Section..."
# River1 flows TO Basin1 (optional), connects to Environment1
Start-Process java -ArgumentList "-cp `"$baseDir\lab06-river-section\target\classes;$baseDir\lab06-common\target\classes`" org.example.RiverSectionApp localhost 1099 Tailor River1 Basin1 Environment1" -WindowStyle Variable


Write-Host "All components launched!" -ForegroundColor Cyan
Write-Host "Press Enter to exit this launcher..."
Read-Host
