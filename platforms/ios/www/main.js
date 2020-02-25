var API_URL = "https://api.vrchat.cloud/api/1/"; // Access point to the API.
var CLIENT_API_KEY = ""; // The key required for accessing the VRChat API>
var localstorage = ""; // I don't know what this is.
var debugFriends = false; // For debugging 


function getAPIUrl(){
	return API_URL;
	console.log("Retrieved API URL:" + API_URL);
}

function sendReq(endpoint,success,error){

	sendReqCommand({type:"none"},"config",
		(data)=>{
			localStorage['clientapikey'] = data["clientApiKey"];
			success(data);},error
		);
}
function getUsername(){
	return localStorage['user'];
}
function getPassword(){
	return localStorage['pass']; // Encrypt or hash.
}
function getClientapikey(){
	return localStorage['clientapikey'];
}
function getToken(){
	return localStorage["token"]; // Encrypt or hash.
}

function setUsername(val){
	localStorage["user"] = val;
}
function setPassword(val){
	localStorage["pass"] = val;
}
function setToken(val){
	localStorage["token"] = val;
}

function sendReqCommand(prop,endpoint,success,error){

	var type = prop["type"];
	var user = prop["user"] || "";
	var pass = prop["pass"] || "";
	var key  = prop["key"]  || "";
	var token= prop["token"]|| "";


	var uri = getAPIUrl() + endpoint;

	var p = new Object();
	p.tpe="GET";
	p.url =uri + "?apiKey=" + key;
	p.xhrFields = { withCredentials: true};
	p.crossDomain= true;
	switch(type){
		case "none":
			p.url = uri;
			break;
		case "basic":
			p.headers={
				"Authorization": "Basic " + window.btoa(unescape(encodeURIComponent(user+":"+pass))),
			};
			break;
		case "auth":
			p.data={auth:token};
			p.processData=true;
			break;
		case "else":
			break;
	}
		p.success = (data) =>{
		    	success(data);};
		p.error = (XMLHttpRequest, textStatus, errorThrown) => {
		        console.log("sendReqUser 	: " + user); // Update unreadable characters.
		        console.log("XMLHttpRequest : " + XMLHttpRequest.status);
		        console.log("textStatus     : " + textStatus);
		        console.log("errorThrown    : " + errorThrown.message);
				alert ("Error code " + XMLHttpRequest.status);
				error();
			}
	$.ajax(p);
}


function reqKey(success,error){
	sendReq("config",function(data){
		localStorage['clientapikey'] = data["clientApiKey"];
		success(data);
	},error);
}

var userdata;
function getInfo(success,error){
	var u = getUsername();
	var p = getPassword();
	var k = getClientapikey();

	sendReqCommand({user:u,pass:p,key:k,type:"basic"},"auth/user",(data)=>{
		console.log("logging in with Username " + u);
		userdata = JSON.parse(data);
		console.log(userdata);
		
	localStorage["token"] = userdata["authToken"];
	localStorage["user_icon_url"] = userdata["currentAvatarThumbnailImageUrl"];
	localStorage["user_name"] = userdata["displayName"];
	localStorage["friends_num"] = userdata["friends"].length;
	localStorage["usr_id"] = userdata["id"];
	localStorage["usr_email"] = userdata["obfuscatedEmail"];
	localStorage["usr_passusr"] = userdata["pastDisplayNames"];
	localStorage["usr_usrtype"] = userdata["developerType"];
	localStorage["usr_lastlogin"] = userdata["last_login"];
	localStorage["usr_tags"] = userdata["tags"];
	showInfo(userdata);
	},error);
}

function showInfo(data){
	$("#userpanel").show();
	$("#userpanel2").show(); 
	console.log("Populating User Info......");
	console.log(userdata['displayName']);
	console.log(userdata['currentAvatarThumbnailImageUrl']);
	var icon = userdata['currentAvatarThumbnailImageUrl'];
	var name = userdata['displayName'];
    // Profile Info
	var id = userdata["id"];
	var email = userdata["obfuscatedEmail"];
	var passusr = userdata["pastDisplayNames"];
	var usrtype = userdata["developerType"];
	var usrlastlogin = userdata["last_login"];
	var usrtags = userdata["tags"];
	
	var $img = $("<img>").attr({src: icon,id:"myicon"});
	var $name= $("<div>").text(name);
	var $onfr   = $("<div>").attr({id:"onfr" ,title:"onlinefriends"});
	var $allfr  = $("<div>").attr({id:"allfr",title:"allfriends"}).text( " friends");
	// Profile Info
	var $email = $("<a>Email:</a><input id='text' type='text' style='width:90%' disabled>").attr({value: email});
	var $id = $("<a>User ID:</a><input id='text' type='text' style='width:90%'disabled>").attr({value: id});
	var $pastuser = $("<a>Past Usernames:</a><input id='text' type='text' style='width:90%'disabled>").attr({value: passusr});
	var $usrtype = $("<a>Developer Type:</a><input id='text' type='text' style='width:90%'disabled>").attr({value: usrtype});
	var $lastlogin = $("<a>Last Logged in:</a><input id='text' type='text' style='width:90%'disabled>").attr({value: usrlastlogin});
	var $usrtags = $("<a>User Tags:</a><input id='text' type='text' style='width:90%'disabled>").attr({value: usrtags});

	$("#userpanel").empty().append($img).append($name).append($onfr).append($allfr);
	$("#userpanel2").empty().append($id).append($email).append($pastuser).append($usrtype).append($lastlogin).append($usrtags)
	

}



function reqToken(success,error){
	//basic�F�؂�token���擾�i�X�V�j
	var u = getUsername();
	var p = getPassword();
	var k = getClientapikey();

	sendReqCommand({user:u,pass:p,key:k,type:"basic"},"auth/user",(data)=>{
		success(data);
	},error);
}

function fetchOnlinedata(success){
	var key = getClientapikey();
	var token = getToken();
	sendReqCommand({type:"auth",key:key,token:token},"auth/user/friends",(data)=>{
		console.log(data);
		success(data);
		$("#loading").show();
	},()=>{
		reqToken(
			(data)=>{
				fetchOnlinedata(showfriends);
				$("#loading").hide();
			},()=>{
				loginstatus(false);
				logout();
			}
	);
	});

}



function showfriends(data){
	$("#frineds").show();
	$("#friends").empty();
	if (debugFriends) {
		console.log("Showing friends.");
		console.log(data);
	}

	$("#onfr").text(data.length + " online ");

	data.sort((a,b)=>{ // Add friend sorting in later update (By name, world, ect..)
		return a["displayName"].localeCompare(b["displayName"]);
	});

	$.each(data,function(index,val){
			var $img = $("<img>").addClass("friendicon").attr({
				src:val["currentAvatarThumbnailImageUrl"],
				align:"middle"
			});
			if (debugFriends) {console.log("****name ",val["displayName"]);} // whats with your console logging? I marked it so it would be easyer to find when i have 100lines of errors
			var worldid = worldId(val["location"]);
			var instanceid = instanceId(val["location"]);


			if (debugFriends) {console.log("@@@@", val["location"]);}
			var loc = getWorldname(worldid);
			var $c_img  = $("<div>").addClass("c_img").append($img);
			var $c_text = $("<div>").append($("<div>").text(val["displayName"])).append($("<a>").attr({href:lunchurl(worldid,instanceid),target:"_blank"}).html($("<div>").addClass(worldid).text(preset_in(loc))).append($("<span>").text(" (" + instancestatus(instanceid) + ")")));
			var $c_desc = $("<div>").addClass("friendcontent").append($c_text);
			var $content= $("<div>").append($c_img).append($c_desc);
			var $newli  = $("<li>").append($content);
			$("#friends").append($newli);

	});
}



function preset_in(str){
	return " in " + str;
}

function loginsuccess(data){
	loginstatus(true);

	$("#friendspanel").show();



	$("#loginpanel").hide();
	$("#logoutpanel").show();
	showInfo(userdata);
	fetchOnlinedata();
	fetchOnlinedata(showfriends);

	backgroundsend("login");

}
function esc(str){
  if(typeof str !== 'string') {
    return str;
  }
  return str.replace(/[&'`"<>]/g, function(match) {
    return {
      '&': '&amp;',
      "'": '&#x27;',
      '`': '&#x60;',
      '"': '&quot;',
      '<': '&lt;',
      '>': '&gt;',
    }[match]
  });
}
function loginerror(data){

}


function str2bool(val){
	return val=="true"?true:false;
}

function worldId(str){
	if (debugFriends) {console.log("*****world ",str);}
	return str.split(":")[0];
}
function instanceId(str){
	return str.split(":")[1] || "";

}
function instancestatus(instanceid){
	var status = ""
	if(instanceid==""){return "-----"}
	if(instanceid.indexOf("hidden")!=-1){
			//friends of guests
			status = "friends of guests";
	}else if(instanceid.indexOf("friends")!=-1){
		//friend only
		status =  "friend only";
		
	}else{
		status = "public";
	}

	return status;
}
function lunchurl(worldid,instanceid){
	return "https://vrchat.net/launch?worldId=" + worldid + "&instanceId=" + instanceid;
}

function fetchWorldname(worldid){
	k = getClientapikey();
	t = getToken();
	if (debugFriends) {console.log("fetch*************",worldid);}
	sendReqCommand({type:"auth",key:k,token:t},"worlds/"+worldid,(data)=>{
		var name = data["name"];
		localStorage[worldid] = name;
		$("."+ worldid).text(preset_in(name));
	},()=>{
		if (debugFriends) {console.log("fetchWorldname error");}
	});

}

function getWorldname(worldid){
	var loc = worldid;
	if (debugFriends) {console.log("--------------loc",loc); console.log("getwn ",localStorage[worldid]);}
	var world = localStorage[worldid];

		if(worldid.split("_")[0]=="wrld"){
			loc = world;
			if(world){
				//has localstorage
				loc = world;
			}else{
				loc = worldid;
				setTimeout(()=>{fetchWorldname(worldid)},~~(Math.random()*3000));
			}
		}


	return loc;
}
function login(){


	var u = getUsername();
	var p = getPassword();
	var k  = getClientapikey();
	var status;
	if (u && p){
		var t = getToken();

		if(t){
			console.log("has token");
			sendReqCommand({user:u,pass:p,key:k,token:t,type:"auth"},"auth/user",
				(data)=>{
					//����������serUserdata���ĕ\��
					loginsuccess(data);
				},
				()=>{
					//�ł��Ȃ�������token���Â��Ƃ������ƂȂ̂�reqtoken�ōX�V����
					reqToken(
						(data)=>{
							//�擾�ɐ���������setUserdata���ĕ\��
							loginsuccess(data);
						},()=>{
							//�擾�Ɏ��s�������\��error:���O�C���ł��܂����ł���
							//���O�C�����ʂ� // this is fucking dumb. wtf great comments
							loginstatus(false);
							logout();
						}
					);
				}
			);
		}else{
			//token���Ȃ����΁i���񃍃O�C���j
			console.log("the first login");
			reqToken((data)=>{
				loginsuccess(data);

			},()=>{
				//���s�iapikey���ύX���ꂽ�����O�C�����񂪊Ԉ����Ă����j�Ȃ��΃��O�C�����������Ȃ���
				loginstatus(false);
				logout();

			})
		}

	}
}
function logout(){
	$("#logoutpanel").hide();
	$("#loginpanel").show();
	$("#friendspanel").hide();
	$("#userpanel").hide();
	$("#loading").hide();
	$("#menu").hide();
	localStorage.clear();

	backgroundsend("logout");
}

function loginstatus(bool) {
	localStorage["islogin"] = bool;
}
function islogin() {
	return localStorage["islogin"];
}
function isNotification(){
	return JSON.parse(localStorage["notification"]);
}