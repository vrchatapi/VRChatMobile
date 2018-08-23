
var API_URL = "https://www.vrchat.net/api/1/";
var CLIENT_API_KEY = "";
var fetchnum = 100;
var onlinedata = [];
var search = "";

var errorcount = 0;


function getAPIUrl(){
	return API_URL;
}

function sendReq(endpoint,success,error){

	sendReqCommand({type:"none"},"config",
		(data)=>{
			console.log('sendReq');
			console.log(data);

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
function getFriendsnum(){
	return localStorage["friends_num"];
}
function getSearch(){
	return localStorage["search"];
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
function setSearch(val){
	localStorage["search"] = val;
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
			p.success = (data) =>{
						success(data);};
			break;
		case "basic":
			p.headers={
				"Authorization": "Basic " + window.btoa(unescape(encodeURIComponent(user+":"+pass))),
			};
			p.success = (data) =>{
						success(data);};
			break;

		case "auth":
			p.data={auth:token};
			p.processData=true;
			p.success = (data) =>{
						success(data);};
			break;
		case "auth2":
			//p.data={path:"/"};
			p.processData=true;
			p.success = (data) =>{
						success(data);};
			break;
		case "friends":
			p.processData=true;
			p.data = {n:fetchnum,offset:prop.offset,offline:"false"};
			p.success = (data) =>{
					if(data.length >0){
						Array.prototype.push.apply(onlinedata,data);
						fetchOnlinedata_ref(prop.offset+fetchnum,success);
					}else{
						console.log("fetch out");
						success(onlinedata);
					}
			};
			break;
		case "else":
			break;
	}

		p.error = (XMLHttpRequest, textStatus, errorThrown) => {
		        console.log("SendReq type   : " + type);
		        console.log("XMLHttpRequest : " + XMLHttpRequest.status);
		        console.log("textStatus     : " + textStatus);
		        console.log("errorThrown    : " + errorThrown.message);
						console.log("prop           : " + prop);
						console.log("endpoint       : " + endpoint);
				error();
			}
	$.ajax(p);



}


function reqKey(success,error){
	sendReq("config",function(data){
		console.log(data);
		localStorage['clientapikey'] = data["clientApiKey"];
		success(data);
	},error);
}

function setUserdata(data){
	localStorage["token"] = data["authToken"];
	localStorage["user_icon_url"] = data["currentAvatarThumbnailImageUrl"];
	localStorage["user_name"] = data["displayName"];
	localStorage["friends_num"] = data["friends"].length;
	console.log(data);
}
function showInfo(data){
	$("#userpanel").show();
	console.log("xxxx showInfo");
	var icon = localStorage["user_icon_url"];
	var name = localStorage["user_name"];
	var fr_num=localStorage["friends_num"];

	var $img = $("<img>").attr({src: icon,id:"myicon"});
	var $name= $("<div>").text(name);
	var $onfr   = $("<div>").attr({id:"onfr" ,title:"onlinefriends"});
	var $allfr  = $("<div>").attr({id:"allfr",title:"allfriends"}).text(" / " + fr_num + " friends");
	$("#userpanel").empty().append($img).append($name).append($onfr).append($allfr);



}
function reqCookie(success,error){
	var u = getUsername();
	var p = getPassword();
	var k = getClientapikey();

  console.log("reqCookie");

	sendReqCommand({user:u,pass:p,key:k,type:"basic"},"auth/user",(data)=>{
		success(data);
		data = JSON.parse(data);
		console.log(data);
	},error);
}
function fetchOnlinedata(success){
	var key = getClientapikey();
	var token = getToken();
	clientapikey({type:"auth2",key:key},"auth/user/friends",(data)=>{
		console.log(data);
		success(data);
	},()=>{
		reqCookie(
			(data)=>{
				console.log("re fetch");
				fetchOnlinedata(showfriends);
			},()=>{
				loginstatus(false);
				logout();
			}
	);
	});

}
function fetchOnlinedata2(success){
	if (islogin()){
		onlinedata = [];
		var key = getClientapikey();
		var friends_num = getFriendsnum();
		var n = 100;
		var offset = 0;
		fetchOnlinedata_ref(offset,success);
}
}
function fetchOnlinedata_ref(offset,success,after){

	after = after || false;

	if(!islogin()){return}

	console.log("fetchOnlinedata_ref offset = "+ offset);
	var key = getClientapikey();
	sendReqCommand({type:"friends",key:key,offset:offset},"auth/user/friends",success,
		()=>{
			console.log("+++++++++++++++friendsdata fetch2 error");
			if(after){

				loginstatus(false);
				logout();
			}else{
				reqCookie(()=>{
					console.log("reqCookie success");
					fetchOnlinedata_ref(offset,success,true);
				},()=>{
					console.log("reqCookie error");
					loginstatus(false);
					logout();
				});

			}

			/*
			reqCookie(()=>{
				fetchOnlinedata_ref(offset,success);
			},()=>{
				console.log("reqCookie error");
				loginstatus(false);
				logout();
			});
			*/
		});

}
function showfriends(data){
	//$("#frineds").show();
	$("#friends").empty();


	$("#onfr").text(data.length + " online");

	data.sort((a,b)=>{
		return a["displayName"].localeCompare(b["displayName"]);
	});

	$.each(data,function(index,val){
			var $img = $("<img>").addClass("friendicon").attr({
				src:val["currentAvatarThumbnailImageUrl"],
				align:"middle"
			});
			var worldid = worldId(val["location"]);
			var instanceid = instanceId(val["location"]);


			var loc = getWorldname(worldid);
			var $c_img  = $("<div>").addClass("c_img").append($img);
			var $c_text = $("<div>").append($("<div>").text(val["displayName"])).append($("<a>").attr({href:lunchurl(worldid,instanceid),target:"_blank"}).html($("<div>").addClass(worldid).text(preset_in(loc))).append($("<span>").text(" (" + instancestatus(instanceid) + ")")));
			var $c_desc = $("<div>").addClass("friendcontent").append($c_text);
			var $content= $("<div>").append($c_img).append($c_desc);
			var $newli  = $("<li>").append($content);
			$("#friends").append($newli);

	});

	searchfriend($("#vrcnsearch").val());
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
	fetchOnlinedata2(showfriends);

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
	//console.log("*****world ",str);
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
	//console.log("fetch*************",worldid);
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
	//console.log("--------------loc",loc);
	//console.log("getwn ",localStorage[worldid]);
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
function loginsend(u,p,k){
	sendReqCommand({user:u,pass:p,key:k,type:"basic"},"auth/user",(ddd)=>{
		ddd = JSON.parse(ddd);
		setUserdata(ddd);
		loginsuccess(ddd);
	});
}
function hasCookie(){
	var r = document.cookie.split(';');
	r.forEach(function(value) {

    //cookie名と値に分ける
    var content = value.split('=');

	console.log(content);
})
}

function login(){


	var u = getUsername();
	var p = getPassword();
	var k  = getClientapikey();
	var status;
	if (u && p){
			if(k){
			  	loginsend(u,p,k);
			}else{
					reqKey(loginsend(u,p,k));
			}
	}else{
		logout();
	}
/*
		if(t){
			console.log("has token");
			sendReqCommand({user:u,pass:p,key:k,token:t,type:"auth"},"auth/user",
				(data)=>{
					loginsuccess(data);
				},
				()=>{
					reqToken(
						(data)=>{
							loginsuccess(data);
						},()=>{

							loginstatus(false);
							logout();
						}
					);
				}
			);
		}else{
			console.log("the first login");
			reqToken((data)=>{
				loginsuccess(data);

			},()=>{

				loginstatus(false);
				logout();

			})
		}
*/

}

function searchfriend(str){
	var userlists = $("#friends > li > div > div.friendcontent > div > div").show().each(function(i,v){
		var username = v.innerText;
		console.log("username " + username);
		if(username.toLowerCase().indexOf(str) === 0){
			$(v).parents("li").show();
		}else{
			$(v).parents("li").hide();
		}
	});
}



function logout(){
	$("#logoutpanel").hide();
	$("#loginpanel").show();
	$("#friendspanel").hide();
	$("#userpanel").hide();
	loginstatusclear();
	backgroundsend("logout");
}

function loginstatusclear(){
	["clientapikey","friends_num","pass","token","user_icon_url","user_name"].forEach((val)=>{
		localStorage.removeItem(val);
	});
}
function loginstatus(bool) {
	localStorage["islogin"] = bool;
}
function islogin() {
	return JSON.parse(localStorage["islogin"]);
}
function isNotification(){
	return JSON.parse(localStorage["notification"]);
}
