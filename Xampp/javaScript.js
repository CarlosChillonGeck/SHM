//Javascript to dynamically plot the Diagram in the website with the help of the AJAX - Technology.	

function input() {
    
    return document.getElementById("field1").value;
   
}

//var width = 2;



function getCount() {

	//Function to get a parameter to compare with so that the script knows when to redraw the diagram. 	

		

	var result 

		

		var xmlhttp = new XMLHttpRequest();

	    xmlhttp.onreadystatechange = function() {

		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

		//Creating xmlhttp-Request object for the AJAX communication with the database.

			

			result = xmlhttp.responseText

			.slice(1, -2)

			//Delete from the first symbol counted the first and the second symbol.

			

			.split(",")

			//Split the incoming string always when the symbol "," appears.

			

			.map(str => str.slice(2, -2));

			//Delete from the last symbol counted the first and the second symbol.

			//All in all Code to transform the incoming string from the php-Script into a useable form.

					

			}		

	    }

	   

	    xmlhttp.open("GET", "getCount.php", false);

		xmlhttp.send();

		//connection to the php-Script thats used to communicate with the database.

		

		return result;

		}

		

		

		/**

    * returns an array with moving average of the input array

    * @param array - the input array

    * @param count - the number of elements to include in the moving average calculation

    * @param qualifier - an optional function that will be called on each 

    *  value to determine whether it should be used

    */

    function movingAvg(array, count, qualifier){



        // calculate average for subarray

        var avg = function(array, qualifier){



            var sum = 0, count = 0, val;

            for (var i in array){

                val = array[i];

                if (!qualifier || qualifier(val)){

                    sum += val;

                    count++;

                }

            }



            return sum / count;

        };



        var result = [], val;



        // pad beginning of result with null values

        for (var i=0; i < count-1; i++)

            result.push(null);



        // calculate average for each subarray and add to result

        for (var i=0, len=array.length - count; i <= len; i++){

        	if(i<count||(i+count>array.length)){
        		
        		val = avg(array.slice(i, i + count + 1), qualifier);
        		
        	}
        	else{
        		val = avg(array.slice(i - count, i + count + 1), qualifier);
        	}

            

            if (isNaN(val))

                result.push(null);

            else

                result.push(val);

        }



        return result;

    }

	

		

function getTime() {

	//Function to get the time for the diagram.	

	var finaltime;	

		var xmlhttp = new XMLHttpRequest();			

		xmlhttp.onreadystatechange = function() {				

			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

				//Creating xmlhttp-Request object for the AJAX communication with the database.

				
				var result = xmlhttp.responseText;

					
					// Formating data to ready read
					var time = result

						.slice(1, -2)

						.split(",")

						.map(str => str.slice(2, -2))

					//Again Code to transform the incoming string from the php-Script into a useable form.
					finaltime = time
						

				    // finaltime = time.map(function (x) 

				    // 	     {

				    // 	//Code to transform the incoming time data into a real time.

				    	

				    //      	var date = new Date(parseInt(x));

				    //      	var mm = (date.getUTCMonth()+1);

				    //      	if(mm < 10)

				    //          	mm = "0"+ mm;

				    //      	var min = date.getUTCMinutes();

				    //      	if(min < 10)

				    //          	min = "0"+ min;

				    //      	var ss = date.getUTCSeconds();

				    //      	if(ss < 10)

				    //          	ss = "0"+ ss;

				    //      	var hh = date.getUTCHours();

				    //      	if(hh < 10)

				    //          	hh = "0"+ hh;

				    //      	var dd = date.getUTCDate();

				    //      	if(dd < 10)

				    //          	dd = "0"+ dd;

				    //      	var ms = date.getUTCMilliseconds();

				    //      	if(ms < 10)

				    //          	ms = "00"+ ms;

				    //      	else if(ms < 100)

				    //          	ms = "0"+ ms;

				         	

				             	

				    //      	return date.getFullYear() + "-" + mm + "-" + dd+" "+hh+":"+min+":"+ss+"."+ms;

				    //      });				

				}

			};

			xmlhttp.open("GET", "getTime.php", false);

				xmlhttp.send();
	
				//connection to the php-Script thats used to communicate with the database.	


			
			return finaltime;

		}

function getTemperature1() {

	//Function to get the temperature for the diagram.

			

	var finaltemperature;

					

			var xmlhttp = new XMLHttpRequest();

			xmlhttp.onreadystatechange = function() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

					//Creating xmlhttp-Request object for the AJAX communication with the database.

					

					var result = xmlhttp.responseText;

					

					var temperature = result

					

						.slice(1, -2)

						.split(",")

						.map(str => str.slice(2, -2))

					//Again Code to transform the incoming string from the php-Script into a useable form.



					finaltemperature = temperature.map(function (x) {return parseFloat(x);});			         

					//Transform the incoming string data with the temperature information into numbers.

					

				}

			};

			

			xmlhttp.open("GET", "getTemperature1.php", false);

			xmlhttp.send();

			//connection to the php-Script thats used to communicate with the database.

		

			

			return finaltemperature;

		

		}		

		function getAcc_x() {

			//Function to get the temperature for the diagram.
		
					
		
			var finalAcc_x;
		
							
		
					var xmlhttp = new XMLHttpRequest();
		
					xmlhttp.onreadystatechange = function() {
		
						if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		
							//Creating xmlhttp-Request object for the AJAX communication with the database.
		
							
		
							var result = xmlhttp.responseText;
		
							
		
							var Acc_x = result
		
							
		
								.slice(1, -2)
		
								.split(",")
		
								.map(str => str.slice(2, -2))
		
							//Again Code to transform the incoming string from the php-Script into a useable form.
		
		
		
							finalAcc_x = Acc_x.map(function (x) {return parseFloat(x);});			         
		
							//Transform the incoming string data with the temperature information into numbers.
		

		
						}
		
					};
		
					
		
					xmlhttp.open("GET", "getAcc_x.php", false);
		
					xmlhttp.send();
		
					//connection to the php-Script thats used to communicate with the database.
		
				
		
					
		
					return finalAcc_x;
		
				}
				

function getTemperature1() {

	//Function to get the temperature for the diagram.

			

	var finaltemperature;

					

			var xmlhttp = new XMLHttpRequest();

			xmlhttp.onreadystatechange = function() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

					//Creating xmlhttp-Request object for the AJAX communication with the database.

					

					var result = xmlhttp.responseText;

					

					var temperature = result

					

						.slice(1, -2)

						.split(",")

						.map(str => str.slice(2, -2))

					//Again Code to transform the incoming string from the php-Script into a useable form.



					finaltemperature = temperature.map(function (x) {return parseFloat(x);});			         

					//Transform the incoming string data with the temperature information into numbers.

					

				}

			};

			

			xmlhttp.open("GET", "getTemperature1.php", false);

			xmlhttp.send();

			//connection to the php-Script thats used to communicate with the database.

		

			

			return finaltemperature;

		

		}



function getTemperature2() {

	//Function to get the temperature for the diagram.

			

	var finaltemperature;

					

			var xmlhttp = new XMLHttpRequest();

			xmlhttp.onreadystatechange = function() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

					//Creating xmlhttp-Request object for the AJAX communication with the database.

					

					var result = xmlhttp.responseText;

					

					var temperature = result

					

						.slice(1, -2)

						.split(",")

						.map(str => str.slice(2, -2))

					//Again Code to transform the incoming string from the php-Script into a useable form.



					finaltemperature = temperature.map(function (x) {return parseFloat(x);});			         

					//Transform the incoming string data with the temperature information into numbers.

					

				}

			};

			

			xmlhttp.open("GET", "getTemperature2.php", false);

			xmlhttp.send();

			//connection to the php-Script thats used to communicate with the database.

		

			

			return finaltemperature;

		

		}

		

		

function doDiagram(time, acc_x) {

		//Function for the diagram drwaing.

		

		

		 var trace1 = {

     x: time, 

     y: acc_x, 

     type: 'markers',

	 name: 'x',

     };

	 

	//  var trace2 = {

    //  x: time, 

    //  y: temperature2, 

    //  type: 'scatter',

	//  name: 'CH1',

    //  };



     var layout1 = {

    	     title: 'Acceleration x in Sensor 1',

    	     xaxis: {

        	     title: 'time'

    	     },

    	     yaxis: {

        	     title: 'Acceleration'

    	     }

     };

	 

	//  var layout2 = {

    // 	     title: 'temperature time series sensor CH0',

    // 	     xaxis: {

    //     	     title: 'time'

    // 	     },

    // 	     yaxis: {

    //     	     title: 'temperature [°C]'

    // 	     }

    //  };

	 

	//  var layout3 = {

    // 	     title: 'temperature time series sensor CH1',

    // 	     xaxis: {

    //     	     title: 'time'

    // 	     },

    // 	     yaxis: {

    //     	     title: 'temperature [°C]'

    // 	     }

    //  };

     



     var data1 = [trace1];

	//  var data2 = [trace2];

	//  var data3 = [trace1,trace2];

     

	

			

				    // if(true)

					 //{

						Plotly.newPlot('myDiv1', data1, layout1);

						// Plotly.newPlot('myDiv2', data1, layout2);

						// Plotly.newPlot('myDiv3', data2, layout3);

					// }						 

				    // else 

					 //{

						//Plotly.restyle('myDiv1', temperature, [1]);

						//Plotly.restyle('myDiv2', temperature, [1]);

						//Plotly.restyle('myDiv3', temperature, [1]);						

					// }

				     //Plotly.newPlot('myDiv', data, layout);



		}		

		

		

		//var a = 0;

		//Initialize a variable to compare with for drawing the diagram.	

		

function doTheThing(){

		//Function to combine the diagram-function and the comparing function.

	
	var width = input();
	    //if(parseInt(getCount())>a||parseInt(getCount())==0){

	
			var acc_x = getAcc_x();

			acc_x = movingAvg(acc_x, width, function(val){ return true; });
	
			var time = getTime();

		// var temperature1 = getTemperature1();

		// temperature1 = movingAvg(temperature1, width, function(val){ return true; });

		// var temperature2 = getTemperature2();

		// temperature2 = movingAvg(temperature2, width, function(val){ return true; });

		// var time = getTime();

		doDiagram(time, acc_x);



		}

	    //a = parseInt(getCount());

	


	

		setInterval(doTheThing, 3000);
		document.write('test');

		//Interval setting to initialize the doTheThing-function timed withe the proposed window of incoming data from the thermometer.