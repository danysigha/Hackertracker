function initializeCalendar() {
    return new CalHeatmap();
}

// Initialize calendar with appropriate theme based on current mode
function initializeCalendarWithTheme() {
    const isDarkMode = window.matchMedia("(prefers-color-scheme: dark)").matches;
    const theme = isDarkMode ? 'dark' : 'light';
    const colorScheme = isDarkMode ? 'YlOrBr' : 'Greens'; // Using YlGn for dark mode

    // Load data and display calendar
    loadDataAndDisplayCalendarData(cal, theme, colorScheme);
}

function displayCalendar(cal, processedData, startDate, theme = 'light', colorScheme = 'Greens') {

// Get the button element by ID
    const forwardButton = document.getElementById('navigation-forward');
    const backwardButton = document.getElementById('navigation-backward');

// Add a click event listener to the button
    backwardButton.addEventListener('click', function() {
        cal.previous(7);
    });

    forwardButton.addEventListener('click', function() {
        cal.next(7);
    });

    cal.on('minDateReached', () => {
        backwardButton.style.opacity = '0.5';
        backwardButton.style.cursor = 'not-allowed';
        backwardButton.disabled = true;
    });

    cal.on('maxDateReached', () => {
        forwardButton.style.opacity = '0.5';
        forwardButton.style.cursor = 'not-allowed';
        forwardButton.disabled = true;
    });

    cal.on('minDateNotReached', () => {
        backwardButton.style.opacity = '1';
        backwardButton.style.cursor = 'pointer';
        backwardButton.disabled = false;
    });

    cal.on('maxDateNotReached', () => {
        forwardButton.style.opacity = '1';
        forwardButton.style.cursor = 'pointer';
        forwardButton.disabled = false;
    });


// Using the paint method with explicit timezone settings
    cal.paint({
            itemSelector: "#cal-heatmap", // Make sure this element exists
            theme: theme,
            data: {
                source: processedData,
                x: 'date',
                y: 'value'
            },
            date: {
                start: getPastSunday(new Date()),
                min: getPastSunday(startDate),
                max: new Date(getNextSaturday(new Date())),
                // timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
                // Explicitly set timezone to use the browser's local timezone
                timezone: 'UTC'
            },
            range: 7,
            domain: {
                type: "day",
                gutter: 16,
                label: {
                    text: 'YYYY-MM-DD',
                    textAlign: 'start'
                }
            },
            subDomain: {
                type: 'hour',
                label: 'HH:00',
                width: 35,
                height: 35,
                radius: 4
            },
            scale: {
                color: {
                    scheme: colorScheme,
                    type: 'linear',
                    domain: [0, 4]
                }
            }
        },

        [
            [
                Legend,
                {
                    label: 'Number of questions solved',
                    itemSelector: '#legend-label',
                },
            ]
        ]);
}


function loadDataAndDisplayCalendarData(cal, theme = 'light', colorScheme = 'Greens') {
    $.ajax({
        url: "/api/calendar/update",
        method: "GET",
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {
            // Process the data to adjust for timezone
            console.log("inside loadDataAndDisplayCalendarData");
            console.log(data);

            if(data.length === 0) {
                displayCalendar(cal, data, new Date(), theme, colorScheme);
            } else {
                // Get the user's timezone offset in minutes
                const localTimezoneOffsetMinutes = new Date().getTimezoneOffset();

                // Process the data with explicit timezone conversion
                const processedData = data.map(item => {
                    // Parse the UTC time from the server
                    const utcDate = new Date(item.date);

                    if (isNaN(utcDate.getTime())) {
                        console.error("Invalid date created from:", item.date);
                        return null; // Skip invalid dates
                    }

                    // Convert to local time by adjusting for timezone offset
                    // Note: getTimezoneOffset returns minutes WEST of UTC, so we subtract to go east
                    const localTimestamp = utcDate.getTime() - (localTimezoneOffsetMinutes * 60 * 1000);
                    const localDate = new Date(localTimestamp);

                    // Format as ISO string for CalHeatmap
                    // But we need to ensure CalHeatmap doesn't try to convert again
                    return {
                        date: localDate.toISOString(),
                        value: item.value,
                        timestamp: localTimestamp
                    };
                }).filter(item => item !== null);

                // Get min timestamp for start date
                const minTimestamp = Math.min(...processedData.map(item => item.timestamp));
                const startDate = new Date(minTimestamp);

                displayCalendar(cal, processedData, startDate, theme, colorScheme);
            }
        }
    });
}


function getPastSunday(startDay) {
    // const today = new Date();
    const dayOfWeek = startDay.getDay(); // 0 is Sunday, 1 is Monday, etc.

    // Calculate how many days to go back to reach Monday
    const daysToSubtract = dayOfWeek;

    // Create a new date by subtracting those days
    const pastSunday = new Date(startDay);
    pastSunday.setDate(startDay.getDate() - daysToSubtract);

    // Reset time to midnight
    pastSunday.setHours(0, 0, 0, 0);

    return pastSunday;
}

function getNextSaturday(startDay) {
    const sunday = getPastSunday(startDay);
    const nextSunday = new Date(sunday);
    nextSunday.setDate(sunday.getDate() + 6);
    return nextSunday;
}

// Example usage
// const lastMonday = getClosestPastMonday();
// console.log(lastMonday);