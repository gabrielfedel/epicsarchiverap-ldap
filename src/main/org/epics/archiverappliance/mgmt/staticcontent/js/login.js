function attemptSignUser(e) {

        $.ajax({
                url: '../bpl/login',
                dataType: 'json',
                data: 'username=' + encodeURIComponent(e.data.user.val()) + '&password=' + encodeURIComponent(e.data.password.val()) ,
                async: false,
                type: 'post',
                success: function(data, textStatus, jqXHR) {
                        if (data.validate == "authenticated")
                                window.location = e.data.redirectURL;
                        else {
                            if (data.validate == "forbidden" )
                               $("#loginWarning").text("User does not have enough privileges to edit the archiver parameters.");

                            else
                               $("#loginWarning").text("Invalid credentials");

                            $("#loginWarning").fadeIn ();

                            var timer = setInterval(function () {

                                $("#loginWarning").fadeOut ();
                                clearInterval (timer);

                            }, 5000);
                        }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                        $("#user").text("Anonymus");
                        alert("An error occured on the server while requesting current user -- " + textStatus + " -- " + errorThrown);
                }
        });

}

function signUser() {

	if ($("#user").text() == "Anonymus") {
		return true;
	}
	
	logout();

	return false;
}

function queryUser() {

	$.ajax({
		url: '../bpl/getLoginUsername',
		dataType: 'json',
		async: false,
		type: 'get',
		success: function(data, textStatus, jqXHR) {
			if(data.username != null && data.username != undefined) {
				$("#user").text(data.username);
				$("#log").text("Log out");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#user").text("Anonymus");
			alert("An error occured on the server while requesting current user -- " + textStatus + " -- " + errorThrown);
		}
	});
}

function logout() {

        $.ajax({
                url: '../bpl/logout',
                dataType: 'json',
                async: false,
                type: 'get',
                success: function(data, textStatus, jqXHR) {
                        if(data.username != null && data.username != undefined) {
                                $("#user").text("Anonymus");
                                $("#log").text("Login");
                        }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                        $("#user").text("Anonymus");
                }
        });
}

