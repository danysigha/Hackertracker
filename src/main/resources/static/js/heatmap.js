function loadCalendar() {
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


// Debug: Check April 17 data after timezone adjustment
// <%--console.log('Timezone-adjusted April 17 data:');--%>
// <%--processedData--%>
// <%--    .filter(item => item.date.includes('2025-04-17'))--%>
// <%--    .forEach(item => {--%>
// <%--        const origDate = new Date(item.date);--%>
// <%--        console.log(`${item.date} (local hour: ${origDate.getHours()}:00) - value: ${item.value}`);--%>
// <%--    });--%>


            const cal = new CalHeatmap();

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
    });
}



// Initial setup
$(document).ready(function() {
    // Small delay to ensure cookie is fully processed
    setTimeout(function() {
        loadCalendar();
    }, 100); // Even a short 100ms delay can help
});




// const data = [
//     { date: '2025-04-29 15:47:38.123456', value: 1 },
//     { date: '2025-04-29 22:47:38.123456', value: 2 },
//     { date: '2025-04-28 08:12:54.987654', value: 5 },
//     { date: '2025-04-28 14:33:21.456789', value: 4 },
//     { date: '2025-04-28 19:27:44.234567', value: 1 },
//     { date: '2025-04-28 10:45:09.876543', value: 2 },
//     { date: '2025-04-27 16:58:37.345678', value: 3 },
//     { date: '2025-04-27 11:21:05.654321', value: 1 },
//     { date: '2025-04-27 20:39:16.789012', value: 1 },
//     { date: '2025-04-26 09:04:27.567890', value: 1 },
//     { date: '2025-04-26 13:51:33.901234', value: 2 },
//     { date: '2025-04-26 18:05:49.432109', value: 1 },
//     { date: '2025-04-25 07:30:17.246810', value: 2 },
//     { date: '2025-04-25 12:29:58.135792', value: 2 },
//     { date: '2025-04-25 23:10:42.579136', value: 1 },
//     { date: '2025-04-24 15:22:39.864209', value: 4 },
//     { date: '2025-04-24 10:37:11.294758', value: 1 },
//     { date: '2025-04-24 17:48:25.713924', value: 1 },
//     { date: '2025-04-23 21:16:03.592718', value: 1 },
//     { date: '2025-04-23 14:59:22.385019', value: 2 },
//
//     { date: '2025-04-22 15:47:38.123456', value: 1 },
//     { date: '2025-04-22 22:47:38.123456', value: 2 },
//     { date: '2025-04-22 08:12:54.987654', value: 5 },
//     { date: '2025-04-21 14:33:21.456789', value: 4 },
//     { date: '2025-04-21 19:27:44.234567', value: 1 },
//     { date: '2025-04-21 10:45:09.876543', value: 2 },
//     { date: '2025-04-20 16:58:37.345678', value: 3 },
//     { date: '2025-04-20 11:21:05.654321', value: 1 },
//     { date: '2025-04-20 20:39:16.789012', value: 1 },
//     { date: '2025-04-19 09:04:27.567890', value: 1 },
//     { date: '2025-04-19 13:51:33.901234', value: 2 },
//     { date: '2025-04-19 18:05:49.432109', value: 1 },
//     { date: '2025-04-18 07:30:17.246810', value: 2 },
//     { date: '2025-04-18 12:29:58.135792', value: 2 },
//     { date: '2025-04-18 23:10:42.579136', value: 1 },
//     { date: '2025-04-17 15:22:39.864209', value: 4 },
//     { date: '2025-04-17 10:37:11.294758', value: 1 },
//     { date: '2025-04-17 17:48:25.713924', value: 1 },
//     { date: '2025-04-16 21:16:03.592718', value: 1 },
//     { date: '2025-04-16 14:59:22.385019', value: 2 }
// ];
