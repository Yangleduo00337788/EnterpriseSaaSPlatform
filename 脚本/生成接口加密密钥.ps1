param(
    [string]$OutputDir = "",
    [int]$KeySize = 2048
)

$ErrorActionPreference = "Stop"

function Get-Text {
    param(
        [Parameter(Mandatory = $true)]
        [int[]]$Codes
    )

    return (-join ($Codes | ForEach-Object { [char]$_ }))
}

function Get-RepoRoot {
    $scriptDir = $PSScriptRoot
    if ([string]::IsNullOrWhiteSpace($scriptDir)) {
        $scriptDir = Split-Path -Parent $PSCommandPath
    }
    if ([string]::IsNullOrWhiteSpace($scriptDir)) {
        throw "Unable to resolve script directory."
    }
    return Split-Path -Parent $scriptDir
}

function To-FileUri {
    param(
        [Parameter(Mandatory = $true)]
        [string]$PathValue
    )

    $fullPath = [System.IO.Path]::GetFullPath($PathValue)
    return "file:/" + ($fullPath -replace "\\", "/")
}

function Test-OpenSsl {
    $command = Get-Command openssl -ErrorAction SilentlyContinue
    return $null -ne $command
}

function Invoke-OpenSsl {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & openssl @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "OpenSSL failed: openssl $($Arguments -join ' ')"
    }
}

function Assert-Java {
    $command = Get-Command java -ErrorAction SilentlyContinue
    if ($null -eq $command) {
        throw "java was not found. Please install JDK 21+ or install OpenSSL."
    }
}

function Invoke-JavaKeyGen {
    param(
        [Parameter(Mandatory = $true)]
        [string]$PrivateKeyPath,
        [Parameter(Mandatory = $true)]
        [string]$PublicKeyPath,
        [Parameter(Mandatory = $true)]
        [int]$Size
    )

    Assert-Java

    $tempJavaFile = Join-Path ([System.IO.Path]::GetTempPath()) ("FlowCloudKeyGenerator_" + [System.Guid]::NewGuid().ToString("N") + ".java")
    $javaSource = @"
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGeneratorApp {
    public static void main(String[] args) throws Exception {
        int keySize = Integer.parseInt(args[0]);
        Path privateKeyPath = Path.of(args[1]);
        Path publicKeyPath = Path.of(args[2]);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize);
        KeyPair keyPair = generator.generateKeyPair();

        writePem(privateKeyPath, "PRIVATE KEY", keyPair.getPrivate().getEncoded());
        writePem(publicKeyPath, "PUBLIC KEY", keyPair.getPublic().getEncoded());
    }

    private static void writePem(Path path, String type, byte[] content) throws Exception {
        Base64.Encoder encoder = Base64.getMimeEncoder(64, System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        String pem = "-----BEGIN " + type + "-----" + System.lineSeparator()
                + encoder.encodeToString(content) + System.lineSeparator()
                + "-----END " + type + "-----" + System.lineSeparator();
        Files.writeString(path, pem, StandardCharsets.UTF_8);
    }
}
"@

    try {
        Set-Content -LiteralPath $tempJavaFile -Value $javaSource -Encoding ASCII
        & java $tempJavaFile $Size $PrivateKeyPath $PublicKeyPath
        if ($LASTEXITCODE -ne 0) {
            throw "Java key generation failed."
        }
    }
    finally {
        if (Test-Path -LiteralPath $tempJavaFile) {
            Remove-Item -LiteralPath $tempJavaFile -Force
        }
    }
}

$repoRoot = Get-RepoRoot
$keyRootName = Get-Text -Codes @(0x5BC6, 0x94A5)
$cryptoDirName = Get-Text -Codes @(0x63A5, 0x53E3, 0x52A0, 0x5BC6)
$privateKeyName = (Get-Text -Codes @(0x63A5, 0x53E3, 0x52A0, 0x5BC6, 0x79C1, 0x94A5)) + ".pem"
$publicKeyName = (Get-Text -Codes @(0x63A5, 0x53E3, 0x52A0, 0x5BC6, 0x516C, 0x94A5)) + ".pem"

if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $OutputDir = Join-Path $repoRoot ("." + $keyRootName + "\" + $cryptoDirName)
}

if ($KeySize -lt 2048) {
    throw "KeySize must be at least 2048."
}

$OutputDir = [System.IO.Path]::GetFullPath($OutputDir)
$privateKeyFile = Join-Path $OutputDir $privateKeyName
$publicKeyFile = Join-Path $OutputDir $publicKeyName

if (-not (Test-Path -LiteralPath $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

Write-Host ""
Write-Host "Generating API crypto keys..."
Write-Host "Output directory: $OutputDir"
Write-Host "Key size: $KeySize"

if (Test-OpenSsl) {
    Write-Host "Generator: OpenSSL"
    Invoke-OpenSsl @(
        "genpkey",
        "-algorithm", "RSA",
        "-pkeyopt", "rsa_keygen_bits:$KeySize",
        "-out", $privateKeyFile
    )

    Invoke-OpenSsl @(
        "rsa",
        "-pubout",
        "-in", $privateKeyFile,
        "-out", $publicKeyFile
    )
}
else {
    Write-Host "Generator: Java"
    Invoke-JavaKeyGen -PrivateKeyPath $privateKeyFile -PublicKeyPath $publicKeyFile -Size $KeySize
}

$privateKeyUri = To-FileUri -PathValue $privateKeyFile
$publicKeyUri = To-FileUri -PathValue $publicKeyFile

Write-Host ""
Write-Host "Done."
Write-Host "Private key: $privateKeyFile"
Write-Host "Public key: $publicKeyFile"
Write-Host ""
Write-Host "Environment variables:"
Write-Host "FLOWCLOUD_API_CRYPTO_PRIVATE_KEY_LOCATION=$privateKeyUri"
Write-Host "FLOWCLOUD_API_CRYPTO_PUBLIC_KEY_LOCATION=$publicKeyUri"
