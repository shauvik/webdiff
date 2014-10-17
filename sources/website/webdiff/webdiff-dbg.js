var allNodes=new Array();
document.expando = true;
var bcc = {
  version:[0.1,20091109],
  init:function(testid){
    var dtObj1=levelorderTraversal(document.body);
    var jsonStr=createJSON();
    jsonStr=testid+jsonStr;
    this.post_data("/webdiff/data.php","data="+jsonStr);
  },
  debug:function(){
    var dtObj1=levelorderTraversal(document.body);
	alert("#Nodes="+allNodes.length);
	var data = "";
	for(var node in allNodes){
		var n = allNodes[node];
		data += n.getId()+ ","+ n.getNodeName()+","+n.xpath+";";
	}
	alert(data);
	this.post_data("/webdiff/log.php","data="+data);
    var jsonStr=createJSON();
	alert(jsonStr);
	this.post_data("/webdiff/log.php","json="+jsonStr);
  },
  xhr:false,
  supports_web_workers:function() {
    return !!window.Worker;
  },
  supports_local_storage:function() {
    return !!window.localStorage;
  },
  post_data:function(url,qs){
	  var getdate = new Date();  //Used to prevent caching during ajax call
	  this.getXHR();
	  if(this.xhr) { 
		this.xhr.open("POST",url,true);
		this.xhr.onreadystatechange  = function () {
	    	   if (bcc.xhr.readyState == 4) {
	    		     if(bcc.xhr.status == 200) {
	    		       //alert(bcc.xhr.responseText); //Update the HTML Form element 
	    		     }
	    		     else {
	    		    	 bcc.post_data("/webdiff/error.php","Error_during_AJAX_call.");
	    		     }
	    		   }
	    		};
	    this.xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	    this.xhr.send(qs);
	  }
  },
  getXHR:function(){
     if(this.xhr != false)
    	 return this.xhr;
     if(typeof XMLHttpRequest != 'undefined'){
    	 this.xhr = new XMLHttpRequest();        //For Mozilla, Opera Browsers
     }else{
  	   try {
  		 this.xhr = new ActiveXObject("Microsoft.XMLHTTP")  // For Microsoft IE 6.0+
  	   } catch (e){
  	     try {
  	    	this.xhr = new ActiveXObject("Msxml2.XMLHTTP")  // For Old Microsoft Browsers
  	     } catch (e2) {
  	    	this.xhr = false   // No Browser accepts the XMLHTTP Object then false
  	     }
  	   }
     }
     return this.xhr;  // Mandatory Statement returning the ajax object created
  }
};

//Always display scrollbars
(function (){
	var sty = document.createElement("style");
	sty.type = "text/css";
	if (sty.styleSheet) { //IE
	  sty.styleSheet.cssText = "html {overflow-y: scroll;}";
	} else { 
	  sty.appendChild(document.createTextNode("html{overflow-y:scroll;overflow:-moz-scrollbars-vertical;}"));
	}
	document.getElementsByTagName("head")[0].appendChild(sty);
})();

//Remove focus from all text fields - for scrolling
(function(){
	var i = document.getElementsByTagName("input");
	for(inp in i){
		if(i[inp].blur) i[inp].blur();
	}
})();

//Resize handler
/*
(function(){
	var postOnChange = function(){
		var db = document.body;
		var data = db.clientWidth+','+db.clientHeight+','+db.scrollWidth+','+db.scrollHeight;
		bcc.post_data('/resized/'+data, '');
	}
	if (window.addEventListener){
		window.addEventListener('resize', postOnChange, false); 
	} else if (window.attachEvent){
		window.attachEvent('onresize', postOnChange);
	}
}());
*/

function getElementXPath(elt)
{
     var path = "";
     for (; elt && elt.nodeType == 1; elt = elt.parentNode)
     {
   	var idx = getElementIdx(elt);
	var xname = elt.tagName;
	if (idx > 1) xname += "[" + idx + "]";
	path = "/" + xname + path;
     }
 
     return path;	
};
function getElementIdx(elt)
{
    var count = 1;
    for (var sib = elt.previousSibling; sib ; sib = sib.previousSibling)
    {
        if(sib.nodeType == 1 && sib.tagName == elt.tagName)	count++;
    }
    
    return count;
};

function checkIsClickable(node){
	
	if(node.nodeType == 1){
	
		if(node && node.attributes){
			var attr = "";
			if(node.getAttribute('onclick')){
				return true;
			}
		}
	// FIREFOX PATCH NEEDED :
	// https://bugzilla.mozilla.org/attachment.cgi?id=347341
	// https://bugzilla.mozilla.org/show_bug.cgi?id=448602
//	var els = getEventListenerService();
//    if (els){
//    	var infos = els.getListenerInfoFor(node);
//    	for (var i = 0; i < infos.length; i++){
//            var anInfo = infos[i];
//            //alert(node+">>"+anInfo);
//        }
//    }
}
	return "-1";
}


function __parseBorderWidth(width) {
    var res = 0;
    if (typeof(width) == "string" && width != null && width != "" ) {
        var p = width.indexOf("px");
        if (p >= 0) {
            res = parseInt(width.substring(0, p));
        }
        else {
     		//do not know how to calculate other values (such as 0.5em or 0.1cm) correctly now
    		//so just set the width to 1 pixel
            res = 1; 
        }
    }
    return res;
}


//returns border width for some element
function __getBorderWidth(element) {
	var res = new Object();
	res.left = 0; res.top = 0; res.right = 0; res.bottom = 0;
	if (window.getComputedStyle) {
		//for Firefox
		var elStyle = window.getComputedStyle(element, null);
		res.left = parseInt(elStyle.borderLeftWidth.slice(0, -2));  
		res.top = parseInt(elStyle.borderTopWidth.slice(0, -2));  
		res.right = parseInt(elStyle.borderRightWidth.slice(0, -2));  
		res.bottom = parseInt(elStyle.borderBottomWidth.slice(0, -2));  
	}else {
		//for other browsers
		res.left = __parseBorderWidth(element.style.borderLeftWidth);
		res.top = __parseBorderWidth(element.style.borderTopWidth);
		res.right = __parseBorderWidth(element.style.borderRightWidth);
		res.bottom = __parseBorderWidth(element.style.borderBottomWidth);
	}
	return res;
}


//returns absolute position of some element within document
function getAbsolutePos(element) {
	var __isIE =  navigator.appVersion.match(/MSIE/);
	var __userAgent = navigator.userAgent;
	var __isFireFox = __userAgent.match(/firefox/i);
	var __isFireFoxOld = __isFireFox && (__userAgent.match(/firefox\/2./i) || __userAgent.match(/firefox\/1./i));
	var __isFireFoxNew = __isFireFox && !__isFireFoxOld;
	var res = new Object();
	res.x = 0; res.y = 0;
	if (element !== null) {
		res.x = element.offsetLeft;
		res.y = element.offsetTop;
    	
		var offsetParent = element.offsetParent;
		var parentNode = element.parentNode;
		var borderWidth = null;

		while (offsetParent != null) {
			res.x += offsetParent.offsetLeft;
			res.y += offsetParent.offsetTop;
			
			var parentTagName = offsetParent.tagName.toLowerCase();	

			if ((__isIE && parentTagName != "table") || (__isFireFoxNew && parentTagName == "td")) {		    
				borderWidth = __getBorderWidth(offsetParent);
				res.x += borderWidth.left;
				res.y += borderWidth.top;
			}
		    
			if (offsetParent != document.body && offsetParent != document.documentElement) {
				res.x -= offsetParent.scrollLeft;
				res.y -= offsetParent.scrollTop;
			}

			//next lines are necessary to support FireFox problem with offsetParent
   			if (!__isIE) {
    			while (offsetParent != parentNode && parentNode !== null) {
					res.x -= parentNode.scrollLeft;
					res.y -= parentNode.scrollTop;
					
					if (__isFireFoxOld) {
						borderWidth = _getBorderWidth(parentNode);
						res.x += borderWidth.left;
						res.y += borderWidth.top;
					}
    				parentNode = parentNode.parentNode;
    			}    
			}

   			parentNode = offsetParent.parentNode;
    		offsetParent = offsetParent.offsetParent;
		}
	}
    return res;
}
function getDOMCoords(node){
var curleft=0;
var curtop=0;

try{
	var rectObject = node.getBoundingClientRect(); //usednode here is the current element
	var xPos = getAbsolutePos(node).x;
	var yPos = getAbsolutePos(node).y;
	myReturns=new Array();
	myReturns[0]=xPos;
	myReturns[1]=yPos;
	myReturns[2]=node.offsetWidth;
	myReturns[3]=node.offsetHeight;
	return myReturns;
}catch(e){
        var myReturns=new Array();
        myReturns[0]="-1";
        myReturns[1]="-1";
        myReturns[2]="-1";
        myReturns[3]="-1";
        return myReturns;
}
}
function getNodeType(type){
	var nType = new Array();
	nType[1] = "ELEMENT_NODE";
	nType[2] = "ATTRIBUTE_NODE";
	nType[3] = "TEXT_NODE";
	nType[4] = "CDATA_SECTION_NODE";
	nType[5] = "ENTITY_REFERENCE_NODE";
	nType[6] = "ENTITY_NODE";
	nType[7] = "PROCESSING_INSTRUCTION_NODE";
	nType[8] = "COMMENT_NODE";
	nType[9] = "DOCUMENT_NODE";
	nType[10] = "DOCUMENT_TYPE_NODE";
	nType[11] = "DOCUMENT_FRAGMENT_NODE";
	nType[12] = "NOTATION_NODE";

	return nType[type];
}

//Node Object

function Node(arktos,domid,nodeName, id, domNumber,nodeType,x0,y0,x1,y1,clickable,xpath,zindex,visible) {
	if(arktos==null || arktos==undefined){
		this.arktos=0;
	}
	else{
		this.arktos=arktos;
	}
	this.domid=escape(domid); //some wierd sites have special chars in ids
    this.nodeName = nodeName;
    this.id = id;
    this.domNumber=domNumber;
    this.nodeType=nodeType;
    this.x0=x0;
    this.y0=y0;
    this.x1=x1;
    this.y1=y1;
    this.clickable=clickable;
    this.parent=-99;
    this.children=new Array();
    this.xpath=xpath;
    this.zindex=zindex;
    this.visible=visible;
    this.childHash="no hash";
};
Node.prototype.toString = function() {
    return this.nodeName + " " + this.id;
};
Node.prototype.addChildHash = function(childHash) {
	  this.childHash=childHash;
};
Node.prototype.addChildren = function(node) {
	  this.children.push(node);
};
Node.prototype.addParent = function(id) {
	  this.parent=id;
	  for(var i=0;i<allNodes.length;i++){
	  	if(allNodes[i].getId()==id){
	  		allNodes[i].addChildren(this);
	  	}
	  }
};
Node.prototype.getParent = function() {
    return this.parent;
};
Node.prototype.getNodeName = function() {
    return this.nodeName;
};
Node.prototype.addToChildData = function() {
	var childNodes=allNodes[this.getId()].getChildren();
	var str="";
	for(var x=0;x<childNodes.length;x++){
		if(x<childNodes.length-1){
			str+='{ \"arktos\":"'+ childNodes[x].arktos+'", \"domid\":"'+ childNodes[x].domid+'",\"id\":"'+ childNodes[x].getId()+'",\n \"name\":"'+ childNodes[x].getNodeName()+'",\n \"data\": {\"childhash\":"'+childNodes[x].childHash+'",\n \"type\":"'+childNodes[x].nodeType+'",\n \"x0\":"'+childNodes[x].x0+'",\n \"y0\":"'+childNodes[x].y0+'",\n \"x1\":"'+childNodes[x].x1+'",\n \"y1\":"'+childNodes[x].y1+'",\n \"isClickable\":"'+childNodes[x].clickable+'",\n \"isVisible\":"'+childNodes[x].visible+'",\n \"parentId\":"'+childNodes[x].parent+'",\n \"childElements\":"'+childNodes[x].getChildren()+'",\n \"xpath\":"'+childNodes[x].xpath+'",\n \"zindex\":"'+childNodes[x].zindex+'\"},\n \"children\":['+childNodes[x].addToChildData()+']}, ';
		}
		else{
		str+='{ \"arktos\":"'+ childNodes[x].arktos+'",\"domid\":"'+ childNodes[x].domid+'",\"id\":"'+ childNodes[x].getId()+'",\n \"name\":"'+ childNodes[x].getNodeName()+'",\n \"data\": {\"childhash\":"'+childNodes[x].childHash+'",\n \"type\":"'+childNodes[x].nodeType+'",\n \"x0\":"'+childNodes[x].x0+'",\n \"y0\":"'+childNodes[x].y0+'",\n \"x1\":"'+childNodes[x].x1+'",\n \"y1\":"'+childNodes[x].y1+'",\n \"isClickable\":"'+childNodes[x].clickable+'",\n \"isVisible\":"'+childNodes[x].visible+'",\n \"parentId\":"'+childNodes[x].parent+'",\n \"childElements\":"'+childNodes[x].getChildren()+'",\n \"xpath\":"'+childNodes[x].xpath+'",\n \"zindex\":"'+childNodes[x].zindex+'\"},\n \"children\":['+childNodes[x].addToChildData()+']} ';
		}
	}
	return str;
};
Node.prototype.getId = function() {
    return this.id;
};

Node.prototype.getChildren = function() {
    return this.children;
};
function checkIsVisible(node){
	if(node.nodeType!=1){
		return "-1";
	}
	else if(node.nodeType==1){	
		var style_opacity = getStyle(node,"opacity");
		var style_visiblity = getStyle(node,"visibility");
		var style_display = getStyle(node,"display");
		//The check below is for IE
		if(typeof(stye_opacity)=='undefined'){
			style_opacity = getStyle(node,"filter");
			//Comes back with nothing since its a layout element
			if(style_opacity.length==0){
				style_opacity=100;
			}
			else{
				style_opacity=style_opacity.substring((style_opacity.indexOf("=")+1),(style_opacity.length-1));
			}
		}
		if((style_opacity!='0') && (style_visiblity != 'hidden') && (style_display != 'none') && (style_visiblity != 'collapse')){
			return "1";
		}
		else{
			return "0";
		}
	}
	else{
		return "0";
	}
	
}
//Non-recursive levelorder traversal of DOM tree
function levelorderTraversal(root) {
var strings4mNode=new Array();
var nodeParent=new Array();
var idNo=1;
////alert("AA");
// Initialize queue to contain root element
var q1 = [root];
// While there are elements in the queue
while(q1.length) {
 var q2 = [];
 
 // For each element in queue
 for(var i=0; i<q1.length; i++) {
   if(q1[i].parentNode.visId==undefined && q1[i].parentNode.nodeName=="HTML"){
   	q1[i].parentNode.visId=0;
   	 returnStuff=getDOMCoords(q1[i].parentNode);
   	allNodes.push(new Node(q1[i].parentNode.getAttribute('arktos'),"notPresent",q1[i].parentNode.nodeName,q1[i].parentNode.visId,-1,q1[i].parentNode.nodeType,returnStuff[0],returnStuff[1],(returnStuff[0]+returnStuff[2]),(returnStuff[1]+returnStuff[3]),0,getElementXPath(q1[i]),11111111,1));
   }else if(q1[i].parentNode.visId==undefined && q1[i].parentNode.nodeName=="BODY"){
    returnStuff=getDOMCoords(q1[i].parentNode);
   	q1[i].parentNode.visId=1;
   	var n=new Node(q1[i].parentNode.getAttribute('arktos'),"notPresent",q1[i].parentNode.nodeName,q1[i].parentNode.visId,-1,q1[i].parentNode.nodeType,returnStuff[0],returnStuff[1],(returnStuff[0]+returnStuff[2]),(returnStuff[1]+returnStuff[3]),0,getElementXPath(q1[i]),11111111,1);
   	n.addParent(0);
   	allNodes.push(n);
   	idNo++;
   } else if(q1[i].nodeName=="#text"){
	   try{
		   if(strings4mNode[q1[i].parentNode.visId]!=null){
			strings4mNode[q1[i].parentNode.visId]+=q1[i].nodeValue;
		   }else{
			   strings4mNode[q1[i].parentNode.visId]=q1[i].nodeValue;
		   }
		   var insNode=-1;
		   for(var x=0;x<allNodes.length;x++){
			   if(allNodes[x].getId()==q1[i].parentNode.visId){
				   insNode=x;
				   break;
			   }
		   }
		   nodeParent[q1[i].parentNode.visId]=allNodes[x];
	   }
	   catch(e){
//	   alert("XX after visId"+q1[i].nodeName);
	   }
   }
   else{
  q1[i].visId=idNo;
  var clickVar=1;
 if(checkIsClickable(q1[i])=='-1')
 {
 	clickVar=0;
 }
 returnStuff=getDOMCoords(q1[i]);
 	var style = getStyle(q1[i],"z-index");
 	var zIndexVal=-1111111;
 	var visiblityVar=checkIsVisible(q1[i]);
			if(style){ //null check
				zIndexVal = parseInt(style); //getPropertyValue("z-index");
				if(isNaN(zIndexVal)){
					zIndexVal = 0;
				}
			}

	try{
  		var n=new Node(q1[i].getAttribute('arktos'),q1[i].id,q1[i].nodeName,q1[i].visId,-1,q1[i].nodeType, returnStuff[0],returnStuff[1],(returnStuff[0]+returnStuff[2]),(returnStuff[1]+returnStuff[3]),clickVar,getElementXPath(q1[i]),zIndexVal,visiblityVar);
	}catch(e){
		// For handling comment tags that do not have an arktos id
  		var n=new Node(0,q1[i].id,q1[i].nodeName,q1[i].visId,-1,q1[i].nodeType, returnStuff[0],returnStuff[1],(returnStuff[0]+returnStuff[2]),(returnStuff[1]+returnStuff[3]),clickVar,getElementXPath(q1[i]),zIndexVal,visiblityVar);
	}
   	n.addParent(q1[i].parentNode.visId);
   	
   		allNodes.push(n);
   
   idNo++;
   }
   
   //
   // Do something with node q[i]
   //
   // Create new queue with childnodes of elements in queue
   for(var j=0; j<q1[i].childNodes.length; j++)
     q2.push(q1[i].childNodes[j]);
 }
 q1 = q2;
}
		for(var x=0;x<nodeParent.length;x++)
		{
			if(nodeParent[x]!=null)
			{
				var crc=0;
				if(strings4mNode[x].length>10240){
					var currLocStart=0;
					var currLocEnd=10240;
					while(strings4mNode[x].length>0){
						crc+=Crc32Str(strings4mNode[x].substring(0,10240));
						strings4mNode[x]=strings4mNode[x].substring(10240);
					}
					nodeParent[x].addChildHash(Hex32(crc));
				}
				else
				{
					
				nodeParent[x].addChildHash(Hex32(Crc32Str(strings4mNode[x])));
				}
			}
		}
		
}


function getStyle(oElm, strCssRule){
	var strValue = "";
	if(document.defaultView && document.defaultView.getComputedStyle && oElm.nodeName!="#comment"){
		strValue = document.defaultView.getComputedStyle(oElm, "").getPropertyValue(strCssRule);
		}
	else if(oElm.currentStyle){
		strCssRule = strCssRule.replace(/\-(\w)/g, function (strMatch, p1){
			return p1.toUpperCase();
		});
		strValue = oElm.currentStyle[strCssRule];
	}
	return strValue;
}


/**Creating a JSON string**/
function createJSON(){
	var jsonString='{\"arktos\":"'+ allNodes[0].arktos+'",\"domid\":"'+ allNodes[0].domid+'",\"id\":"'+ allNodes[0].getId()+'",\n \"name\":"'+ allNodes[0].getNodeName()+'",\n \"data\": {},\n \"children\":['+allNodes[0].addToChildData()+']}';
	return jsonString;
}


var Crc32Tab = new Array(
0x00000000,0x77073096,0xEE0E612C,0x990951BA,0x076DC419,0x706AF48F,0xE963A535,0x9E6495A3,
0x0EDB8832,0x79DCB8A4,0xE0D5E91E,0x97D2D988,0x09B64C2B,0x7EB17CBD,0xE7B82D07,0x90BF1D91,
0x1DB71064,0x6AB020F2,0xF3B97148,0x84BE41DE,0x1ADAD47D,0x6DDDE4EB,0xF4D4B551,0x83D385C7,
0x136C9856,0x646BA8C0,0xFD62F97A,0x8A65C9EC,0x14015C4F,0x63066CD9,0xFA0F3D63,0x8D080DF5,
0x3B6E20C8,0x4C69105E,0xD56041E4,0xA2677172,0x3C03E4D1,0x4B04D447,0xD20D85FD,0xA50AB56B,
0x35B5A8FA,0x42B2986C,0xDBBBC9D6,0xACBCF940,0x32D86CE3,0x45DF5C75,0xDCD60DCF,0xABD13D59,
0x26D930AC,0x51DE003A,0xC8D75180,0xBFD06116,0x21B4F4B5,0x56B3C423,0xCFBA9599,0xB8BDA50F,
0x2802B89E,0x5F058808,0xC60CD9B2,0xB10BE924,0x2F6F7C87,0x58684C11,0xC1611DAB,0xB6662D3D,
0x76DC4190,0x01DB7106,0x98D220BC,0xEFD5102A,0x71B18589,0x06B6B51F,0x9FBFE4A5,0xE8B8D433,
0x7807C9A2,0x0F00F934,0x9609A88E,0xE10E9818,0x7F6A0DBB,0x086D3D2D,0x91646C97,0xE6635C01,
0x6B6B51F4,0x1C6C6162,0x856530D8,0xF262004E,0x6C0695ED,0x1B01A57B,0x8208F4C1,0xF50FC457,
0x65B0D9C6,0x12B7E950,0x8BBEB8EA,0xFCB9887C,0x62DD1DDF,0x15DA2D49,0x8CD37CF3,0xFBD44C65,
0x4DB26158,0x3AB551CE,0xA3BC0074,0xD4BB30E2,0x4ADFA541,0x3DD895D7,0xA4D1C46D,0xD3D6F4FB,
0x4369E96A,0x346ED9FC,0xAD678846,0xDA60B8D0,0x44042D73,0x33031DE5,0xAA0A4C5F,0xDD0D7CC9,
0x5005713C,0x270241AA,0xBE0B1010,0xC90C2086,0x5768B525,0x206F85B3,0xB966D409,0xCE61E49F,
0x5EDEF90E,0x29D9C998,0xB0D09822,0xC7D7A8B4,0x59B33D17,0x2EB40D81,0xB7BD5C3B,0xC0BA6CAD,
0xEDB88320,0x9ABFB3B6,0x03B6E20C,0x74B1D29A,0xEAD54739,0x9DD277AF,0x04DB2615,0x73DC1683,
0xE3630B12,0x94643B84,0x0D6D6A3E,0x7A6A5AA8,0xE40ECF0B,0x9309FF9D,0x0A00AE27,0x7D079EB1,
0xF00F9344,0x8708A3D2,0x1E01F268,0x6906C2FE,0xF762575D,0x806567CB,0x196C3671,0x6E6B06E7,
0xFED41B76,0x89D32BE0,0x10DA7A5A,0x67DD4ACC,0xF9B9DF6F,0x8EBEEFF9,0x17B7BE43,0x60B08ED5,
0xD6D6A3E8,0xA1D1937E,0x38D8C2C4,0x4FDFF252,0xD1BB67F1,0xA6BC5767,0x3FB506DD,0x48B2364B,
0xD80D2BDA,0xAF0A1B4C,0x36034AF6,0x41047A60,0xDF60EFC3,0xA867DF55,0x316E8EEF,0x4669BE79,
0xCB61B38C,0xBC66831A,0x256FD2A0,0x5268E236,0xCC0C7795,0xBB0B4703,0x220216B9,0x5505262F,
0xC5BA3BBE,0xB2BD0B28,0x2BB45A92,0x5CB36A04,0xC2D7FFA7,0xB5D0CF31,0x2CD99E8B,0x5BDEAE1D,
0x9B64C2B0,0xEC63F226,0x756AA39C,0x026D930A,0x9C0906A9,0xEB0E363F,0x72076785,0x05005713,
0x95BF4A82,0xE2B87A14,0x7BB12BAE,0x0CB61B38,0x92D28E9B,0xE5D5BE0D,0x7CDCEFB7,0x0BDBDF21,
0x86D3D2D4,0xF1D4E242,0x68DDB3F8,0x1FDA836E,0x81BE16CD,0xF6B9265B,0x6FB077E1,0x18B74777,
0x88085AE6,0xFF0F6A70,0x66063BCA,0x11010B5C,0x8F659EFF,0xF862AE69,0x616BFFD3,0x166CCF45,
0xA00AE278,0xD70DD2EE,0x4E048354,0x3903B3C2,0xA7672661,0xD06016F7,0x4969474D,0x3E6E77DB,
0xAED16A4A,0xD9D65ADC,0x40DF0B66,0x37D83BF0,0xA9BCAE53,0xDEBB9EC5,0x47B2CF7F,0x30B5FFE9,
0xBDBDF21C,0xCABAC28A,0x53B39330,0x24B4A3A6,0xBAD03605,0xCDD70693,0x54DE5729,0x23D967BF,
0xB3667A2E,0xC4614AB8,0x5D681B02,0x2A6F2B94,0xB40BBE37,0xC30C8EA1,0x5A05DF1B,0x2D02EF8D);

function Crc32Add(crc,c){
	return Crc32Tab[(crc^c)&0xFF]^((crc>>8)&0xFFFFFF);
}

function Crc32Str(str){
	var n, crc;
	var len=str.length;
	crc=0xFFFFFFFF;
	for (n=0; n<len; n++){
	  crc=Crc32Add(crc,str.charCodeAt(n));
	}
	return crc^0xFFFFFFFF;
}

function Hex32(val){
	var n, str1, str2;
	n=val&0xFFFF;
	str1=n.toString(16).toUpperCase();
	while (str1.length<4){
	  str1="0"+str1;
	}
	n=(val>>>16)&0xFFFF;
	str2=n.toString(16).toUpperCase();
	while (str2.length<4){
	  str2="0"+str2;
	}
	return "0x"+str2+str1;
}
