function validate_pass(password){
  var minimum_length = 7;
  var errors = [];
  if(password.length<minimum_length)
    errors.push("Must be at least 7 characters long");//checks the length
  if(password.match(/[^a-zA-Z0-9]/))
    errors.push("Only alphanumeric chars allowed");//checks the charecters used
  if(!password.match(/[a-z]/))
    errors.push("Lower case letter required");//checks it includes lower case characters
  if(!password.match(/[A-Z]/))
    errors.push("Upper case letter required");//checks upper case characters are used
  if(!password.match(/[0-9]/))
    errors.push("Number required");//checks numbers are used
  return errors;
}

