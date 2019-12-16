<?php

// Php-Script to get the current temperature-data from the database.

// connection to database



$link = mysqli_connect("localhost", "root", "", "accelerometer") or die(mysql_error());



// SQL-Query

$strSQLAcc_x = "SELECT Acc_x FROM data";



// excute Query

$rsAcc_x = mysqli_query($link, $strSQLAcc_x);



// Create Arrays from the column data



$resultAcc_x = array();

$resultAcc_x = mysqli_fetch_all($rsAcc_x);



// transform data into a transportable type

$a = json_encode($resultAcc_x);





// database connection closed

mysqli_close($link);



echo $a;

?>