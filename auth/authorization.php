<?php

$connection_address = "ldap://10.0.4.69";


if (isset($_POST["username"]) && isset($_POST["password"])) {
	
	$ldap = ldap_connect($connection_address);

	$user = "cn=".$_POST['username'].",cn=archiver,cn=users,dc=controle,dc=lnls,dc=br";
	$password = $_POST['password'];

	ldap_set_option($ldap, LDAP_OPT_PROTOCOL_VERSION, 3);

	if (ldap_bind($ldap, $user , $password)) {

		echo "Authenticated: " . $_POST['username'];

		ldap_unbind($ldap);
	}
	else echo "Credentials failed with error: (" . ldap_errno($ldap) .") ". ldap_error($ldap);

}
else echo "Not defined credentials";

?>
