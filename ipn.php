<?php
// Author: Monnier Antoine
// Revision Notes
// 11/04/11 - changed post back url from https://www.paypal.com/cgi-bin/webscr to https://ipnpb.paypal.com/cgi-bin/webscr
// For more info see below:
// https://www.x.com/content/bulletin-ip-address-expansion-paypal-services
// "ACTION REQUIRED: if you are using IPN (Instant Payment Notification) for Order Management and your IPN listener script is behind a firewall that uses ACL (Access Control List) rules which restrict outbound traffic to a limited number of IP addresses, then you may need to do one of the following: 
// To continue posting back to https://www.paypal.com  to perform IPN validation you will need to update your firewall ACL to allow outbound access to *any* IP address for the servers that host your IPN script
// OR Alternatively, you will need to modify  your IPN script to post back IPNs to the newly created URL https://ipnpb.paypal.com using HTTPS (port 443) and update firewall ACL rules to allow outbound access to the ipnpb.paypal.com IP ranges (see end of message)."


/////////////////////////////////////////////////
/////////////Begin Script below./////////////////
/////////////////////////////////////////////////

include('config.php');




// read the post from PayPal system and add 'cmd'
$req = 'cmd=_notify-validate';
foreach ($_POST as $key => $value) {
$value = urlencode(stripslashes($value));
$req .= "&$key=$value";
}
// post back to PayPal system to validate
$header = "POST /cgi-bin/webscr HTTP/1.1\r\n";
$header .= "Content-Type: application/x-www-form-urlencoded\r\n";

	$header .= "Host: www.paypal.com\r\n";  // www.paypal.com for a live site

$header .= "Content-Length: " . strlen($req) . "\r\n";
$header .= "Connection: close\r\n\r\n";

// If testing on Sandbox use:
if($DBsandbox)
{
	$fp = fsockopen ('ssl://www.sandbox.paypal.com', 443, $errno, $errstr, 30);
}
else
{
	$fp = fsockopen ('ssl://www.paypal.com', 443, $errno, $errstr, 30);
}

// assign posted variables to local variables
$item_name[0] = $_POST['item_name1'];
$item_name[1] = $_POST['item_name2'];
$item_name[2] = $_POST['item_name3'];
$item_name[3] = $_POST['item_name4'];
$item_name[4] = $_POST['item_name5'];
$item_name[5] = $_POST['item_name6'];
$item_name[6] = $_POST['item_name7'];
$item_name[7] = $_POST['item_name8'];
$item_name[8] = $_POST['item_name9'];
$item_name[9] = $_POST['item_name10'];
$item_name[10] = $_POST['item_name11'];
$item_name[11] = $_POST['item_name12'];
$item_name[12] = $_POST['item_name13'];
$item_name[13] = $_POST['item_name14'];
$item_name[14] = $_POST['item_name15'];
$item_name[15] = $_POST['item_name16'];
$item_name[16] = $_POST['item_name17'];
$item_name[17] = $_POST['item_name18'];
$item_name[18] = $_POST['item_name19'];
$item_name[19] = $_POST['item_name20'];
$item_name[20] = $_POST['item_name21'];
$item_name[21] = $_POST['item_name22'];
$item_name[22] = $_POST['item_name23'];
$item_name[23] = $_POST['item_name24'];
$item_name[24] = $_POST['item_name25'];
$business = $_POST['business'];
$quantity[0] = $_POST['quantity1'];
$quantity[1] = $_POST['quantity2'];
$quantity[2] = $_POST['quantity3'];
$quantity[3] = $_POST['quantity4'];
$quantity[4] = $_POST['quantity5'];
$quantity[5] = $_POST['quantity6'];
$quantity[6] = $_POST['quantity7'];
$quantity[7] = $_POST['quantity8'];
$quantity[8] = $_POST['quantity9'];
$quantity[9] = $_POST['quantity10'];
$quantity[10] = $_POST['quantity11'];
$quantity[11] = $_POST['quantity12'];
$quantity[12] = $_POST['quantity13'];
$quantity[13] = $_POST['quantity14'];
$quantity[14] = $_POST['quantity15'];
$quantity[15] = $_POST['quantity16'];
$quantity[16] = $_POST['quantity17'];
$quantity[17] = $_POST['quantity18'];
$quantity[18] = $_POST['quantity19'];
$quantity[19] = $_POST['quantity20'];
$quantity[20] = $_POST['quantity21'];
$quantity[21] = $_POST['quantity22'];
$quantity[22] = $_POST['quantity23'];
$quantity[23] = $_POST['quantity24'];
$quantity[24] = $_POST['quantity25'];
$mc_gross[0] = $_POST['mc_gross_1'];
$mc_gross[1] = $_POST['mc_gross_2'];
$mc_gross[2] = $_POST['mc_gross_3'];
$mc_gross[3] = $_POST['mc_gross_4'];
$mc_gross[4] = $_POST['mc_gross_5'];
$mc_gross[5] = $_POST['mc_gross_6'];
$mc_gross[6] = $_POST['mc_gross_7'];
$mc_gross[7] = $_POST['mc_gross_8'];
$mc_gross[8] = $_POST['mc_gross_9'];
$mc_gross[9] = $_POST['mc_gross_10'];
$mc_gross[10] = $_POST['mc_gross_11'];
$mc_gross[11] = $_POST['mc_gross_12'];
$mc_gross[12] = $_POST['mc_gross_13'];
$mc_gross[13] = $_POST['mc_gross_14'];
$mc_gross[14] = $_POST['mc_gross_15'];
$mc_gross[15] = $_POST['mc_gross_16'];
$mc_gross[16] = $_POST['mc_gross_17'];
$mc_gross[17] = $_POST['mc_gross_18'];
$mc_gross[18] = $_POST['mc_gross_19'];
$mc_gross[19] = $_POST['mc_gross_20'];
$mc_gross[20] = $_POST['mc_gross_21'];
$mc_gross[21] = $_POST['mc_gross_22'];
$mc_gross[22] = $_POST['mc_gross_23'];
$mc_gross[23] = $_POST['mc_gross_24'];
$mc_gross[24] = $_POST['mc_gross_25'];
$payment_date = $_POST['payment_date'];
$first_name = $_POST['first_name'];
$last_name = $_POST['last_name'];
$payer_email = $_POST['payer_email'];
$custom = $_POST['custom'];

if (!$fp)
{
}
else
{
	fputs ($fp, $header . $req);
	$found=false;
	while (!feof($fp) && !$found) 
	{
		$res = fgets ($fp, 1024);
		if (stripos($res, "VERIFIED") !== false)
		{
			$length = count($item_name);
			for ($i = 0; $i < $length; $i++) 
			{
				if (($item_name[$i] != "") && ($mc_gross[$i] != ""))
				{
					$quantityLength = $quantity[$i];
					$mc_gross[$i] = ($mc_gross[$i] / $quantity[$i]);
					for ($j = 0; $j < $quantityLength; $j++)
					{
						$found=true;
						//create MySQL connection
						$Connect = @mysql_connect($DB_Server, $DB_Username, $DB_Password)
						or die("Couldn't connect to MySQL:<br>" . mysql_error() . "<br>" . mysql_errno());
						//select database
						$Db = @mysql_select_db($DB_DBName, $Connect)
						or die("Couldn't select database:<br>" . mysql_error(). "<br>" . mysql_errno());
						$fecha = date("m")."/".date("d")."/".date("Y");
						$fecha = date("Y").date("m").date("d");
						//execute query
						if($DBsandbox)
						{
							$sandbox="true";
						}
						else
						{
							$sandbox="false";
						}
                        // Table Structure 1:id(int)*AUTO_INCREMENT, 2:item_name(text), 3:username(tinytext), 4:amount(tinytext), 5:date(text), 6:processed(text), 7:sandbox(text), 8:first_name(text), 9:last_name(text), 10:payer_email(text), 11:expires(text), 12:expired(text), 13:canGet(int)
						$strQuery="INSERT INTO donations (username, item_name, amount, date, processed, sandbox, first_name, last_name, payer_email, expires, expired, canGet)";
						$strQuery.=" VALUES ('".$custom."','".$item_name[$i]."','".$mc_gross[$i]."','".$payment_date."','false','".$sandbox."','".$first_name."','".$last_name."','".$payer_email."','null','false',1);";
						$result=mysql_query($strQuery);
					}
				}
			}
			// send an email in any case
			echo "Verified";
			mail($notify_email, "VERIFIED IPN", "$res\n $req\n $strQuery\n");
		}
		// if the IPN POST was 'INVALID'...do this
		else if (stripos($res, "INVALID") !== false)
		{
			$found=true;
			// log for manual investigation
			mail($notify_email, "INVALID IPN", "$res\n $req");
		}
	}	
	fclose ($fp);
}?>