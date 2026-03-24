// Timer variables
let startTime = null;
let endTime = null;
let timerInterval = null;
let isTimerRunning = false;

// DOM elements
const timerDisplay = document.getElementById('timerDisplay');
const startTimeDisplay = document.getElementById('startTimeDisplay');
const endTimeDisplay = document.getElementById('endTimeDisplay');
const startTimerBtn = document.getElementById('startTimerBtn');
const stopTimerBtn = document.getElementById('stopTimerBtn');

document.addEventListener('DOMContentLoaded', function() {
    // Event listeners
    startTimerBtn.addEventListener('click', startTimer);
    stopTimerBtn.addEventListener('click', stopTimer);

    // Initialize display
    updateTimerDisplay();
});

// Format time for display (HH:MM:SS)
function formatTime(date) {
    if (!date) return '--:--:--';
    return date.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}


// Calculate and format elapsed time
function calculateElapsedTime() {
    if (!startTime) {
        //console.log("startTime is not set, returning default time");
        return '00:00:00';
    }

    // Get end time (either the stored end time or current time)
    const end = endTime || new Date();

    // Calculate difference in milliseconds and convert to seconds
    const diffMs = end - startTime;
    const diffSec = Math.floor(diffMs / 1000);

    // Calculate hours, minutes, seconds
    const hr = Math.floor(diffSec / 3600);
    const min = Math.floor((diffSec % 3600) / 60);
    const sec = Math.floor(diffSec % 60);

    // Format with leading zeros
    const formattedTime =
        hr.toString().padStart(2, '0') + ':' +
        min.toString().padStart(2, '0') + ':' +
        sec.toString().padStart(2, '0');

    // console.log("Calculated time:", formattedTime);

    return formattedTime;
}

// Update timer display
function updateTimerDisplay() {
    timerDisplay.textContent = calculateElapsedTime();
}

// Start timer functionality
function startTimer() {
    // Clear any existing interval to avoid multiple intervals
    if (timerInterval) {
        clearInterval(timerInterval);
    }

    startTime = new Date();
    endTime = null;
    isTimerRunning = true;

    startTimeDisplay.textContent = formatTime(startTime);
    endTimeDisplay.textContent = '--:--:--';

    // Update timer display then start interval
    updateTimerDisplay();
    timerInterval = setInterval(updateTimerDisplay, 1000);

    // Toggle buttons
    startTimerBtn.style.display = 'none';
    stopTimerBtn.style.display = 'block';
}

// Stop timer functionality
function stopTimer() {
    if (!isTimerRunning) return;

    endTime = new Date();
    isTimerRunning = false;

    endTimeDisplay.textContent = formatTime(endTime);

    // Clear interval
    if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
    }

    // Update display one final time
    updateTimerDisplay();

    // Toggle buttons
    startTimerBtn.style.display = 'block';
    stopTimerBtn.style.display = 'none';
}