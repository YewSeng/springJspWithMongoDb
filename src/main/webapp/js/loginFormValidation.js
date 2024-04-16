function validateForm() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;

    // Regular expressions for validation
    var usernameRegex = /^[ADU][a-zA-Z0-9]{7,}$/; // Username must start with 'A', 'D' or 'U' followed by at least 7 characters and numbers
    var passwordRegex = /^(?=.*[A-Z])(?=.*[@])(?=.*[0-9])[a-zA-Z0-9@]{8,}$/; // Password: At least 1 uppercase, 1 "@", 1 number, At least length 8
	
    // Variables to store error messages
    var errorMessage = "";
    var usernameError = "";
    var passwordError = "";

    if (!usernameRegex.test(username)) {
        usernameError = "Username must start with an uppercase 'A', 'D' or 'U' followed by at least 7 characters";
        errorMessage += usernameError + "\n";
    }

    if (!passwordRegex.test(password)) {
        passwordError = "Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number";
        errorMessage += passwordError + "\n";
    }
    
    // Display error messages in divs
    document.getElementById("username-error").innerText = usernameError;
    document.getElementById("password-error").innerText = passwordError;

    // Display error message in main error div if there are errors, or hide it if no errors
    var errorDiv = document.getElementById("error-message");
    if (errorMessage.trim() !== "") {
        errorDiv.innerText = errorMessage;
        errorDiv.style.display = "block"; // Show the error message div
        
        // Disable the login button if there are errors
        document.getElementById("submit-button").disabled = true;
    } else {
        errorDiv.style.display = "none"; // Hide the error message div
        
        // Enable the login button if there are no errors
        document.getElementById("submit-button").disabled = false;
    }
    var loginDiv = document.getElementById("login-error");
    if (loginDiv && loginDiv.innerText.trim() !== "") {
        loginDiv.style.display = "block"; 
    }
    
    return errorMessage === ""; // Return true if no error messages, otherwise return false
}

function togglePasswordVisibility() {
    var passwordField = document.getElementById("password");
    var showPasswordCheckbox = document.getElementById("showPassword");

    if (showPasswordCheckbox.checked) {
        passwordField.type = "text";
    } else {
        passwordField.type = "password";
    }
}
