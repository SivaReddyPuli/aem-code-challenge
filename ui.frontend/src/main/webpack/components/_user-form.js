$(function() {
    console.log( "ready!" );
    
    $($('#userDetails')[0]).attr('action', '/bin/saveUserDetails');

    var htmlstr = "<label for=\"Country\">Country:</label>";
        htmlstr += "<select name=\"country\" id=\"country\">";
        // htmlstr += "<option value=\"Select Country\"></option>";
        htmlstr += "</select>";
    

    $('.country__cmp').html(htmlstr);

    $('#age').keypress(function (e) {    
    
        var charCode = (e.which) ? e.which : event.keyCode    

        if (String.fromCharCode(charCode).match(/[^0-9]/g))    

            return false;                        

    }); 

    if($('#country').length) {

        $.getJSON( "/content/dam/anf-code-challenge/exercise-1/countries.json", function( data ) {
            var items = [];
            $.each( data, function( key, val ) {
              items.push( "<option value='" + val + "'>" + key + "</option>" );
            });
           
            $('#country').html(items);
            // $( "<ul/>", {
            //   "class": "my-new-list",
            //   html: items.join( "" )
            // }).appendTo( "body" );
          });

    }   


    $("#userFormSubmit").click(
        function(event) {

            var formData = {
                firstName: $("#firstName").val(),
                lastName: $("#lastName").val(),
                age: $("#age").val(),
                country: $("#country").val(),
              };
          
            //   $.ajax({
            //     type: "POST",
            //     url: $($('#userDetails')[0]).attr('action'),
            //     data: formData,
            //     dataType: "json",
            //     encode: true,
            //     "headers": {
            //         "accept": "application/json",
            //         "Access-Control-Allow-Origin":"*"
            //     }
            //   }).done(function (data) {
            //     console.log(data);
            //   });

              $.ajax({     
                type : "POST",     
                    url : $($('#userDetails')[0]).attr('action'),     
                    data: formData,
                    dataType: "json",
                    encode: true,   
                    success : function(data, textStatus, jqXHR) {         
                    //write your logic that you need to perform on sucess  
                    if(data.data.status == 'ERROR') {
                        if(data.data.field == 'AGE') {
                          if(!$('#ageError').length) {
                            $("#age").parent().append("<div id=\"ageError\" style=\"color: red\">Only Users between 18 and 50 are allowed to submit the form</div>");
                          }
                        }
                    } else {
                      alert("Success"); 
                      location.reload();  
                    }
                                          
                    },     
                    error : function(data, textStatus, jqXHR) {     
                        alert("error");    
                    //write your logic that you need to perform on error     
                    } 
                    });
          
              event.preventDefault();

           event.stopPropagation();
        }
     );

});