import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;


public class PrinterInformationUtility
{
	//Subnet Finder global variables
	Vector<String> ip_addresses;
	Vector<Vector<String>> subnet_dataset;
	Vector<Vector<String>> output;
	Vector<Vector<String>> files_loaded;

	//AD Comparer global variables
	String ad_dataset_file_loaded;
	String ad_comparison_file_loaded;
	Vector<Vector<String>> ad_dataset;
	Vector<Vector<String>> ad_output;

	//Html Parser global variables
	Vector<Printer> html_printers;
	Vector<String> html_ip_addresses;
	Vector<Vector<String>> html_files_loaded;

	public static void main(String args[]) {
		PrinterInformationUtility loader = new PrinterInformationUtility();
		boolean run = true;
		String error = "";
		String option = "";

		loader.InitialiseFolders();

		while(run) {
			try{
				loader.displayMenu("Printer Information Gathering Utility", "Written by David Semple", error, "");
			} catch(Exception e) {
				System.out.println("Unrecoverable exception encountered. Program will terminate");
				return;
			}
			System.out.println("1. Subnet Finder");
			System.out.println("2. AD Dataset Comparer");
			System.out.println("3. HTML Parser");
			System.out.println("0. Exit");
			System.out.println("");
			try{
				BufferedReader console_in = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Please choose an option from the list above (? for help): ");
				option = console_in.readLine();	
			} catch(Exception e) {
				System.out.println("Unrecoverable exception encountered. Program will terminate");
				return;
			}

			if(option.compareTo("1") == 0) {
				error = "";
				try{
					loader.processSubnetFinder();
				} catch(Exception e) {
					System.out.println("Unrecoverable exception encountered. Program will terminate");
					return;
				}
			} else if(option.compareTo("2") == 0) {
				error = "";
				try{
					loader.processAdComparer();
				} catch(Exception e) {
					System.out.println("Unrecoverable exception encountered. Program will terminate");
					return;
				}
			} else if(option.compareTo("3") == 0) {
				error = "";
				try{
					loader.processHtmlParser();
				} catch(Exception e) {
					System.out.println("Unrecoverable exception encountered. Program will terminate");
					return;
				}
			}else if(option.compareTo("0") == 0) {
				run = false;
			} else if(option.compareTo("?") == 0) {
				try {
					String instructions = "";
					loader.displayHelp(instructions, "main menu");
					error = "";
				} catch(Exception e) {
					System.out.println("Unrecoverable exception encountered. Program will terminate");
					return;
				}
			} else {
				error = "Invalid option";
			}
		}
	}

	public PrinterInformationUtility() {
		ip_addresses = new Vector<String>();
		subnet_dataset = new Vector<Vector<String>>();
		output = new Vector<Vector<String>>();
		files_loaded = new Vector<Vector<String>>();
		Vector<String> files_loaded_headings = new Vector<String>();
		files_loaded_headings.add("Filename");
		files_loaded_headings.add("Processed");
		files_loaded.add(files_loaded_headings);

		ad_dataset_file_loaded = "";
		ad_comparison_file_loaded = "";
		ad_dataset = new Vector<Vector<String>>();
		ad_output = new Vector<Vector<String>>();

		html_printers = new Vector<Printer>();
		html_ip_addresses = new Vector<String>();
		html_files_loaded = new Vector<Vector<String>>();
		Vector<String> html_files_loaded_headings = new Vector<String>();
		html_files_loaded_headings.add("Filename");
		html_files_loaded_headings.add("Processed");
		html_files_loaded.add(html_files_loaded_headings);
	}

	public void InitialiseFolders() {
		new File("./data").mkdirs();
		new File("./output").mkdirs();
	}

	public void clearConsole() throws UnrecoverableException{
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch(Exception e) {
			throw new UnrecoverableException();
		}
	}

	public Vector<String> convert2DVectorToVector(Vector<Vector<String>> input, String delimiter) {
		Vector<String> new_output = new Vector<String>();
		for(Vector<String> line : input) {
			String new_line = "";
			for(int i = 0;i < line.size();i++) {
				if(i == line.size()-1) {
					new_line += line.get(i);
				} else {
					new_line += line.get(i) + delimiter;
				}
			}
			new_output.add(new_line);
		}
		return new_output;
	}

	public Vector<String> convert2DVectorToVector(Vector<Vector<String>> input, Vector<String> titles, String delimiter) {
		Vector<String> new_output = new Vector<String>();
		for(Vector<String> line : input) {
			String new_line = "";
			for(int i = 0;i < line.size();i++) {
				if(i == line.size()-1) {
					new_line += titles.get(i) + ": " + line.get(i);
				} else {
					new_line += titles.get(i) + ": " + line.get(i) + delimiter;
				}
			}
			new_output.add(new_line);
		}
		return new_output;
	}

	public void displayHelp(String instructions, String previous_menu) throws UnrecoverableException {
		clearConsole();
		System.out.println(instructions);
		System.out.println("");
		System.out.println("Press enter to return to " + previous_menu + "...");
		try{
			BufferedReader console_in = new BufferedReader(new InputStreamReader(System.in));
			console_in.readLine();
		} catch(Exception e) {
			throw new UnrecoverableException();
		}
	}

	public void displayMenu(String title, String subtitle, String error, String other_info) throws UnrecoverableException{
		final int total_blank_space = 46;
		String line = "";
		
		clearConsole();

		System.out.println("************************************************");
		System.out.println("*                                              *");

		line = "*";
		int left_hand_title_blank_space = (total_blank_space / 2) - (title.length() - (title.length()/2));
		for(int i = 0;i < left_hand_title_blank_space;i++) {
			line += " ";
		}
		line += (title);
		int right_hand_title_blank_space = total_blank_space - (title.length() + left_hand_title_blank_space);
		for(int i = 0;i < right_hand_title_blank_space;i++) {
			line += " ";
		}
		line += "*";
		System.out.println(line);
		
		line = "*";
		int left_hand_subtitle_blank_space = (total_blank_space / 2) - (subtitle.length() - (subtitle.length()/2));
		for(int i = 0;i < left_hand_subtitle_blank_space;i++) {
			line += " ";
		}
		line += (subtitle);
		int right_hand_subtitle_blank_space = total_blank_space - (subtitle.length() + left_hand_subtitle_blank_space);
		for(int i = 0;i < right_hand_subtitle_blank_space;i++) {
			line += " ";
		}
		line += "*";
		System.out.println(line);

		System.out.println("*                                              *");
		System.out.println("*                                              *");
		System.out.println("************************************************");
		
		System.out.println("");
		System.out.println(error);
		System.out.println(other_info);
		System.out.println("");
		
	}

	public void processSubnetFinder() throws UnrecoverableException{
		String error = "";
		int menu_list = 0;

		boolean run = true;
		BufferedReader console_in = new BufferedReader(new InputStreamReader(System.in));

		try{
			createDataset("data\\subnets.txt", subnet_dataset);
		} catch(Exception e) {
			error = "subnets.txt file is missing from the data folder. Process is unable to run";
			displayMenu("Subnet Finder", "", error, "");
			System.out.println("Press enter to return to main menu");
			try {
				console_in.readLine();
			} catch(Exception f) {
				throw new UnrecoverableException();
			}
			return;
		}

		while(run) {
			String option = "";
			String loaded_file_info = "\nLoaded Files";
			for(Vector<String> file_info : files_loaded) {
				loaded_file_info += "\n" + file_info.get(0) + "\t" + file_info.get(1);
			}
			displayMenu("Subnet Finder", "", error, loaded_file_info);

			if(menu_list == 0) {
				System.out.println("1. Load list of IP addresses");
				System.out.println("2. Process IP data");
				System.out.println("3. Output processed data");
				System.out.println("4. Clear loaded files and data");
				System.out.println("0. Back");
				System.out.println("");
				try{
					System.out.println("Please choose an option from the list above (? for help): ");
					option = console_in.readLine();	
				} catch(Exception e) {
					throw new UnrecoverableException();
				}

				if(option.compareTo("1") == 0) {
					error = "";
					menu_list = 1;
				} else if(option.compareTo("2") == 0) {
					error = "";
					menu_list = 2;
				} else if(option.compareTo("3") == 0) {
					error = "";
					menu_list = 3;
				} else if(option.compareTo("4") == 0) {
					error = "";
					menu_list = 4;
				} else if(option.compareTo("0") == 0) {
					run = false;
				} else if(option.compareTo("?") == 0) {
					try {
						String instructions = "This program takes a list of IP addresses and calculates what subnet that IP is a part of and gives a rough location based on the subnet.\nTo use it you must first load a file containing the list of IP addresses. The file must contain each IP address on a new line. You may load multiple different files.\nOnce you have loaded one or more files use menu option 2 to process the data from the files and calculate the subnets they belong to. Each file loaded and whether or not it has been process will be shown in the menu.\nOnce you have processed the IP data you can output it using the various output options from menu 3.\nIf you wish to clear the IP and subnet data you have loaded use menu option 4 to start again.";
						displayHelp(instructions, "Subnet Finder main menu");
						error = "";
					} catch(Exception e) {
						throw new UnrecoverableException();
					}
				} else {
					error = "Invalid option";
				}
			} else if(menu_list == 1) {
				System.out.println("0. Back");
				System.out.println("");
				try{
					System.out.println("Please enter the name + path of the IP address list file (? for help): ");
					option = console_in.readLine();
				} catch(Exception e) {
					throw new UnrecoverableException();
				}

				if(option.compareTo("0") == 0) {
					error = "";
					menu_list = 0;
				} else if(option.compareTo("?") == 0) {
					try {
						String instructions = "You may enter the full file path (eg C:\\example folder\\example file.txt) or the relative path of the file to the program (eg example file.txt if the file is in the same folder as the program).";
						displayHelp(instructions, "Subnet Finder file loader menu");
						error = "";
					} catch(Exception e) {
						throw new UnrecoverableException();
					}
				} else {
					try {
						readFromFile(option, ip_addresses);
						Vector<String> new_loaded_file = new Vector<String>();
						new_loaded_file.add(option);
						new_loaded_file.add("N");
						files_loaded.add(new_loaded_file);
						error = "Successfully loaded IP list";
						menu_list = 0;
					} catch(Exception e) {
						error = "Bad filename";
					}
				}
			} else if(menu_list == 2) {
				if(ip_addresses.size() > 0) {
					output = new Vector<Vector<String>>();
					calculateSubnets(ip_addresses, subnet_dataset, output);
					for(Vector<String> file_info : files_loaded) {
						if(file_info.get(1).compareTo("N") == 0) {
							file_info.set(1, "Y");
						}
					}
					error = "Successfully processed dataset";
					menu_list = 0;
				} else {
					error = "Please load an IP list file first";
					menu_list = 0;
				}
			} else if(menu_list == 3) {
				if(output.size() > 0) {
					System.out.println("1. Output to console");
					System.out.println("2. Export to txt (tab delimited, raw data)");
					System.out.println("3. Export to txt (single line, human readable)");
					System.out.println("0. Back");
					System.out.println("");
					try{
						System.out.println("Please choose an option from the list above (? for help): ");
						option = console_in.readLine();	
					} catch(Exception e) {
						throw new UnrecoverableException();
					}

					if(option.compareTo("1") == 0) {
						clearConsole();
						Vector<String> one_line_output = convert2DVectorToVector(output, "\t");
						System.out.println("IP\t\tSubnet\t\tLocation");
						for(String output_string : one_line_output) {
							System.out.println(output_string);
						}
						System.out.println("");
						System.out.println("Press enter to return to output menu...");
						try{
							error = "";
							console_in.readLine();
						} catch(Exception e) {
							throw new UnrecoverableException();
						}
					} else if(option.compareTo("2") == 0) {
						Vector<String> one_line_output = convert2DVectorToVector(output, "\t");
						one_line_output.add(0, "IP\tSubnet\tLocation");
						try {
							TimeZone timezone = TimeZone.getTimeZone("UTC");
							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat filename_utc = new SimpleDateFormat ("yyMMddHHmmss");
							String current_date = filename_utc.format(calendar.getTime());
							writeToFile("output\\subnetfinder_tab_" + current_date + ".txt", one_line_output);
							error = "Successfully wrote (tab delimited, raw data) to file output\\subnetfinder_tab_" + current_date + ".txt";
						} catch(Exception e) {
							error = "Unable to write to file";
						}			
					} else if(option.compareTo("3") == 0) {
						Vector<String> titles = new Vector<String>();
						titles.add("IP");
						titles.add("Subnet");
						titles.add("Location");
						Vector<String> one_line_output = convert2DVectorToVector(output, titles, " --- ");
						try {
							TimeZone timezone = TimeZone.getTimeZone("UTC");
							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat filename_utc = new SimpleDateFormat ("yyMMddHHmmss");
							String current_date = filename_utc.format(calendar.getTime());
							writeToFile("output\\subnetfinder_line_" + current_date + ".txt", one_line_output);
							error = "Successfully wrote (single line, human readable) to file output\\subnetfinder_line_" + current_date + ".txt";
						} catch(Exception e) {
							error = "Unable to write to file";
						}
					} else if(option.compareTo("0") == 0) {
						error = "";
						menu_list = 0;
					} else if(option.compareTo("?") == 0) {
						try {
							String instructions = "Use these options to output processed IP and subnet data.\nFor quick viewing use option 1 to print the information to the console.\nUse option 2 to output the raw data to a tab delimited text file. This will only output the ip, subnet and subnet location seperated by a tab for each IP address to a new line. This can be easily imported into Excel.\nIf you just need the data in some kind of easily human readable format use option 3. This will put each ip + subnet + location onto a line in the text file with labels appended to easily tell what it is.";
							displayHelp(instructions, "Subnet Finder output menu");
							error = "";
						} catch(Exception e) {
							throw new UnrecoverableException();
						}
					} else {
						error = "Invalid option";
					}
				} else {
					error = "Please load and process an IP list file first";
					menu_list = 0;
				}
			} else if(menu_list == 4) {
				ip_addresses.clear();
				output.clear();
				files_loaded.clear();
				Vector<String> files_loaded_headings = new Vector<String>();
				files_loaded_headings.add("Filename");
				files_loaded_headings.add("Processed");
				files_loaded.add(files_loaded_headings);

				error = "Successfully cleared all loaded and processed data";
				menu_list = 0;
			}
		}
	}

	public void calculateSubnets(Vector<String> ip_addresses, Vector<Vector<String>> subnet_dataset, Vector<Vector<String>> output) {
		for(String ip : ip_addresses) {
			String current_subnet = "";
			String ip_ending = "";
			String subnet_ending = "-1";
			String subnet_location = "";
			String[] octets = ip.split("\\.");
			Pattern regex_valid_octet = Pattern.compile("[0-9]{1,3}");
			if(octets.length == 4 && (regex_valid_octet.matcher(octets[0]).matches() && regex_valid_octet.matcher(octets[1]).matches() && regex_valid_octet.matcher(octets[2]).matches() && regex_valid_octet.matcher(octets[3]).matches())) {
				current_subnet = octets[0] + "." + octets[1] + "." + octets[2];
				ip_ending = octets[3];
				for(Vector<String> subnet : subnet_dataset) {
					String subnet_first = "";
					String subnet_last = "";
					String[] subnet_octets = subnet.get(0).split("\\.");
					subnet_first = subnet_octets[0] + "." + subnet_octets[1] + "." + subnet_octets[2];
					subnet_last = subnet_octets[3];
					if(current_subnet.compareTo(subnet_first) == 0) {
						if((Integer.parseInt(subnet_last) > Integer.parseInt(subnet_ending)) && (Integer.parseInt(subnet_last) <= Integer.parseInt(ip_ending))) {
							subnet_ending = subnet_last;
							subnet_location = subnet.get(1);
						}
					}
				}
				if(subnet_ending.compareTo("-1") == 0) {
					current_subnet = "No matching subnet found";
					subnet_location = "N/A";
				} else {
					current_subnet += "." + subnet_ending;
				}
			} else {
				current_subnet = "Not a valid IP address";
				subnet_location = "Not a valid IP address";
			}
			Vector<String> subnet_details = new Vector<String>();
			subnet_details.add(ip);
			subnet_details.add(current_subnet);
			subnet_details.add(subnet_location);
			output.add(subnet_details);
		}
	}

	public void processAdComparer() throws UnrecoverableException{
		String error = "";
		int menu_list = 0;

		boolean run = true;
		BufferedReader console_in = new BufferedReader(new InputStreamReader(System.in));

		while(run) {
			String option = "";
			String loaded_file_info = "\nCurrently loaded AD dataset";
			loaded_file_info += "\n" + ad_dataset_file_loaded;
			loaded_file_info += "\n\nCurrently processed comparison file";
			loaded_file_info += "\n" + ad_comparison_file_loaded;
			displayMenu("AD Dataset Comparer", "", error, loaded_file_info);

			if(menu_list == 0) {
				System.out.println("1. Load default AD dataset");
				System.out.println("2. Load custom dataset");
				System.out.println("3. Compare data to dataset");
				System.out.println("4. Output processed data");
				System.out.println("5. Clear only comparison file and data");
				System.out.println("6. Clear all loaded files and data");
				System.out.println("7. Update default AD dataset file");
				System.out.println("0. Back");
				System.out.println("");
				try{
					System.out.println("Please choose an option from the list above (? for help): ");
					option = console_in.readLine();	
				} catch(Exception e) {
					throw new UnrecoverableException();
				}

				if(option.compareTo("1") == 0) {
					error = "";
					menu_list = 1;
				} else if(option.compareTo("2") == 0) {
					error = "";
					menu_list = 2;
				} else if(option.compareTo("3") == 0) {
					error = "";
					menu_list = 3;
				} else if(option.compareTo("4") == 0) {
					error = "";
					menu_list = 4;
				} else if(option.compareTo("5") == 0) {
					error = "";
					menu_list = 5;
				} else if(option.compareTo("6") == 0) {
					error = "";
					menu_list = 6;
				} else if(option.compareTo("7") == 0) {
					error = "";
					menu_list = 7;
				} else if(option.compareTo("0") == 0) {
					run = false;
				} else if(option.compareTo("?") == 0) {
					try {
						String instructions = "This program allows you to take a tab delimited list of data and search for specific records within that list using a comparison file.\nIt was designed to be used to find specific printers within an Active Directory using a list of IP addresses or Serial Numbers, however the program has been expanded to allow a search for any specific details with a tab delimited list.\n\nTo use the program you need 2 pieces of data:\n1. A tab delimited list of information to search through. This list needs to be formatted so that each line is essentially a new record. Each tabbed section of the line represents a column in that record.The first line in this file must contain the headings for each column\n2. A list of data to search for. This file needs to contain each search phrase on a new line.\n\nOnce both files are loaded the program will then ask you to choose which column you would like to search in. It will then go through each line in the second comparison file until it finds a match for it in the specified column of the first file. PLEASE NOTE It will stop when it finds the first match for that search phrase. This means that if for example your Active Directory has two printers with the serial number ABCDEFG in the ADComment of AD it will only match the first record it finds of this. Be careful to ensure your tab delimited dataset is sorted with this in mind. The dataset I use to find printers has all printers on AHPRINT01 at the top of the dataset to ensure that it will find them first, before looking on the other servers.\n\nUse option 1 to load the default tab delimited AD dataset and option 2 to load your own custom tab delimited dataset.\nOption 3 attempts to load the comparison dataset. It will also ask you which column you want to search in (it will show you the titles for each column when it asks this for ease of reference) and whether you are searching specifically for an IP address. It is very important to select the correct option when asked whether the search criteria is an IP address, otherwise if it is currently searching for 192.168.1.1 and it first finds 192.168.1.122 it will stop and select this record if you choose to not search specifically for an IP address.\nOnce it has finished processing the search use option 4 to output the data.\nOption 5 allows you to clear the comparison data and re-use the current dataset without having to reload it.\n Option 6 allows you to clear all the data that has been loaded and processed, allowing you to start again without re-running the program.\nOption 7 will run a powershell script that will generate the default AD dataset. This default dataset is a list of all printers in AD (except those on ac-vps01) and contains a column for the IP, Hostname (yellow sticker), Location, ADComment and Server.";
						displayHelp(instructions, "AD Dataset comparer main menu");
						error = "";
					} catch(Exception e) {
						throw new UnrecoverableException();
					}
				} else {
					error = "Invalid option";
				}
			} else if(menu_list == 1) {
				try {
					String default_dataset = "data\\addataset.txt";
					checkFile(default_dataset);
					ad_dataset = new Vector<Vector<String>>();
					createDataset(default_dataset, ad_dataset);
					ad_dataset_file_loaded = default_dataset;
					error = "Successfully loaded default AD dataset";
					menu_list = 0;
				} catch(Exception e) {
					error = "Unable to load default dataset. Use option 7 to re-generate the default dataset file.";
				}
			} else if(menu_list == 2) {
				System.out.println("0. Back");
				System.out.println("");
				try{
					System.out.println("Please enter the name + path of the AD dataset file (? for help): ");
					option = console_in.readLine();
				} catch(Exception e) {
					throw new UnrecoverableException();
				}

				if(option.compareTo("0") == 0) {
					error = "";
					menu_list = 0;
				} else if(option.compareTo("?") == 0) {
					try {
						String instructions = "You may enter the full file path (eg C:\\example folder\\example file.txt) or the relative path of the file to the program (eg example file.txt if the file is in the same folder as the program).";
						displayHelp(instructions, "AD dataset comparer dataset file loader menu");
						error = "";
					} catch(Exception e) {
						throw new UnrecoverableException();
					}
				} else {
					try {
						checkFile(option);
						ad_dataset = new Vector<Vector<String>>();
						createDataset(option, ad_dataset);
						ad_dataset_file_loaded = option;
						error = "Successfully loaded AD dataset";
						menu_list = 0;
					} catch(Exception e) {
						error = "Bad filename";
					}
				}
			} else if(menu_list == 3) {
				if(ad_dataset.size() > 0) {
					Vector<String> ad_comparison_dataset = new Vector<String>();
					int column = 0;
					boolean isIp = false;
					boolean correct_input = true;
					try{
						System.out.println("Please enter the filename + path of the data to compare: ");
						String filename = console_in.readLine();
						readFromFile(filename, ad_comparison_dataset);
						ad_comparison_file_loaded = filename;
					} catch(Exception e) {
						error = "Bad file name";
						correct_input = false;
						ad_comparison_file_loaded = "";
						menu_list = 0;
					}

					if(correct_input) {
						try{
							System.out.println("");
							System.out.println("Column Name\tColumn Number");
							for(int i = 0;i < ad_dataset.get(0).size();i++) {
								int column_number = i+1;
								System.out.println(ad_dataset.get(0).get(i) + "\t" + column_number);
							}
							System.out.println("");
							System.out.println("Please enter the number of the column in the AD dataset to compare with (see above for valid numbers): ");
							column = Integer.parseInt(console_in.readLine());
						} catch(Exception e) {
							error = "Column must be a number";
							correct_input = false;
							ad_comparison_file_loaded = "";
							menu_list = 0;
						}
						if(correct_input) {
							try{
								System.out.println("Are you comparing an IP address?(y/n): ");
								String ipInput = console_in.readLine();
								if(ipInput.compareTo("y") == 0) {
									isIp = true;
								} else if(ipInput.compareTo("n") == 0) {
									isIp = false;
								} else {
									error = "Answer must be y or n";
									correct_input = false;
									ad_comparison_file_loaded = "";
									menu_list = 0;
								}	
							} catch(Exception e) {
								error = "Answer must be y or n";
								correct_input = false;
								ad_comparison_file_loaded = "";
								menu_list = 0;
							}
							if(correct_input) {
								try{
									ad_output = new Vector<Vector<String>>();
									compareAdDataset(ad_dataset, ad_comparison_dataset, ad_output, column-1, isIp);
									error = "Successfully compared to dataset";
									menu_list = 0;
								} catch(Exception e) {
									error = "Bad column number, check the ad dataset and comparison dataset and try again";
									ad_comparison_file_loaded = "";
									menu_list = 0;
								}
							}
						}
					}
				} else {
					error = "Please load an AD dataset file first";
					menu_list = 0;
				}
			} else if(menu_list == 4) {
				if(ad_output.size() > 0) {
					System.out.println("1. Output to console");
					System.out.println("2. Export to txt (tab delimited, raw data)");
					System.out.println("3. Export to txt (single line, human readable)");
					System.out.println("0. Back");
					System.out.println("");
					try{
						System.out.println("Please choose an option from the list above (? for help): ");
						option = console_in.readLine();	
					} catch(Exception e) {
						throw new UnrecoverableException();
					}

					if(option.compareTo("1") == 0) {
						clearConsole();
						Vector<String> one_line_output = convert2DVectorToVector(ad_output, "\t");
						String output_headings = "";
						for(int i = 0;i < ad_dataset.get(0).size();i++) {
							if(i == ad_dataset.get(0).size()-1) {
								output_headings += ad_dataset.get(0).get(i);
							} else {
								output_headings += ad_dataset.get(0).get(i) + "\t";
							}
						}
						System.out.println(output_headings);
						for(String output_string : one_line_output) {
							System.out.println(output_string);
						}
						System.out.println("");
						System.out.println("Press enter to return to output menu...");
						try{
							error = "";
							console_in.readLine();
						} catch(Exception e) {
							throw new UnrecoverableException();
						}
					} else if(option.compareTo("2") == 0) {
						Vector<String> one_line_output = convert2DVectorToVector(ad_output, "\t");
						String output_headings = "";
						for(int i = 0;i < ad_dataset.get(0).size();i++) {
							if(i == ad_dataset.get(0).size()-1) {
								output_headings += ad_dataset.get(0).get(i);
							} else {
								output_headings += ad_dataset.get(0).get(i) + "\t";
							}
						}
						one_line_output.add(0, output_headings);
						try {
							TimeZone timezone = TimeZone.getTimeZone("UTC");
							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat filename_utc = new SimpleDateFormat ("yyMMddHHmmss");
							String current_date = filename_utc.format(calendar.getTime());
							writeToFile("output\\adcomparer_tab_" + current_date + ".txt", one_line_output);
							error = "Successfully wrote (tab delimited, raw data) to file output\\adcomparer_tab_" + current_date + ".txt";
						} catch(Exception e) {
							error = "Unable to write to file";
						}			
					} else if(option.compareTo("3") == 0) {
						Vector<String> one_line_output = convert2DVectorToVector(ad_output, ad_dataset.get(0), " --- ");
						try {
							TimeZone timezone = TimeZone.getTimeZone("UTC");
							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat filename_utc = new SimpleDateFormat ("yyMMddHHmmss");
							String current_date = filename_utc.format(calendar.getTime());
							writeToFile("output\\adcomparer_line_" + current_date + ".txt", one_line_output);
							error = "Successfully wrote (single line, human readable) to file output\\adcomparer_line_" + current_date + ".txt";
						} catch(Exception e) {
							error = "Unable to write to file";
						}
					} else if(option.compareTo("0") == 0) {
						error = "";
						menu_list = 0;
					} else if(option.compareTo("?") == 0) {
						try {
							String instructions = "Use these options to output processed comparison data.\nFor quick viewing use option 1 to print the information to the console.\nUse option 2 to output the raw data to a tab delimited text file. This will output each search result to a new line with each column seperated by a tab. The headings for each column will be on the first line of the file. This can be easily imported into Excel.\nIf you just need the data in some kind of easily human readable format use option 3. This will put each search result onto a line in the text file with the titles for each column appended to the data to easily tell what it is.";
							displayHelp(instructions, "AD dataset comparer output menu");
							error = "";
						} catch(Exception e) {
							throw new UnrecoverableException();
						}
					} else {
						error = "Invalid option";
					}
				} else {
					error = "Please load and process and AD dataset and comparison file first";
					menu_list = 0;
				}
			} else if(menu_list == 5) {
				ad_comparison_file_loaded = "";
				ad_output.clear();
				
				error = "Successfully cleared loaded comparison file";
				menu_list = 0;
			}else if(menu_list == 6) {
				ad_dataset_file_loaded = "";
				ad_comparison_file_loaded = "";
				ad_dataset.clear();
				ad_output.clear();
				
				error = "Successfully cleared all loaded and processed data";
				menu_list = 0;
			} else if(menu_list == 7) {
				try{
					System.out.println("Generating AD Dataset file to data\\addataset.txt. Please wait...");
					String command = "powershell.exe Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process | powershell.exe \"scripts\\GetPrinterDataFromAD.ps1\"";
  					Process powerShellProcess = Runtime.getRuntime().exec(command);
					BufferedReader powershell_process_error_stream = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
					powerShellProcess.waitFor();
					String error_stream = powershell_process_error_stream.readLine();
					if(error_stream == null) {
						error = "Successfully generated file to data\\addataset.txt";
					} else {
						throw new IOException();
					}
				} catch(Exception e) {
					error = "Failed to generate file. Ensure the data folder exists and you have appropriate permissions to write to it.";
				}
				menu_list = 0;
			}
		}
	}


	public void compareAdDataset(Vector<Vector<String>> ad_dataset, Vector<String> ip_addresses, Vector<Vector<String>> subnet_by_ip, int column, boolean isIp) throws IOException {
		BufferedReader console_in = new BufferedReader(new InputStreamReader(System.in));
		boolean exists = false;

		for(String ip_line : ip_addresses) {
			Vector<String> line = new Vector<String>();
			for(Vector<String> data_line : ad_dataset) {
				try {
					String data_line_string = data_line.get(column);
					if(data_line_string != null) {
						if(Pattern.compile(Pattern.quote(ip_line), Pattern.CASE_INSENSITIVE).matcher(data_line_string).find()) {
							boolean correctIp = false;
							if(isIp) {
								if((data_line_string.indexOf(ip_line) + ip_line.length() + 1) <= data_line_string.length()) {
									Pattern pattern = Pattern.compile("[0-9]");
									if(!pattern.matcher(data_line_string.substring(data_line_string.indexOf(ip_line) + ip_line.length(),data_line_string.indexOf(ip_line) + ip_line.length() + 1)).matches()) {
										correctIp = true;
									}
								} else {
									correctIp = true;
								}
							} else {
								correctIp = true;
							}
							if(correctIp) {
								line = data_line;
								exists = true;
								break;
							}
						}
					}
				} catch(Exception e) {
					throw new IOException("Bad column number, check the ad dataset and comparison dataset and try again");
				}
			}
			if(!exists) {
				line.add("Does not exist");
			}
			subnet_by_ip.add(line);
			exists = false;
		}
	}

	public void processHtmlParser() throws UnrecoverableException{
		String error = "";
		int menu_list = 0;

		boolean run = true;
		BufferedReader console_in = new BufferedReader(new InputStreamReader(System.in));

		while(run) {
			String option = "";
			String loaded_file_info = "\nLoaded Files";
			for(Vector<String> file_info : html_files_loaded) {
				loaded_file_info += "\n" + file_info.get(0) + "\t" + file_info.get(1);
			}
			displayMenu("HTML Parser", "", error, loaded_file_info);

			if(menu_list == 0) {
				System.out.println("1. Load list of IP addresses");
				System.out.println("2. Process IP data");
				System.out.println("3. Output processed data");
				System.out.println("4. Clear loaded files and data");
				System.out.println("0. Back");
				System.out.println("");
				try{
					System.out.println("Please choose an option from the list above (? for help): ");
					option = console_in.readLine();	
				} catch(Exception e) {
					throw new UnrecoverableException();
				}

				if(option.compareTo("1") == 0) {
					error = "";
					menu_list = 1;
				} else if(option.compareTo("2") == 0) {
					error = "";
					menu_list = 2;
				} else if(option.compareTo("3") == 0) {
					error = "";
					menu_list = 3;
				} else if(option.compareTo("4") == 0) {
					error = "";
					menu_list = 4;
				} else if(option.compareTo("0") == 0) {
					run = false;
				} else if(option.compareTo("?") == 0) {
					try {
						String instructions = "This program takes a list of IP addresses and attempts to access specific Lexmark Printer web interface pages to find information about the printer.\nIf you need to get specifics about a printer or a whole list of them but only have the IP addresses this utility will find the hostname, model, location, contact, serial number, connected printers serial number (if the IP belongs to an MX6500 or something similar) and MAC address.\n\nThe specific Lexmark web interface pages it will search on are:\nhttp://<INSERT_IP_HERE>/cgi-bin/dynamic/topbar.html\nhttp://<INSERT_IP_HERE>/cgi-bin/dynamic/printer/config/reports/deviceinfo.html\nhttp://<INSERT_IP_HERE>/cgi-bin/dynamic/config/gen/setuppg.html\n\nUse option 1 to load a list of IP addresses. The file must be formatted to contain each IP address you wish to search for on a new line.\nUse option 2 to collect the information after you have loaded an IP address list. PLEASE NOTE This process isn't particularly well optimized and it is attempting to download each of the 3 pages mentioned above for each IP address. If you are searching for a long list of printers this process may take a while. It will show you the progress as it processes, but keep in mind it can take on average 1-10 seconds per IP address.\nUse option 3 to output the processed data.\nOption 4 allows you to clear all the data that has been loaded and processed, allowing you to start again without re-running the program.";
						displayHelp(instructions, "HTML Parser main menu");
						error = "";
					} catch(Exception e) {
						throw new UnrecoverableException();
					}
				} else {
					error = "Invalid option";
				}
			} else if(menu_list == 1) {
				System.out.println("0. Back");
				System.out.println("");
				try{
					System.out.println("Please enter the name + path of the IP address list file (? for help): ");
					option = console_in.readLine();
				} catch(Exception e) {
					throw new UnrecoverableException();
				}

				if(option.compareTo("0") == 0) {
					error = "";
					menu_list = 0;
				} else if(option.compareTo("?") == 0) {
					try {
						String instructions = "You may enter the full file path (eg C:\\example folder\\example file.txt) or the relative path of the file to the program (eg example file.txt if the file is in the same folder as the program).";
						displayHelp(instructions, "HTML Parser file loader menu");
						error = "";
					} catch(Exception e) {
						throw new UnrecoverableException();
					}
				} else {
					try {
						readFromFile(option, html_ip_addresses);
						Vector<String> new_loaded_file = new Vector<String>();
						new_loaded_file.add(option);
						new_loaded_file.add("N");
						html_files_loaded.add(new_loaded_file);
						error = "Successfully loaded IP list";
						menu_list = 0;
					} catch(Exception e) {
						error = "Bad filename";
					}
				}
			} else if(menu_list == 2) {
				if(html_ip_addresses.size() > 0) {
					populatePrinters();
					for(Vector<String> file_info : html_files_loaded) {
						if(file_info.get(1).compareTo("N") == 0) {
							file_info.set(1, "Y");
						}
					}
					error = "Successfully processed dataset";
					menu_list = 0;
				} else {
					error = "Please load an IP list file first";
					menu_list = 0;
				}
			} else if(menu_list == 3) {
				if(html_printers.size() > 0) {
					System.out.println("1. Output to console");
					System.out.println("2. Export to txt (tab delimited, raw data)");
					System.out.println("3. Export to txt (single line, human readable)");
					System.out.println("0. Back");
					System.out.println("");
					try{
						System.out.println("Please choose an option from the list above (? for help): ");
						option = console_in.readLine();	
					} catch(Exception e) {
						throw new UnrecoverableException();
					}

					if(option.compareTo("1") == 0) {
						clearConsole();
						Vector<String> printer_output = dumpPrinters("\t", false);
						for(String output_line : printer_output) {
							System.out.println(output_line);
						}
						System.out.println("");
						System.out.println("Press enter to return to output menu...");
						try{
							error = "";
							console_in.readLine();
						} catch(Exception e) {
							throw new UnrecoverableException();
						}
					} else if(option.compareTo("2") == 0) {
						Vector<String> one_line_output = dumpPrinters("\t", false);
						try {
							TimeZone timezone = TimeZone.getTimeZone("UTC");
							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat filename_utc = new SimpleDateFormat ("yyMMddHHmmss");
							String current_date = filename_utc.format(calendar.getTime());
							writeToFile("output\\htmlparser_tab_" + current_date + ".txt", one_line_output);
							error = "Successfully wrote (tab delimited, raw data) to file output\\htmlparser_tab_" + current_date + ".txt";
						} catch(Exception e) {
							error = "Unable to write to file";
						}			
					} else if(option.compareTo("3") == 0) {
						Vector<String> one_line_output = dumpPrinters(" --- ", true);
						try {
							TimeZone timezone = TimeZone.getTimeZone("UTC");
							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat filename_utc = new SimpleDateFormat ("yyMMddHHmmss");
							String current_date = filename_utc.format(calendar.getTime());
							writeToFile("output\\htmlparser_line_" + current_date + ".txt", one_line_output);
							error = "Successfully wrote (single line, human readable) to file output\\htmlparser_line_" + current_date + ".txt";
						} catch(Exception e) {
							error = "Unable to write to file";
						}
					} else if(option.compareTo("0") == 0) {
						error = "";
						menu_list = 0;
					} else if(option.compareTo("?") == 0) {
						try {
							String instructions = "Use these options to output processed printer web interface data.\nFor quick viewing use option 1 to print the information to the console.\nUse option 2 to output the raw data to a tab delimited text file. This will output each search result to a new line with each column seperated by a tab. The headings for each column will be on the first line of the file. This can be easily imported into Excel.\nIf you just need the data in some kind of easily human readable format use option 3. This will put each search result onto a line in the text file with the titles for each column appended to the data to easily tell what it is.";
							displayHelp(instructions, "HTML Parser output menu");
							error = "";
						} catch(Exception e) {
							throw new UnrecoverableException();
						}
					} else {
						error = "Invalid option";
					}
				} else {
					error = "Please load and process an IP list file first";
					menu_list = 0;
				}
			} else if(menu_list == 4) {
				html_ip_addresses.clear();
				html_printers.clear();
				html_files_loaded.clear();
				Vector<String> html_files_loaded_headings = new Vector<String>();
				html_files_loaded_headings.add("Filename");
				html_files_loaded_headings.add("Processed");
				html_files_loaded.add(html_files_loaded_headings);
				
				error = "Successfully cleared all loaded and processed data";
				menu_list = 0;
			}
		}
	}

	public void populatePrinters() {
		html_printers.clear();
		int count = 0;
		for(String ip : html_ip_addresses) {
			Printer printer = new Printer();
			printer.ip = ip;
			testConnection(ip, printer);
			if(printer.is_connected) {
				loadTopBar(ip, printer);
				loadDeviceInfoReport(ip, printer);
				loadNetworkSetupPage(ip, printer);
			}
			html_printers.add(printer);
			count++;
			System.out.println("Processed " + count + "/" + html_ip_addresses.size());
		}
	}

	public void testConnection(String ip, Printer printer) {
		String html = "http://" + ip;
		Document doc = null;
		try {
			doc = Jsoup.connect(html).get();
			printer.is_connected = true;
		} catch(Exception e) {
			printer.is_connected = false;
		}
	}

	public void loadTopBar(String ip, Printer printer) {
		String html = "http://" + ip + "/cgi-bin/dynamic/topbar.html";
		Document doc = null;
		try {
			doc = Jsoup.connect(html).get();
		} catch(Exception e) {
			printer.contact_name = "Error retrieving info";
			printer.location = "Error retrieving info";
		}

		if(doc != null) {
			String cleaned_doc = doc.text().replace("\u00a0", " ");
			if(cleaned_doc.indexOf("Contact Name:") + 14 < cleaned_doc.indexOf("Location:")) {
				printer.contact_name = cleaned_doc.substring(cleaned_doc.indexOf("Contact Name:") + 14, cleaned_doc.indexOf("Location:"));
			}
			if(cleaned_doc.indexOf("Location:") + 10 < cleaned_doc.length()) {			
				printer.location = cleaned_doc.substring(cleaned_doc.indexOf("Location:") + 10);
			}
		}
	}

	public void loadDeviceInfoReport(String ip, Printer printer) {
		String html = "http://" + ip + "/cgi-bin/dynamic/printer/config/reports/deviceinfo.html";
		Document doc = null;
		try {
			doc = Jsoup.connect(html).get();
		} catch(Exception e) {
			printer.serial = "Error retrieving info";
		}

		if(doc != null) {
			Elements elements = doc.getElementsByTag("td");
			boolean serial_found = false;
			boolean printer_serial_found = false;
			String serial = "";
			String printer_serial = "";
			for(Element element : elements) {
				if(serial_found && serial.compareTo("") == 0) {
					serial = element.text();
				}
				if(printer_serial_found && printer_serial.compareTo("") == 0) {
					printer_serial = element.text();
					break;
				}

				if(element.text().replace("\u00a0", " ").contains("Serial Number")) {
					if(!serial_found) {
						serial_found = true;
					} else if(element.text().replace("\u00a0", " ").contains("Printer Serial Number")) {
						printer_serial_found = true;
					}
				}
			}
			printer.serial = serial;
			printer.connected_printer_serial = printer_serial;
		}
	}

	public void loadNetworkSetupPage(String ip, Printer printer) {
		String html = "http://" + ip + "/cgi-bin/dynamic/config/gen/setuppg.html";
		Document doc = null;
		try {
			doc = Jsoup.connect(html).get();
		} catch(Exception e) {
			printer.hostname = "Error retrieving info";
			printer.model = "Error retrieving info";
			printer.mac = "Error retrieving info";
		}

		if(doc != null) {
			Elements elements = doc.getElementsByTag("td");
			boolean mac_found = false;
			boolean model_found = false;
			boolean hostname_found = false;
			String mac = "";
			String model = "";
			String hostname = "";
			for(Element element : elements) {
				if(mac_found && mac.compareTo("") == 0) {
					mac = element.text();
				}
				if(model_found && model.compareTo("") == 0) {
					model = element.text();
				}
				if(hostname_found && hostname.compareTo("") == 0) {
					hostname = element.text();
				}

				if(element.text().replace("\u00a0", " ").contains("UAA (MAC)")) {
					mac_found = true;
				} else if(element.text().replace("\u00a0", " ").contains("Printer Type")) {
					model_found = true;
				} else if(element.text().replace("\u00a0", " ").contains("Fully Qualified Domain Name")) {
					hostname_found = true;
				} 
			}
			printer.mac = mac;
			printer.model = model;
			printer.hostname = hostname;
		}
	}
	
	public Vector<String> dumpPrinters(String delimiter, boolean headings) {
		Vector<String> printer_details = new Vector<String>();
		if(html_printers.size() > 0) {
			if(!headings) {
				String printer_headings = "IP\tHostname\tModel\tContact Name\tLocation\tSerial\tConnected Printer Serial\tMAC";
				printer_details.add(printer_headings);
			}
			for(Printer printer : html_printers) {
				printer_details.add(printer.toString(delimiter, headings));
			}
		} else {
			printer_details.add("No printer details loaded");
		}
		return printer_details;
	}

	public void createDataset(String filename, Vector<Vector<String>> dataset) throws IOException{
		Vector<String> temp_data = new Vector<String>();
		try {
			readFromFile(filename, temp_data);
		} catch(Exception e) {
			throw new IOException();
		}

		for(String data_line : temp_data) {
			String[] split_temp_data = data_line.split("\t");
			Vector<String> processed_data = new Vector<String>();
			for(String data : split_temp_data) {
				processed_data.add(data);
			}
			dataset.add(processed_data);
		}
	}

	public void checkFile(String filename) throws IOException{
		try {
			File file = new File(filename);
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			}
		} catch(Exception e) {
			throw new IOException();
		}
	}

	public void readFromFile(String filename, Vector<String> write_to_vector) throws IOException{
		try {
			File file = new File(filename);
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
  				for(String line; (line = br.readLine()) != null; ) {
       					write_to_vector.add(line);
   				}
			}
		} catch(Exception e) {
			throw new IOException();
		}
	}

	public void writeToFile(String filename, Vector<String> write_to_file) throws IOException{
		try {
			int count = 0;
			File file = new File(filename);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			for(String string_line : write_to_file) {
				if(count > 0) {
					writer.newLine();
				}
				writer.write(string_line);
				count++;
			}
			writer.close();
		} catch(Exception e) {
			throw new IOException();
		}
	}
}

class Printer
{
	boolean is_connected;

	String ip;
	String hostname;
	String model;
	String contact_name;
	String location;
	String serial;
	String connected_printer_serial;
	String mac;

	public Printer() {
		is_connected = false;
		ip = "";
		hostname = "";
		model = "";
		contact_name = "";
		location = "";
		serial = "";
		connected_printer_serial = "";
		mac = "";
	}

	public String toString() {
		String output = "";
		output = toString(" --- ", true);
		return output;
	}

	public String toString(String delimiter, boolean headings) {
		String output = "";

		if(is_connected) {
			if(hostname.compareTo("Error retrieving info") == 0 && model.compareTo("Error retrieving info") == 0 && contact_name.compareTo("Error retrieving info") == 0 && location.compareTo("Error retrieving info") == 0 && serial.compareTo("Error retrieving info") == 0 && connected_printer_serial.compareTo("") == 0 && mac.compareTo("Error retrieving info") == 0) {
				output += "IP is connected but not a Lexmark printer";
			} else {
				if(headings) {
					output += "IP: " + ip + delimiter;
					output += "Hostname: " + hostname + delimiter;
					output += "Model: " + model + delimiter;
					output += "Contact Name: " + contact_name + delimiter;
					output += "Location: " + location + delimiter;
					output += "Serial: " + serial + delimiter;
					if(connected_printer_serial.compareTo("") != 0) {
						output += "Connected Printer Serial: " + connected_printer_serial + delimiter;
					}
					output += "MAC: " + mac;
				} else {
					output += ip + delimiter;
					output += hostname + delimiter;
					output += model + delimiter;
					output += contact_name + delimiter;
					output += location + delimiter;
					output += serial + delimiter;
					if(connected_printer_serial.compareTo("") != 0) {
						output += connected_printer_serial + delimiter;
					} else {
						output += "No connected printer or not applicable" + delimiter;
					}
					output += mac;
				}
			}
		} else {
			output += "No connection or IP not found";
		}

		return output;
	}
}

class UnrecoverableException extends IOException {

}