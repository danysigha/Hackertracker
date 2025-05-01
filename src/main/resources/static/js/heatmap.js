function initializeCalendar() {
    return new CalHeatmap();
}

function displayCalendar(cal, processedData, startDate) {

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
            data: {
                source: processedData,
                x: 'date',
                y: 'value'
            },
            date: {
                start: getPastSunday(new Date()),
                min: getPastSunday(startDate),
                max: new Date(getNextSaturday(new Date())),
                // Explicitly set timezone to UTC
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
                    scheme: 'Greens',
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


function loadDataAndDisplayCalendarData(cal) {
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
                displayCalendar(cal, data, new Date());
            } else {
                const processedData = data.map(item => {
                    // Parse date as local time
                    // console.log(item.date);
                    const fixedDateString = item.date + ":00:00Z";
                    const localDate = new Date(fixedDateString);

                    // Get timezone offset in hours
                    const tzOffset = localDate.getTimezoneOffset() / 60;

                    // Adjust the hour to compensate for timezone conversion
                    // This keeps the hour in local time when the date is parsed later
                    localDate.setHours(localDate.getHours() - tzOffset);

                    return {
                        // Use adjusted ISO string with explicit timezone
                        date: localDate.toISOString(),
                        value: item.value,
                        // Store the timestamp for min/max calculations
                        timestamp: localDate.getTime()
                    };
                });

                // Get min and max timestamps for start and end dates
                const minTimestamp = Math.min(...processedData.map(item => item.timestamp));
                const maxTimestamp = Math.max(...processedData.map(item => item.timestamp));

                // Create Date objects from the timestamps
                const startDate = new Date(minTimestamp);
                //const endDate = new Date(maxTimestamp);

                console.log(startDate);
                //console.log(endDate);

                displayCalendar(cal, processedData, startDate);
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