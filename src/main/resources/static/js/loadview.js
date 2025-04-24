// Using DOMPurify (you'd need to include this library)
// import DOMPurify from 'dompurify';

let pastQuestions = [];
let futureQuestions = [];
let currentQuestionId = null;
let originalStartDate = null;
let originalEndDate = null;


// If using cookies
function getJwtToken() {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'jwtToken') {
            return value;
        }
    }
    return null;
}

// Add to all AJAX requests
$.ajaxSetup({
    beforeSend: function(xhr) {
        const token = getJwtToken();
        if (token) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        }
    }
});

function getTimeDifference(startDateString, endDateString) {
    // Parse the ISO strings to Date objects
    const startDate = new Date(startDateString);
    const endDate = new Date(endDateString);

    // Calculate difference in milliseconds
    let diffMs = Math.abs(endDate - startDate);

    // Calculate hours, minutes, seconds
    const hours = Math.floor(diffMs / (1000 * 60 * 60));
    diffMs -= hours * (1000 * 60 * 60);

    const minutes = Math.floor(diffMs / (1000 * 60));
    diffMs -= minutes * (1000 * 60);

    const seconds = Math.floor(diffMs / 1000);

    // Format as "--:--:--"
    const formattedHours = hours.toString().padStart(2, '0');
    const formattedMinutes = minutes.toString().padStart(2, '0');
    const formattedSeconds = seconds.toString().padStart(2, '0');

    return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
}

// Assuming you have a date string from your AJAX response
function formatTimeFromISOString(isoString) {
    const date = new Date(isoString);

    // Format time as "hh:mm:ss AM/PM"
    return date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true
    });
}

function timeStringToDate(timeString) {
    // Parse the time string
    const [timePart, period] = timeString.split(' ');
    let [hours, minutes, seconds] = timePart.split(':').map(Number);

    // Convert 12-hour format to 24-hour format
    if (period.toUpperCase() === 'PM' && hours < 12) {
        hours += 12;
    } else if (period.toUpperCase() === 'AM' && hours === 12) {
        hours = 0;
    }

    // Create a new Date object with today's date and the parsed time
    const date = new Date();
    date.setHours(hours, minutes, seconds, 0);

    return date;
}

function loadQuestion(questionId = null, addToPast = true) {
    if(addToPast && currentQuestionId !== null) {
        pastQuestions.push(currentQuestionId);
    }

    if(addToPast) {
        futureQuestions = [];
    }

    currentQuestionId = questionId;

    // Set up AJAX parameters
    let ajaxData = {};

    // Only include currentId if we have one
    if(questionId !== null) {
        ajaxData.questionId = questionId;
        ajaxData.fetchById = !addToPast; // We want to fetch by ID when navigating history
    }

    $.ajax({
        url: "/api/challenges/next",
        method: "GET",
        data: ajaxData,
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {

            if (!data || !data.problem) {
                console.error("Received invalid data from server");
                // Handle the error or show a message to the user
                return;
            }

            console.log("what am I getting from the server then?!");
            console.log(data);

            currentQuestionId = data.problem.problemId;

            $("#challengeTitle").text(data.problem.questionTitle);

            let topicHtmlList = "";

            data.problem.problemTopics.forEach(function(topic) {
                topicHtmlList += "<span class='topic'>" + topic + "</span>";
            });

            $("#challengeTopic").html(topicHtmlList);

            $("#challengeLevel").text(data.problem.difficultyLevel);
            $("#challengeUrl").attr("href", data.problem.pageUrl);
            let tagHtmlList = "";

            data.problem.problemTags.forEach(function(tag) {
                tagHtmlList += "<span class='tag'>" + tag + "</span>";
            });

            $("#challengeTags").html(tagHtmlList);

            if(data.attempt) {
                $("#challengeDifficultyRating").text("Difficulty Rating (" + data.attempt.difficultyRating + "/10)");

                $("#difficultySlider").attr("value", data.attempt.difficultyRating);
                $("#difficultySlider").val(data.attempt.difficultyRating).trigger('input');

                $("#timerDisplay").text(getTimeDifference(data.attempt.startTime, data.attempt.endTime));

                $("#startTimeDisplay").text(formatTimeFromISOString(data.attempt.startTime));
                originalStartDate = new Date(data.attempt.startTime);

                $("#endTimeDisplay").text(formatTimeFromISOString(data.attempt.endTime));
                originalEndDate = new Date(data.attempt.endTime);

                //$("#myEditor").text(data.attempt.notes);

                // When setting content from server to editor
                if (tinymce.get('myEditor')) {
                    // TinyMCE has its own sanitization when setting content
                    tinymce.get('myEditor').setContent(data.attempt.notes);
                } else {
                    // Fallback with sanitization
                    const sanitizedHtml = DOMPurify.sanitize(data.attempt.notes);
                    document.getElementById("myEditor").innerHTML = sanitizedHtml;
                }
            } else {
                $("#challengeDifficultyRating").text("Difficulty Rating (3/10)");

                $("#difficultySlider").attr("value", 3);
                $("#difficultySlider").val(3).trigger('input');

                $("#timerDisplay").text("--:--:--");

                $("#startTimeDisplay").text("--:--:--");
                originalStartDate = null;

                $("#endTimeDisplay").text("--:--:--");
                originalEndDate = null;

                // When setting content from server to editor
                if (tinymce.get('myEditor')) {
                    // TinyMCE has its own sanitization when setting content
                    tinymce.get('myEditor').setContent("");
                } else {
                    // Fallback
                    document.getElementById("myEditor").innerHTML = "";
                }
            }

            // Update button states
            updateNavigationButtons();
        }
    });
}

// Back button click handler
$("#previousQuestionBtn").on("click", function() {
    if (pastQuestions.length > 0) {
        // Move current question to future stack
        futureQuestions.push(currentQuestionId);

        // Get the previous question
        let previousId = pastQuestions.pop();

        // Load it without adding to history
        loadQuestion(previousId, false);
    }
});

// Next button click handler
$("#nextQuestionBtn").on("click", function() {
    if (futureQuestions.length > 0) {
        // Move current question to past stack
        pastQuestions.push(currentQuestionId);

        // Get the next question
        let nextId = futureQuestions.pop();

        // Load it without adding to history
        loadQuestion(nextId, false);
    }
});

// Mark completed button could work like this
$("#addAttemptBtn").on("click", function() {
    // Do whatever is needed to mark the question as completed


    //Problem problem, User user, byte difficultyRating, Date startTime, Date endTime, String notes

    // Set up AJAX parameters
    let ajaxData = {};

    ajaxData.questionId = currentQuestionId;

    ajaxData.difficultyRating = document.getElementById("difficultySlider").value;

    // Make sure we're not sending empty strings
    if(document.getElementById("startTimeDisplay").innerHTML !== "--:--:--") {
        if(!originalStartDate) {
            originalStartDate = timeStringToDate(document.getElementById("startTimeDisplay").innerHTML);
        }
        ajaxData.startTime = originalStartDate.toISOString();
    }else {
        ajaxData.startTime = null;
    }

    if(document.getElementById("endTimeDisplay").innerHTML !== "--:--:--") {
        if(!originalEndDate) {
            originalEndDate = timeStringToDate(document.getElementById("endTimeDisplay").innerHTML);
        }
        ajaxData.endTime = originalEndDate.toISOString();
    }else {
        ajaxData.endTime = null;
    }


    console.log("startTime", ajaxData.startTime);
    console.log("endTime", ajaxData.endTime);

    // if(document.getElementById("startTimeDisplay").innerHTML !== "--:--:--") {
    //     ajaxData.startTime = originalStartDate ? originalStartDate.toISOString() : null;
    // } else {
    //     ajaxData.startTime = null;
    // }
    //
    // if(document.getElementById("endTimeDisplay").innerHTML !== "--:--:--") {
    //     ajaxData.endTime = originalEndDate ? originalEndDate.toISOString() : null;
    // } else {
    //     ajaxData.endTime = null;
    // }

    // Safely get TinyMCE content

    // When reading content from editor to send to server
    try {
        if (typeof window.tinymce !== 'undefined' && window.tinymce.get('myEditor')) {
            // TinyMCE's getContent is already sanitized
            ajaxData.notes = window.tinymce.get('myEditor').getContent();
        } else {
            // Need to sanitize if reading from DOM directly
            const rawHtml = document.getElementById("myEditor").innerHTML || "";
            ajaxData.notes = DOMPurify.sanitize(rawHtml);
        }
    } catch (e) {
        console.error("Error accessing TinyMCE:", e);
        // Fallback with sanitization
        const rawHtml = document.getElementById("myEditor").innerHTML || "";
        ajaxData.notes = DOMPurify.sanitize(rawHtml);
    }

    // try {
    //     // Check if TinyMCE is available globally
    //     if (typeof window.tinymce !== 'undefined' && window.tinymce.get('myEditor')) {
    //         ajaxData.notes = window.tinymce.get('myEditor').getContent();
    //     } else {
    //         // Fallback to innerHTML as you suggested
    //         ajaxData.notes = document.getElementById("myEditor").innerHTML || "";
    //         console.log("TinyMCE not initialized, using innerHTML instead");
    //     }
    // } catch (e) {
    //     console.error("Error accessing TinyMCE:", e);
    //     // Fallback
    //     ajaxData.notes = document.getElementById("myEditor").innerHTML || "";
    // }


    // Then request the next question in sequence
    $.ajax({
        url: "/api/challenges/recalculate",
        method: "POST",
        data: ajaxData,
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {
            // Load the new question, adding current to history
            console.log("loading new question!!!! calling loadQuestion with problem id and addToPast = true");
            console.log(data);
            loadQuestion(data.problemId);
        },
        error: function(xhr, status, error) {
            console.error("Error response:", {
                status: xhr.status,
                statusText: xhr.statusText,
                responseText: xhr.responseText,
                error: error
            });
        },
        complete: function(xhr, status) {
            console.log("Request completed with status:", status);
        }
    });
});

// Update navigation button states
function updateNavigationButtons() {
    console.log("Updating buttons. Past questions:", pastQuestions.length, "Future questions:", futureQuestions.length);

    // Enable/disable back button
    if (pastQuestions.length > 0) {
        $("#previousQuestionBtn").prop("disabled", false);
    } else {
        $("#previousQuestionBtn").prop("disabled", true);
    }

    // Enable/disable next button
    if (futureQuestions.length > 0) {
        $("#nextQuestionBtn").prop("disabled", false);
    } else {
        $("#nextQuestionBtn").prop("disabled", true);
    }
}

// Initial setup
$(document).ready(function() {
    // Load the initial question
    loadQuestion(); // Don't add to history since it's the first one
});