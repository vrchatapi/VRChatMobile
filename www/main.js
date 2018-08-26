var API_URL = "https://www.vrchat.net/api/1/";
var CLIENT_API_KEY = "";



function getAPIUrl(){
	return API_URL;
}

function sendReq(endpoint,success,error){

	sendReqCommand({type:"none"},"config",
		(data)=>{
			localStorage['clientapikey'] = data["clientApiKey"];
			success(data);},error
		);

	//var uri = getAPIUrl() + endpoint;
	//
	//$.ajax({
	//    url: uri,
	//    type: 'GET',
	//    success: (data)=>success(data),
	//    error: (data)=>error(data)
	//});

}
function getUsername(){
	return localStorage['user'];
}
function getPassword(){
	return localStorage['pass'];
}
function getClientapikey(){
	return localStorage['clientapikey'];
}
function getToken(){
	return localStorage["token"];
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
	p.type="GET";
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
		        console.log("sendReqUser : ajax�ʐM�Ɏ��s���܂��� ");
		        console.log("XMLHttpRequest : " + XMLHttpRequest.status);
		        console.log("textStatus     : " + textStatus);
		        console.log("errorThrown    : " + errorThrown.message);
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

function setUserdata(data){
	localStorage["token"] = data["authToken"];
	localStorage["user_icon_url"] = data["currentAvatarThumbnailImageUrl"];
	localStorage["user_name"] = data["displayName"].length;
	localStorage["friends_num"] = data["friends"].length;
	console.log(data);
}
function showInfo(data){
	$("#userpanel").show();
	console.log("xxxx showInfo");
	var icon = localStorage["user_icon_url"];
	var name = localStorage["user_name"];
	var fr_num = localStorage["friends_num"];
	console.log(name)

	var $img = $("<img>").attr({src: icon,id:"myicon"});
	var $name= $("<div>").text("N/A");
	var $onfr   = $("<div>").attr({id:"onfr" ,title:"onlinefriends"});
	var $allfr  = $("<div>").attr({id:"allfr",title:"allfriends"}).text(" Friends");
	$("#userpanel").empty().append($img).append($name).append($onfr).append($allfr);



}
function reqToken(success,error){
	//basic�F�؂�token���擾�i�X�V�j
	var u = getUsername();
	var p = getPassword();
	var k = getClientapikey();

	sendReqCommand({user:u,pass:p,key:k,type:"basic"},"auth/user",(data)=>{
		setUserdata(data);
		success(data);
	},error);
}
function fetchOnlinedata(success){
	var key = getClientapikey();
	var token = getToken();
	sendReqCommand({type:"auth",key:key,token:token},"auth/user/friends",(data)=>{
		console.log(data);
		success(data);
	},()=>{
		reqToken(
			(data)=>{
				fetchOnlinedata(showfriends);
			},()=>{
				//�擾�Ɏ��s�������\��error:���O�C���ł��܂����ł���
				//���O�C�����ʂ�
				loginstatus(false);
				logout();
			}
	);
	});

}
function showfriends(data){
	$("#frineds").show();
	$("#friends").empty();
	console.log("xxxxxxxshow friends");
	console.log(data);

	$("#onfr").text(data.length + " online");

	data.sort((a,b)=>{
		return a["displayName"].localeCompare(b["displayName"]);
	});

	$.each(data,function(index,val){
			var $img = $("<img>").addClass("friendicon").attr({
				src:val["currentAvatarThumbnailImageUrl"],
				align:"middle"
			});
			console.log("name ",val["displayName"]);
			var worldid = worldId(val["location"]);
			var instanceid = instanceId(val["location"]);


			console.log("@@@@", val["location"]);
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
	showInfo(data);
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
function backgroundsend(val){
chrome.runtime.sendMessage({
		event:val
	});

}


function str2bool(val){
	return val=="true"?true:false;
}

function worldId(str){
	console.log("*****world ",str);
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
	return "https://www.vrchat.net/launch?worldId=" + worldid + "&instanceId=" + instanceid;
}

function fetchWorldname(worldid){
	k = getClientapikey();
	t = getToken();
	console.log("fetch*************",worldid);
	sendReqCommand({type:"auth",key:k,token:t},"worlds/"+worldid,(data)=>{
		var name = data["name"];
		localStorage[worldid] = name;
		$("."+ worldid).text(preset_in(name));
	},()=>{
		console.log("fetchWorldname error");
	});

}

function getWorldname(worldid){
	var loc = worldid;
	console.log("--------------loc",loc);
	console.log("getwn ",localStorage[worldid]);
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
		//localstorage�Ƀ��O�C�����񂪂����Ȃ���
		//token���񂪂��邩�m�F
		var t = getToken();

		if(t){
			//token�������΂���token�Ńt�����h�������擾
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
							//setUserdata(data);
							loginsuccess(data);
						},()=>{
							//�擾�Ɏ��s�������\��error:���O�C���ł��܂����ł���
							//���O�C�����ʂ�
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
	$("#profile").hide();
	loginstatusclear();
	backgroundsend("logout");
}

function loginstatusclear(){
	["clientapikey","friends_num","islogin","pass","token","user_icon_url","user_name"].forEach((val)=>{
		localStorage.removeItem(val);
	});
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
