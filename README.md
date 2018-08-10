# Printer-Information-Utility
Allows you to compare various information to track down details regarding Lexmark printers on the domain and network.

# About the program
I wrote this program to allow me to track down information about Lexmark printers when i was brought on to assist with a organisation wide Lexmark printer rollout at my workplace.

It is fairly crude and needs a bit of work to make it more user friendly.

It includes 3 different functions.

Subnet matcher: Allows you to compare a list IP addresses to a list of subnets to quickly find which subnets they belong to.

AD dataset matcher: Uses Powershell AD module to get a list of all printers in the directory, then allows you to search for a list of printers using the various columns available. Usually I would use this to search the directory for printers by IP, or by serial number if the AD description was properly updated with the printers serial number.

HTML parser: This function allows you automatically to gather various information from a Lexmark printers web interface using a list of IP addresses. This allowed me to get a lot of necessary information for a list of printers without manually searching through each devices web interface.

# Work needed
It currently has a pretty crude command line interface, I will probably update this to swing at some point.

It uses some obsolete code such as Vectors instead of Lists. I needed to gather the information fairly quickly to it put this utility together pretty quickly.
