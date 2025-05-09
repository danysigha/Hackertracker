let pastQuestions = [];
let futureQuestions = [];
let currentQuestionId = null;
let originalStartDate = null;
let originalEndDate = null;
let cal = initializeCalendar();


function getJwtToken() {
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        const cookie = cookies[i].trim();
        if (cookie.startsWith('jwtToken=')) {
            return cookie.substring('jwtToken='.length, cookie.length);
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

function formatTimeFromDateArray(dateArray) {
    if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 6) {
        return '--:--:--';
    }

    // Extract components from the array
    const [year, month, day, hour, minute, second] = dateArray;

    // Create a date object in UTC, then JavaScript will automatically
    // convert it to local time when displaying
    const date = new Date(Date.UTC(year, month - 1, day, hour, minute, second));

    // Format time as "hh:mm:ss AM/PM" - no manual timezone adjustment needed
    return date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true
    });
}

// Assuming you have a date string from your AJAX response
// function formatTimeFromDateArray(dateArray) {
//     if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 6) {
//         return '--:--:--';
//     }
//
//     // Extract components from the array
//     const [year, month, day, hour, minute, second] = dateArray;
//
//     // Note: JavaScript months are 0-indexed (0-11), but the array uses 1-indexed (1-12)
//     // So we need to subtract 1 from the month
//     const date = new Date(year, month - 1, day, hour, minute, second);
//
//     const localTimezoneOffsetMinutes = new Date().getTimezoneOffset();
//     const localTimestamp = date.getTime() - (localTimezoneOffsetMinutes * 60 * 1000);
//     const localDate = new Date(localTimestamp);
//
//     // Format time as "hh:mm:ss AM/PM"
//     return localDate.toLocaleTimeString('en-US', {
//         hour: '2-digit',
//         minute: '2-digit',
//         second: '2-digit',
//         hour12: true
//     });
// }

function getTimeDifference(startDateArray, endDateArray) {
    // Check if inputs are valid
    if (!startDateArray || !endDateArray) {
        return "00:00:00"; // Default if either date is missing
    }

    let startDate, endDate;

    // Convert date array to JavaScript Date object
    if (Array.isArray(startDateArray) && startDateArray.length >= 6) {
        const [year, month, day, hour, minute, second, nanosecond = 0] = startDateArray;
        // JavaScript months are 0-indexed
        startDate = new Date(year, month - 1, day, hour, minute, second, nanosecond / 1000000);
    } else {
        // Handle ISO string or other format
        startDate = new Date(startDateArray);
    }

    if (Array.isArray(endDateArray) && endDateArray.length >= 6) {
        const [year, month, day, hour, minute, second, nanosecond = 0] = endDateArray;
        // JavaScript months are 0-indexed
        endDate = new Date(year, month - 1, day, hour, minute, second, nanosecond / 1000000);
    } else {
        // Handle ISO string or other format
        endDate = new Date(endDateArray);
    }

    // Calculate difference in milliseconds
    let diffMs = Math.abs(endDate - startDate);

    // Calculate hours, minutes, seconds
    const hours = Math.floor(diffMs / (1000 * 60 * 60));
    diffMs -= hours * (1000 * 60 * 60);

    const minutes = Math.floor(diffMs / (1000 * 60));
    diffMs -= minutes * (1000 * 60);

    const seconds = Math.floor(diffMs / 1000);

    // Format as "hh:mm:ss"
    const formattedHours = hours.toString().padStart(2, '0');
    const formattedMinutes = minutes.toString().padStart(2, '0');
    const formattedSeconds = seconds.toString().padStart(2, '0');

    return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
}


// function timeStringToDate(timeString) {
//     // Parse the time string
//     const [timePart, period] = timeString.split(' ');
//     let [hours, minutes, seconds] = timePart.split(':').map(Number);
//
//     // Convert 12-hour format to 24-hour format
//     if (period.toUpperCase() === 'PM' && hours < 12) {
//         hours += 12;
//     } else if (period.toUpperCase() === 'AM' && hours === 12) {
//         hours = 0;
//     }
//
//     // Create a new Date object with today's date and the parsed time
//     const date = new Date();
//     date.setHours(hours, minutes, seconds, 0);
//
//     return date;
// }

function timeStringToDateWithNanos(timeString) {
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

    // Get the ISO string without the 'Z' at the end
    const isoWithoutZ = date.toISOString().slice(0, -1);

    // Replace the millisecond part with nanoseconds (000000 for your case)
    // First, remove the milliseconds that are in the ISO string
    const isoWithoutMillis = isoWithoutZ.split('.')[0];

    // Then add your nanosecond precision
    return `${isoWithoutMillis}.000000Z`;
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

    // Only include question id if we have one
    if(questionId !== null) {
        ajaxData.questionId = questionId;
        ajaxData.fetchById = !addToPast; // We want to fetch by ID when navigating history
    }

    $.ajax({
        url: "/api/challenges/next",
        method: "GET",
        data: ajaxData,
        // dataType: "json",
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

            if(data.problem.difficultyLevel === "Easy") {
                $("#challengeLevel").css("background-color", "#A8DCAB");
            } else if(data.problem.difficultyLevel === "Medium") {
                $("#challengeLevel").css("background-color", "#FFDBBB");
            } else {
                $("#challengeLevel").css("background-color", "#FF7F7F");
            }

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

                $("#startTimeDisplay").text(formatTimeFromDateArray(data.attempt.startTime));
                originalStartDate = new Date(data.attempt.startTime);

                $("#endTimeDisplay").text(formatTimeFromDateArray(data.attempt.endTime));
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

                $("#timerDisplay").text("00:00:00");

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
        },
        error: function(xhr, status, error) {
            console.error("Error loading question:", error);
            // Update button states even if AJAX fails
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

// For continuous updates as the user drags the slider
$('#difficultySlider').on('input', function() {
    const value = $(this).val();
    $("#challengeDifficultyRating").text("Difficulty Rating (" + value + "/10)");
});

// For when the user finishes moving the slider
$('#difficultySlider').on('change', function() {
    const value = $(this).val();
    $("#challengeDifficultyRating").text("Difficulty Rating (" + value + "/10)");
});


// Mark completed button could work like this
$("#addAttemptBtn").on("click", function() {
    // Do whatever is needed to mark the question as completed

    //Problem problem, User user, byte difficultyRating, Date startTime, Date endTime, String notes

    stopTimer();

    // Set up AJAX parameters
    let ajaxData = {};

    ajaxData.questionId = currentQuestionId;

    ajaxData.difficultyRating = document.getElementById("difficultySlider").value;

    // Make sure we're not sending empty strings
    if(document.getElementById("startTimeDisplay").innerHTML !== "--:--:--") {
        if(!originalStartDate) {
            originalStartDate = timeStringToDateWithNanos(document.getElementById("startTimeDisplay").innerHTML);
        }
        ajaxData.startTime = originalStartDate;
    }else {
        ajaxData.startTime = null;
    }

    if(document.getElementById("endTimeDisplay").innerHTML !== "--:--:--") {
        if(!originalEndDate) {
            originalEndDate = timeStringToDateWithNanos(document.getElementById("endTimeDisplay").innerHTML);
        }
        ajaxData.endTime = originalEndDate;
    }else {
        ajaxData.endTime = null;
    }

    // console.log("startTime", ajaxData.startTime);
    // console.log("endTime", ajaxData.endTime);


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

    console.log("sending the following to /api/challenges/recalculate \n\n");
    console.log(ajaxData.questionId, ajaxData.difficultyRating, ajaxData.startTime, ajaxData.endTime, ajaxData.notes);


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
            setTimeout(function() { loadDataAndDisplayCalendarData(cal); }, 100);
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
        $("#addAttemptBtn").prop("disabled", true);
        $("#checkmark path").attr("stroke", "#D3D3D3");
        // $("#skipIcon path").attr("fill", "#D3D3D3");
        $("#skipQuestionBtn").prop("disabled", true);
    } else {
        $("#nextQuestionBtn").prop("disabled", true);
        $("#addAttemptBtn").prop("disabled", false);
        $("#checkmark path").attr("stroke", "#4CAF50");
        // $("#skipIcon path").attr("fill", "#000000");
        $("#skipQuestionBtn").prop("disabled", false);
    }
}

// Initial setup
$(document).ready(function() {
    // Small delay to ensure cookie is fully processed
    setTimeout(function() {
        // console.log("we outsiiiiiiddddde!");
        const token = getJwtToken();
        if (token) {

            tinymce.init({
                selector: '#myEditor',
                plugins: 'link lists code',
                toolbar: 'undo redo | formatselect | bold italic | alignleft aligncenter alignright | bullist numlist | link code',
                height: 400,
                // skin: 'oxide-dark',
                // content_css: 'dark'
            });

            // console.log("we insiiiiiiddddde!");
            // Token found, load question
            loadQuestion();
            loadDataAndDisplayCalendarData(cal);
            // console.log("supposedly called loadQuestion");

            // Add this line to ensure buttons are updated on initial load
            updateNavigationButtons();

            // ensure skip button is initialized on initial load
            // NOT FUNCITONAL YET BECAUSE IT DOES NOT UPDATE PRIORITY
            // SHOULD USE THE SKIP ENDPOINT
            document.getElementById("skipQuestionBtn").addEventListener("click", function (e) {
                loadQuestion();
            })

            // Add a listener for URL changes
            $(window).on('popstate', function() {
                updateNavigationButtons();
            });

        } else {
            // No token, redirect to login
            window.location.href = '/login';
        }
    }, 100); // Even a short 100ms delay can help
});