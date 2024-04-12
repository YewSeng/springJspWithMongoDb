function validateForm() {
    var name = document.getElementById("name").value;
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var status = document.getElementById("status").value;

    // Regular expressions for validation
    var nameRegex = /^[a-zA-Z ]{4,}$/;
    var usernameRegex = /^D[a-zA-Z0-9]{7,}$/; // Username must start with 'D' followed by at least 7 characters and numbers
    var passwordRegex = /^(?=.*[A-Z])(?=.*[@])(?=.*[0-9])[a-zA-Z0-9@]{8,}$/; // Password: At least 1 uppercase, 1 "@", 1 number, At least length 8
	
    // Variables to store error messages
    var errorMessage = "";
    var nameError = "";
    var usernameError = "";
    var passwordError = "";
    var statusError = "";

    if (name.trim() === "") {
        nameError = "Name cannot be empty";
        errorMessage += nameError + "\n";
    } else if (!nameRegex.test(name)) {
        nameError = "Name must be at least 4 characters long and contain only letters";
        errorMessage += nameError + "\n";
    }

    if (!usernameRegex.test(username)) {
        usernameError = "Username must start with an uppercase 'D' followed by at least 7 characters";
        errorMessage += usernameError + "\n";
    }

    if (!passwordRegex.test(password)) {
        passwordError = "Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number";
        errorMessage += passwordError + "\n";
    }

    if (status === "") {
        statusError = "Please select a status";
        errorMessage += statusError + "\n";
    }
    
    // Display error messages in divs
    document.getElementById("name-error").innerText = nameError;
    document.getElementById("username-error").innerText = usernameError;
    document.getElementById("password-error").innerText = passwordError;
    document.getElementById("status-error").innerText = statusError;

    // Display error message in main error div if there are errors, or hide it if no errors
    var errorDiv = document.getElementById("error-message");
    if (errorMessage.trim() !== "") {
        errorDiv.innerText = errorMessage;
        errorDiv.style.display = "block"; // Show the error message div
    } else {
        errorDiv.style.display = "none"; // Hide the error message div
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
