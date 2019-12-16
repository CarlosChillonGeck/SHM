<?php

//Php-Script to get the current number of datasets in the database.

//connection to database



$link = mysqli_connect("localhost", "root", "", "accelerometer") or die(mysql_error());



//SQL-Query

$sumTime = "SELECT COUNT(Time) FROM data";



//excute Query

$rsSum = mysqli_query($link, $sumTime);

$resultSum = array();

$resultSum = mysqli_fetch_all($rsSum);



$a = json_encode($resultSum);



// database connection closed

mysqli_close($link);



echo $a;



?> 