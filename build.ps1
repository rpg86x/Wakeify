$ErrorActionPreference = 'Stop'
$toolRoot = Join-Path $PSScriptRoot '..\android-build-tools'
$config = Get-Content (Join-Path $toolRoot 'paths.json') | ConvertFrom-Json
$env:JAVA_HOME = $config.javaHome
$env:PATH = "$($config.javaHome)\bin;" + $env:PATH
$bt = Join-Path $config.androidHome 'build-tools\36.0.0'
$jar = Join-Path $config.androidHome 'platforms\android-36\android.jar'
Set-Location $PSScriptRoot
New-Item -ItemType Directory -Force build/classes, build/dex | Out-Null
function Check { if ($LASTEXITCODE -ne 0) { throw "Build mislukt: $LASTEXITCODE" } }
& "$bt\aapt2.exe" compile --dir res -o build/resources.zip; Check
& "$bt\aapt2.exe" link -o build/base.apk --manifest AndroidManifest.xml -I $jar --min-sdk-version 26 --target-sdk-version 32 --version-code 2 --version-name 0.1 build/resources.zip; Check
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
& "$env:JAVA_HOME\bin\javac.exe" -encoding UTF-8 -source 8 -target 8 -classpath $jar -d build/classes $sources; Check
$classes = Get-ChildItem build/classes -Recurse -Filter *.class | ForEach-Object FullName
& "$bt\d8.bat" --lib $jar --min-api 26 --output build/dex $classes; Check
Copy-Item build/base.apk build/unsigned.apk -Force
Push-Location build/dex
& "$env:JAVA_HOME\bin\jar.exe" uf ../unsigned.apk classes.dex; Check
Pop-Location
& "$bt\zipalign.exe" -f 4 build/unsigned.apk build/aligned.apk; Check
if (!(Test-Path build/debug.keystore)) {
 & "$env:JAVA_HOME\bin\keytool.exe" -genkeypair -keystore build/debug.keystore -storepass android -keypass android -alias androiddebugkey -dname 'CN=Wakeify Test' -keyalg RSA -validity 10000; Check
}
& "$bt\apksigner.bat" sign --ks build/debug.keystore --ks-pass pass:android --out Wakeify-0.1.apk build/aligned.apk; Check
& "$bt\apksigner.bat" verify --verbose Wakeify-0.1.apk; Check
