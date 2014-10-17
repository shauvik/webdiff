<?php include "inc/head.php" ?>
    <div id="main">
<?php
require_once "db.php";
$dbWrap=new db_conn();
if($_REQUEST['sessid']){
	$xmlbrowsers=$dbWrap->getQuery("SELECT testid,user_agent from tests where sessid='".$_REQUEST['sessid']."';");
	$xmlbrowsers=simplexml_load_string($xmlbrowsers);
	$refbrowser_id=-1;
	foreach($xmlbrowsers->data->row as $row){
		if(strcmp($row->user_agent,"ref")==0){
			$refbrowser_id=$row->testid;
		}

	}
	echo "<h3>Browser report for ".$_REQUEST['url']."</h3>\n<div style='report'>";
	foreach($xmlbrowsers->data->row as $row){
		if(strcmp($row->user_agent,"ie")==0){
			$browser = "Internet Explorer";
		}else if(strcmp($row->user_agent,"gc")==0){
			$browser = "Google Chrome";
		}else{
			continue;
		}
		echo "<div class='report-item'><p>$browser</p><a href='#' onclick='window.open(\"display.php?testid=".$row->testid."&user_agent=gc&refb=".$refbrowser_id."\",\"webdiff\",\"fullscreen=yes,scrollbars=no\");'><img src='screenshots/".$row->testid.".png' width='200px'/></a></div>";
	}
	echo "</div><div class='clear'></div>";
	echo "<p>Select a report to view issue list.</p>";
}else{
	$sessions=$dbWrap->getQuery("SELECT * from sessions;");
	$sessions=simplexml_load_string($sessions);
	echo "<h3>Test Sessions</h3><ul>";
	foreach($sessions->data->row as $row){
		echo "<li><a href='?sessid=".$row->sessid."&url=".$row->url."'>".$row->url."</a></li>";
	}
	echo "</ul>";
}
?>

    </div>

<?php include "inc/foot.php" ?>
