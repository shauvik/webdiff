<?php

mysql_connect("localhost", "webdiff", "db_passw0rd");
@mysql_select_db("webdiff") or die("Cannot select DB");

function db_session_exists($sessId){
	$result = mysql_query("SELECT * FROM sessions WHERE sessid LIKE '".$sessId."'");
	return (mysql_num_rows($result) > 0 );
}

function db_session_url($sessId){
	$result = mysql_query("SELECT * FROM sessions WHERE sessid LIKE '".$sessId."'");
	if (mysql_num_rows($result) > 0 ){
		$row = mysql_fetch_assoc($result);
		return $row['url'];
	}else{
		return "NULL";
	}
}

function rand_str($length = 4, $chars = 'abcdefghijklmnopqrstuvwxyz1234567890'){
	// Length of character list
	$chars_length = (strlen($chars) - 1);

	// Start our string
	$string = $chars{rand(0, $chars_length)};

	// Generate random string
	for ($i = 1; $i < $length; $i = strlen($string)){
		// Grab a random character from our list
		$r = $chars{rand(0, $chars_length)};
			
		// Make sure the same two characters don't appear next to each other
		if ($r != $string{$i - 1}) $string .=  $r;
	}

	// Return the string
	return $string;
}
function addDomDataToDB($data, $testid){
  $query = "INSERT INTO domdata (arktos,domid,testid, id, name, type, x0, y0, x1, y1, clickable, visible, parentid, children, xpath, zindex, contenthash) VALUES ";
  $worklist = array($data);
  $list = "";
  $totalCnt = 0;
  $cnt = 0;
  while(sizeof($worklist) > 0){
    $node = array_shift($worklist);
    $data = $node['data'];
    if($node['name'] != "HTML"){
      $cnt++;
      $list .= "(".$node['arktos'].",'".$node['domid']."',".$testid.",".$node['id'].",'".$node['name']."',".$data['type'].",".$data['x0'].",".$data['y0'].",".$data['x1'].",".$data['y1'].",".$data['isClickable'].",".$data['isVisible'].",".$data['parentId'].",'".$data['childElements']."','".$data['xpath']."',".$data['zindex'].",'".$data['childhash']."'), \n";
    }
	echo "<h1>".gettype($node)."</h1>";
	print "<br>".$node['id'].">".gettype($node['children'])."<br>";
	echo "<pre>";
	//print_r($node);
	echo "</pre>";
	foreach($node['children'] as $child){
	  array_push($worklist,$child);
	}
    
    if($cnt>100){
    	$totalCnt += $cnt;
    	$cnt = 0;
		$list = rtrim($list, ", \n");
		$list = str_replace("NaN", "0", $list);
    	mysql_query($query.$list);
    	$list = "";
        if(mysql_errno()){
	      echo "<h4 style='color:red'>There was an error while submitting your data :".mysql_errno().":".mysql_error()."; Query:".$query."</h4>";
		  log_error(array("mysql_errno"=>mysql_errno(), "mysql_error"=> mysql_error(), "query"=>$query, "list"=>$list));
		  echo "<pre>";
		  print_r($node);
		  echo "</pre>";
	      return;
        }
    }
  }
  $totalCnt += $cnt;
  mysql_query($query.rtrim($list, ", \n"));
  if(mysql_errno()){
	echo "<h4 style='color:red'>There was an error while submitting your data :".mysql_errno().":".mysql_error()."; Query:".$query."</h4>";
	log_error(array("mysql_errno"=>mysql_errno(), "mysql_error"=> mysql_error(), "query"=>$query));
  }else{
	echo "<h4 style='color:green'>Information submitted successfully for $totalCnt DOM nodes.</h4>";
	log_error(array("msg"=>"Information submitted successfully for $totalCnt DOM nodes.", "list"=>$list));
  }
}

function log_error($data){
$fp = fopen('./error.txt', 'a') or die('cant open log file');

$data = '{
  time:"'.date('l jS \of F Y h:i:s A').'",
  ip:"'.$_SERVER['REMOTE_ADDR'].'",
  user_agent:"'.$_SERVER['HTTP_USER_AGENT'].'",
  referer:"'.$_SERVER['HTTP_REFERER'].'",
  data:'.htmlspecialchars_decode(stripslashes(print_r($data,true))).'
},
';

fwrite( $fp, $data);
fclose($fp);
}