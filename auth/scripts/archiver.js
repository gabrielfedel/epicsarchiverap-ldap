
function authorize() {

	var name = $('#username').val();
	var pass = $('#password').val();

	var authentication = false;
	
	$.ajax({
		type: 'post',
		url: '/authorization.php',
		async: false,
		timeout: 5000,
		data: {
			username:name,
			password:pass
		},
		success: function (response) {
			if (response.indexOf("Authenticated") != -1) {
				authentication = true;
			}
			else {
				authentication = false;
				alert(response);
			}
				
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert (textStatus)       		 	;
			authentication = false;
		}
	});

	
	return authentication;

}
