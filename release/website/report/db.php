<?php
class db_conn{
	public function __construct(){
		require_once "inc/dbconn.php";
	}
	public function getQuery($q){
		$result=mysql_query($q);
		$retXML="<xml><records>".mysql_num_rows($result)."</records><data>";
		while($row=mysql_fetch_assoc($result)){
			$retXML.="<row>";
			foreach($row as $key => $value){
			 $retXML.="<".$key.">".$value."</".$key.">";
			}
			$retXML.="</row>";
		}
		$retXML.="</data></xml>";
		return $retXML;
	}
	public function insQuery($q){
		$result=mysql_query($q);
		
	}
}


function intToIssueType($int){
	switch($int){
		case 4:
			return "Positional";
		case 5:
			return "Visibility";
		case 6:
			return "Size";
		case 7:
			return "Appearance";
		default:
			return "Unknown";
	}
}

function browserString($key){
	switch($key){
		case "ie": return "Internet Explorer";
		case "gc": return "Google Chrome";
		case "ref":
		case "ff": return "Mozilla Firefox";
		default: return "Unknown";
	}
}

function printReport(){
	$dbWrap=new db_conn();
	$q = "SELECT nodeid, coords, xpath from report where testid='".$_REQUEST['testid']."';";
	$iXml=$dbWrap->getQuery($q);
	//echo $q;
	$iXml=simplexml_load_string($iXml);

	$nodes = array();
	foreach($iXml->data->row as $row){
		array_push($nodes, array($row->nodeid, $row->coords, $row->xpath));
	}
	$ctr =0;
	foreach($nodes as $node){
		$ret = getIssuesWithIds($node[0], $ctr);
		echo "\n<tr class='cluster'><td id='i$ctr' class='plus' onclick='showHide(\"$ctr\")'></td><td>".$ret[1]."</td><td><a href='javascript:drawCoords(\"".$node[1]."\")'>".$node[1]."</a></td><td>".$node[2]."</td></tr>";
		echo $ret[0];
		$ctr++;
	}
	if($ctr==0){
		echo "\n<tr class='cluster'><td></td><td colspan='3'>No issues found.</td></tr>";
	}
}


function getIssuesWithIds($ids, $ctr){
	//TODO: sanitize ids
	$dbWrap=new db_conn();
	$q = "SELECT position1, xpath1, `type` from issues where testid='".$_REQUEST['testid']."' and id in (".$ids.");";
	$iXml=$dbWrap->getQuery($q);
	//echo $q;
	$iXml=simplexml_load_string($iXml);

	$issues = array(4=>array(), 5=>array(), 6=>array(), 7=>array());
	$xpath=array(4=>array(), 5=>array(), 6=>array(), 7=>array());
	foreach($iXml->data->row as $row){
		array_push($issues[intval($row->type)], $row->position1);
		array_push($xpath[intval($row->type)],$row->xpath1);
	}
	$x=0;
	$out = "";
	$cat = array();
	foreach(array_keys($issues) as $key){
		for($i=0; $i < count($issues[$key]); $i++){
			$type = intToIssueType($key);
			$out.= "\n\t<tr class='".(($x%2==0)?"even":"odd")." r$ctr' style='display:none'><td></td><td>".$type."</td><td><a href='javascript:drawCoords(\"".$issues[$key][$i]."\")'>".$issues[$key][$i]."</td><td>".$xpath[$key][$i]."</td></tr>";
			if(!in_array($type, $cat)){
				array_push($cat,$type);
			}
			$x++;
		}
	}
	return array($out, implode(", ", $cat), $x);
}
?>
