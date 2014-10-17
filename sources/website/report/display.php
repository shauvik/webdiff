<html>
<head>
<link type="text/css" rel="stylesheet" href="style.css"/>
<script type="text/javascript" src="jquery-1.4.2.min.js"></script>
<!--[if IE]>
    <script src="excanvas.js"></script>
<![endif]-->
<script>
function setUp(){

 //loadImage($('#A')[0],'ff.png');
<?php
  echo "loadImage($('#B')[0],'./screenshots/".$_REQUEST['testid'].".png');";
  echo "loadImage($('#A')[0],'./screenshots/".$_REQUEST['refb'].".png');"
?>
  //loadDiff();
  $('#dA').scroll(function(){
	$('#dB').scrollTop(this.scrollTop);
	$('#dB').scrollLeft(this.scrollLeft);
  });
  $('#dB').scroll(function(){
        $('#dA').scrollTop(this.scrollTop);
        $('#dA').scrollLeft(this.scrollLeft);
  });

}

// Load image into canvas
function loadImage(canvas, image_url, callback){
  var ctx = canvas.getContext('2d');
  var img = new Image();
  img.src = image_url;
  img.onload = function(){
    canvas.height = img.height;
    canvas.width = img.width;
    ctx.drawImage(img,0,0);
    if(callback){ callback(); }
  }
}
//Load cross browser difference on right side
function loadDiff_ref(x0,y0,w,h){
  var ctx = $('#A')[0].getContext('2d');
  //Show things in both that are same
  ctx.strokeStyle = '#f00';
  for(var i=0;i<x0.length;i++){
	//alert(x0[i]+","+y0[i]+","+w[i]+","+h[i]);
//ctx.strokeRect(100,100,100,100);
ctx.strokeRect(x0[i],y0[i],w[i],h[i]);
	}

}
//Load cross browser difference on right side
function loadDiff(x0,y0,w,h){
  var ctx = $('#B')[0].getContext('2d');
  //Show things in both that are same
  ctx.strokeStyle = '#f00';
  for(var i=0;i<x0.length;i++){
	//alert(x0[i]+","+y0[i]+","+w[i]+","+h[i]);
	//ctx.strokeRect(100,100,100,100);
	ctx.strokeRect(x0[i],y0[i],w[i],h[i]);
	}
}
function drawCoords(temp){
	//alert(document.getElementById('coords').value);
	var x0=new Array();
	var y0=new Array();
	var w=new Array();
	var h=new Array();
//var temp=document.getElementById('coords').value
	while(temp.indexOf('(')!=-1){
		var currNo=temp.indexOf('(')+1;
		var comma=temp.indexOf(',');
		var x0Val=temp.substring(currNo,comma);
		temp=temp.substring(comma+1,temp.length);
		//alert(temp);
		comma=temp.indexOf(',');
		var y0Val=temp.substring(0,comma);
		temp=temp.substring(comma+1,temp.length);
		comma=temp.indexOf(',');
		var x1Val=temp.substring(0,comma);
		temp=temp.substring(comma+1,temp.length);
		var width=x1Val-x0Val;	
		comma=temp.indexOf(',');
		var y1Val=temp.substring(0,temp.indexOf(')'));
		var height=y1Val-y0Val;	
		x0.push(x0Val);
		y0.push(y0Val);
		w.push(width);
		h.push(height);
	}
	loadDiff(x0,y0,w,h);
}
function makeOSkin(){
	if(document.getElementById("cType").value=='3'){
		alert("X1XX");
		mydiv = document.getElementById("dB");
		mydiv.style.display = "none"; //to hide it
		var canvas=$('#A')[0];
		var ctx = canvas.getContext('2d');
		ctx.globalAlpha = 0.5; 
		loadImage(canvas,'ie0.png');
		
	}
}
function showHide(id){
    $(".r"+id).toggle();
	$("#i"+id).toggleClass('minus')
}
$(document).ready(setUp);
</script>
</head>
<?php 
require_once "db.php";
?>
<body>

<div id="main">

<div id="dA" style="overflow:scroll;width:50%;height:75%;float:left;">
<canvas id="A"></canvas>
</div>
<div id="dB" style="overflow:scroll;width:50%;height:75%;float:left;">

<!--canvas id="D"></canvas-->
<canvas id="B"></canvas>
</div>
</div>
<div style="clear:both;"></div>
<table class="sclbl" style="height:5%">
  <tr>
    <th><?php echo browserString('ref');?></th>
	<th><?php echo browserString($_REQUEST['user_agent']);?></th>
  </tr>
</table>
<div style="height:20%;width:100%;overflow-y:scroll">
<center>
<table class="results">
<thead>
<tr>
<th bgcolor="#CCCCCC" width="15px">(+/-)</th>
<th bgcolor="#CCCCCC" width="250px">Issue Type</th>
<!--<th bgcolor="#CCCCCC">Browser</th>-->
<th bgcolor="#CCCCCC" width="200px">Screen Location</th>
<th bgcolor="#CCCCCC">DOM XPath</th>
</tr>
</thead>

<?php
printReport();
?>
</table>
</center>
</div>
</body>
</html>
