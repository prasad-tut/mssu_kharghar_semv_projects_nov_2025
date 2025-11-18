export const handleError = (error) => {
  if (error.response) {
    console.error("Server Error:", error.response.data);
    alert(error.response.data.message || "Server error occurred");
  } else if (error.request) {
    console.error("Network Error:", error.request);
    alert("Network error. Please try again.");
  } else {
    console.error("Error:", error.message);
    alert("Something went wrong.");
  }
};
