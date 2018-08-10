Import-Module ActiveDirectory
$ahprint01PrintObjects = @()
$allPrintObjects = Get-ADObject -LDAPFilter "(objectCategory=printQueue)" -Properties location, printername, portname, servername, description | select portname, location, printername, servername, description | sort servername
$objectHeadings = "IP" + "`t" + "Hostname" + "`t" + "Location" + "`t" + "ADComment" + "`t" + "PrintServer"
$ahprint01PrintObjects += $objectHeadings
foreach($printObject in $allPrintObjects) {
$newPrintObject = New-Object System.Object
$newPrintObject | Add-Member -type NoteProperty -name IP -value "$($printObject.portname)"
$newPrintObject | Add-Member -type NoteProperty -name Hostname -value "$($printObject.printername)"
$newPrintObject | Add-Member -type NoteProperty -name Location -value "$($printObject.location)"
$newPrintObject | Add-Member -type NoteProperty -name ADComment -value "$($printObject.description)"
$newPrintObject | Add-Member -type NoteProperty -name PrintServer -value "$($printObject.servername)"
$newPrintObject.IP = $newPrintObject.IP -replace "`n|`r|`t",""
$newPrintObject.Hostname = $newPrintObject.Hostname -replace "`n|`r|`t",""
$newPrintObject.Location = $newPrintObject.Location -replace "`n|`r|`t",""
$newPrintObject.ADComment = $newPrintObject.ADComment -replace "`n|`r|`t",""
$newPrintObject.PrintServer = $newPrintObject.PrintServer -replace "`n|`r|`t",""

$newPrintObjectString = "$($newPrintObject.IP)" + "`t" + "$($newPrintObject.Hostname)" + "`t" + "$($newPrintObject.Location)" + "`t" + "$($newPrintObject.ADComment)" + "`t" + "$($newPrintObject.PrintServer)"

$ahprint01PrintObjects += $newPrintObjectString
}

$ahprint01PrintObjects | Out-File -filepath addataset.txt -encoding "ASCII"