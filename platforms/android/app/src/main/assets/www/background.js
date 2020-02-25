


//background.js

console.log("background");
localStorage["volume"] = 100;
var observerTimer;
var beforefriendsData = [];

var sound = new Audio("sound01.mp3");
// but its broken to hell
function comparedata(data1,data2){

	console.log(data1);
	console.log(data2);

	var retar=[];
	console.log("isarray",data1 instanceof Array);
	var b=data1.map(function(v){return v["username"]});
	var a=data2.map(function(v){return v["username"]});

	console.log("b",b);
	console.log("a",a);

	var newonlineindexarray = compareIndex(b,a);
	console.log("newonlineindexarray",newonlineindexarray);
	for(var j=0;j<newonlineindexarray.length;j++){
		retar.push(data2[newonlineindexarray[j]]);
	}
	return retar;
}
function compareIndex(d1,d2){
	var retarray = [];

	for(var i=0;i<d2.length;i++){
		if(d1.indexOf(d2[i])==-1){
			retarray.push(i);
		};
	}
	return retarray;
}
function notification(data){

	var n = comparedata(beforefriendsData,data);
	console.log("notification!!!");
	console.log(n);
	console.log("---------------");
	for(var i=0;i<n.length;i++){
		createnotification(n[i]);
	}

	beforefriendsData = data;
}
function soundEnable(){
	if (localStorage["sound"]!=null){
		return JSON.parse(localStorage["sound"]);
	}else{
		return true;
	}
}

function soundplay(){
	if(soundEnable()){
		sound.volume = JSON.parse(localStorage["volume"])/100;
		sound.play();
	}

}
function createnotification(data){
	console.log("createnotification",data);
	//chrome.notifications.create(data["username"], {type:"basic",title:data["displayName"],iconUrl:data["currentAvatarThumbnailImageUrl"],message:"is online"});
	//chrome.notifications.create(data["username"], {type:"basic",title:data["displayName"],iconUrl:"icon01.png",message:"is online"});
	var options = {
		body:"is online",
		icon:data["currentAvatarThumbnailImageUrl"],

	}
	var ntf = new Notification(data["displayName"],options);
	soundplay();
}
function observer(){
	console.log("observer")
	observerTimer = setTimeout(()=>{
		fetchOnlinedata(notification);
		observer();
	},30*1000);
}

function init(){

	localStorageinit();

	if(islogin()){
		if(isNotification()){
			fetchOnlinedata((data)=>{
				beforefriendsData = data;
				observer();
			});
		}
	}
}

function localStorageinit(){
	if(localStorage["notification"]==null){
		localStorage["notification"] = "true";
	}
	if(localStorage["volume"]==null){
		localStorage["volume"]=50;
	}
	if(localStorage["sound"]==null){
		localStorage["sound"] = "true";
	}
}


init();
