<?php
$fp = fopen('./error.txt', 'a') or die('cant open log file');

$data = '{
  ip:"'.$_SERVER['REMOTE_ADDR'].'",
  user_agent:"'.$_SERVER['HTTP_USER_AGENT'].'",
  referer:"'.$_SERVER['HTTP_REFERER'].'",
  data:'.htmlspecialchars_decode(stripslashes(print_r($_REQUEST,true))).'
},
';

fwrite( $fp, $data);
fclose($fp);
?>