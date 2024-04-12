$(document).ready(function() {
    const searchType = $("#searchType");
    const statusSearch = $("#status-search");
    const textSearch = $("#text-search");

    // Initially disable the status dropdown
    $("#status").prop("disabled", true);

    // Event listener for the searchType dropdown change
    searchType.change(function() {
        const selectedValue = $(this).val();
        if (selectedValue === "status") {
            statusSearch.show();
            textSearch.hide();
            $("#status").prop("disabled", false); // Enable the status dropdown
            $("#searchTerm").prop("disabled", true); // Disable the text input
        } else {
            statusSearch.hide();
            textSearch.show();
            $("#status").prop("disabled", true); // Disable the status dropdown
            $("#searchTerm").prop("disabled", false); // Enable the text input
        }
    });

    // Form submit event handler
    $("#filterForm").submit(function(event) {
        // Prevent the default form submission behavior
        event.preventDefault();
        
        // Get the selected search type and its value
        const searchTypeValue = searchType.val();
        let searchTermValue = "";
        
        if (searchTypeValue === "status") {
            searchTermValue = $("#status").val(); // Get the value from the status dropdown
        } else {
            searchTermValue = $("#searchTerm").val(); // Get the value from the text input
        }
        
        // Remove any existing searchTerm parameters
        const formAction = $(this).attr("action").split("?")[0]; // Get the base URL without query parameters
        
        // Modify the form action URL to include the search type and search term
        const separator = formAction.includes("?") ? "&" : "?";
        const finalFormAction = `${formAction}${separator}searchType=${searchTypeValue}`;
        $(this).attr("action", finalFormAction);
        
        // Submit the form
        $(this).unbind('submit').submit();
    });
});