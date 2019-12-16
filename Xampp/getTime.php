<?php

// Php-Script to get the current data-data from the database.

// connection to database



$link=mysqli_connect("localhost", "root", "","accelerometer") or die (mysql_error ());





// SQL-Query

$strSQLtime = "SELECT Time FROM data";



    

// excute Query

$rstime = mysqli_query($link,$strSQLtime);



// Create Arrays from the column data

$resulttime = array();

$resulttime = mysqli_fetch_all($rstime);





// transform data into a transportable type

$a = json_encode($resulttime);







// database connection closed

mysqli_close($link);



echo $a;



?> 