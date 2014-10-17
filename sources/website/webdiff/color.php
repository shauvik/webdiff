<html>
<head>
<script>
(function (){
		var sty = document.createElement("style");
		sty.type = "text/css";
		if (sty.styleSheet) {
		  sty.styleSheet.cssText = "html {overflow-y: scroll;}";
		} else {
		  sty.appendChild(document.createTextNode("html{overflow-y:scroll;overflow:-moz-scrollbars-vertical;}"));
		}
		document.getElementsByTagName("head")[0].appendChild(sty);
	})();
</script>
</head>
<?php
$color = "FFFFFF";
if(isset($_REQUEST['color'])){
  $color=$_REQUEST['color']; 
}
?>
<body bgcolor="#<?php echo $color; ?>">
</body>
</html>
