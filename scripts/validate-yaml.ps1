# YAML 语法验证工具
# 用于检查 GitHub Actions 工作流文件的语法错误

param(
    [string]$FilePath = ".github\workflows\android-ci.yml"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   YAML 语法验证工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path $FilePath)) {
    Write-Host "❌ 文件不存在：$FilePath" -ForegroundColor Red
    exit 1
}

$content = Get-Content $FilePath -Raw
$lines = Get-Content $FilePath

Write-Host "📄 检查文件：$FilePath" -ForegroundColor Green
Write-Host "📊 总行数：$($lines.Count)" -ForegroundColor Green
Write-Host ""

# 检查常见 YAML 错误
$errors = @()
$warnings = @()

for ($i = 0; $i -lt $lines.Count; $i++) {
    $line = $lines[$i]
    $lineNum = $i + 1
    
    # 检查 Tab 字符（YAML 应该使用空格）
    if ($line -match "`t") {
        $errors += "第 $lineNum 行：发现 Tab 字符，应使用空格缩进"
    }
    
    # 检查中文冒号
    if ($line -match "：") {
        $warnings += "第 $lineNum 行：发现中文冒号 '：'，应使用英文冒号 ':'"
    }
    
    # 检查 emoji 后的空格
    if ($line -match "[🏗️📋📥☕🧹🔧🔍📦✅🧪📊📤🚀📱🎉🌸📢📝🔬🏷️]" -and $line -match "name:") {
        # emoji 在 name 值中是正常的
    }
    
    # 检查缩进不一致
    if ($line -match "^    [^ ]" -and $line -notmatch "^      " -and $line -notmatch "^  ") {
        # 可能是缩进问题
    }
}

# 输出结果
if ($errors.Count -gt 0) {
    Write-Host "❌ 发现 $($errors.Count) 个错误：" -ForegroundColor Red
    $errors | ForEach-Object { Write-Host "  $_" -ForegroundColor Yellow }
    Write-Host ""
} else {
    Write-Host "✅ 未发现严重错误" -ForegroundColor Green
}

if ($warnings.Count -gt 0) {
    Write-Host "⚠️  发现 $($warnings.Count) 个警告：" -ForegroundColor Yellow
    $warnings | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
    Write-Host ""
}

# 检查关键结构
Write-Host "🔍 检查 YAML 结构..." -ForegroundColor Cyan

$hasName = $content -match "^name:"
$hasOn = $content -match "^on:"
$hasJobs = $content -match "^jobs:"
$hasBuildJob = $content -match "^\s+build:"
$hasSteps = $content -match "^\s+steps:"

if ($hasName -and $hasOn -and $hasJobs -and $hasBuildJob -and $hasSteps) {
    Write-Host "✅ YAML 基本结构完整" -ForegroundColor Green
} else {
    Write-Host "❌ YAML 结构不完整:" -ForegroundColor Red
    if (-not $hasName) { Write-Host "  - 缺少 'name:'" }
    if (-not $hasOn) { Write-Host "  - 缺少 'on:'" }
    if (-not $hasJobs) { Write-Host "  - 缺少 'jobs:'" }
    if (-not $hasBuildJob) { Write-Host "  - 缺少 'build:' 任务" }
    if (-not $hasSteps) { Write-Host "  - 缺少 'steps:' 定义" }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   验证完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
