
$(document).ready(function(){

	if(islogin()){
		console.log("login - true");
		$("#loginpanel").hide();
		$("#loading").show();
		showInfo();
		$("input#vrcnsearch").val(getSearch()).focus();
		fetchOnlinedata2(showfriends);
	}else{
		$("#logoutpanel").hide();
		$("#loading").hide();
	}

	$("button#login").click(function(){
		var user = $("#username").val();
		var pass = $("#password").val();
		setUsername(user);
		setPassword(pass);

		var k = getClientapikey();
		if(k){
			login();
		}else{
			reqKey(login(),()=>{console.log("reqKey error");});
		}

	});
	$("button#logout").click(function(){
		logout();
	});


	$("#soundenable").prop("checked",JSON.parse(localStorage["sound"]));
	$("#soundenable").click(()=>{
		localStorage["sound"] = $("#soundenable").prop("checked");
	});
	$("#notificationenable").prop("checked",JSON.parse(localStorage["notification"]));
	$("#notificationenable").click(()=>{
		localStorage["notification"] = $("#notificationenable").prop("checked");
		console.log(localStorage["notification"]);
		if(isNotification()){
			console.log("notification_on");
			backgroundsend("notification_on");
		}else{
			console.log("notification_off");
			backgroundsend("notification_off");
		}
	});


	$("#soundvolume").val(JSON.parse(localStorage["volume"]));
	$("#soundvolume").on('input change',()=>{
		localStorage["volume"] = $("#soundvolume").val();
	});
	$("#soundvolume").mouseup(()=>{
		console.log("soundvolume chaneged");
		backgroundsend("soundplay");
	})

	$("#vrcnsearch").keyup(function(){
		var searchstr = $(this).val();
		setSearch(searchstr);
		searchfriend(searchstr);
	});



});
