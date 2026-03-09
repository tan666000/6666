# 快速 YAML 验证（使用 .NET）

$filePath = ".github\workflows\android-ci.yml"

Write-Host "Checking YAML file: $filePath" -ForegroundColor Cyan

try {
    $content = Get-Content $filePath -Raw
    
    # 基本检查
    $errors = @()
    
    # 1. 检查 Tab 字符
    $lines = $content -split "`n"
    for ($i = 0; $i -lt $lines.Length; $i++) {
        if ($lines[$i] -match "`t") {
            $errors += "Line $($i+1): Contains tab character"
        }
    }
    
    # 2. 检查基本结构
    if ($content -notmatch "^name:") {
        $errors += "Missing 'name:' at root level"
    }
    if ($content -notmatch "^on:") {
        $errors += "Missing 'on:' at root level"
    }
    if ($content -notmatch "^jobs:") {
        $errors += "Missing 'jobs:' at root level"
    }
    
    # 3. 检查 emoji（可能导致编码问题）
    $emojiPattern = "[\u{1F300}-\u{1F9FF}]"
    if ($content -match $emojiPattern) {
        Write-Host "Warning: File contains emoji characters (may cause encoding issues)" -ForegroundColor Yellow
    }
    
    if ($errors.Count -gt 0) {
        Write-Host "Found $($errors.Count) errors:" -ForegroundColor Red
        $errors | ForEach-Object { Write-Host "  $_" -ForegroundColor Yellow }
        exit 1
    } else {
        Write-Host "Basic YAML structure looks valid!" -ForegroundColor Green
        Write-Host "File size: $([System.IO.File]::GetLength($filePath)) bytes" -ForegroundColor Green
        Write-Host "Line count: $($lines.Length)" -ForegroundColor Green
    }
} catch {
    Write-Host "Error reading file: $_" -ForegroundColor Red
    exit 1
}
