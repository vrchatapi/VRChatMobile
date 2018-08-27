$(document).ready(function(){

	if(islogin()){
		console.log("login!!");
		$("#loginpanel").hide();
		showInfo();
		fetchOnlinedata(showfriends);
	}else{
		$("#logoutpanel").hide();
	}

	$("button#login").click(function(){
		var user = $("#username").val();
		var pass = $("#password").val();
		setUsername(user);
		setPassword(pass);

		var k = getClientapikey();
		if(k){
			console.log("has apikey ----login()");
			login();
		}else{
			console.log("has not apikey ----reqKey()");
			reqKey(()=>{
				login();
			},()=>{});
		}
	});
	$("button#logout").click(function(){
		logout();
	});


	

	});
