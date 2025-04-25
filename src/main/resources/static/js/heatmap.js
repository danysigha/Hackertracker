function initializeCalendar() {
    return new CalHeatmap();
}

function displayCalendar(cal, processedData, startDate, endDate) {

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
                min: startDate,
                max: endDate,
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
            const endDate = new Date(maxTimestamp);

            displayCalendar(cal, processedData, startDate, endDate);

        }
    });
}