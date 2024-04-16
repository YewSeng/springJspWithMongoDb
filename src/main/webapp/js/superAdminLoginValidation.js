function validateForm() {
    var superAdminKey = document.getElementById("superAdminKey").value.trim();
    var superAdminKeyRegex = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/;

    var errorElement = document.getElementById("superAdminKey-error");
    if (superAdminKey === "") {
        errorElement.innerHTML = "<b>Super Admin Key cannot be empty!</b>";
        return false;
    } else if (!superAdminKeyRegex.test(superAdminKey)) {
        errorElement.innerHTML = "<b>Invalid Super Admin Key Pattern!</b>";
        return false;
    } else {
        errorElement.innerHTML = "";
        return true;
    }
}
