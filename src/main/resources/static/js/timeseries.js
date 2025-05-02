function createPredictionVisualization(schedule, alreadyCompleted, totalQuestions) {

    $.ajax({
        url: "/api/progress/stats",
        method: "GET",
        data: {},
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {

            // Totals
            const totalSolved = data.numberOfCompletedQuestions;
            const attempts = data.numberOfAttempts;
            const totalNumberOfQuestions = data.numberOfQuestions;
            const schedule = data.userScheduleDto.schedule;

            console.log("the schedule");
            console.log(schedule[0]);
            console.log(schedule[1]);
            console.log(schedule[2]);
            console.log(schedule[3]);
            console.log(schedule[4]);
            console.log(schedule[5]);
            console.log(schedule[6]);


            let chart; // Reference to the chart object

            const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

            // console.log("the schedule");
            // console.log(schedule);

            // Initial state
            let questionsPerDay = {
                Sun: schedule[0], Mon: schedule[1], Tue: schedule[2], Wed: schedule[3], Thu: schedule[4], Fri: schedule[5], Sat: schedule[6]
            };


            let completedQuestions = {
                Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0, Sun: 0
            };

            setUpCalendar(questionsPerDay, completedQuestions, daysOfWeek);

            // Initialize projection chart
            chart = createChart(totalNumberOfQuestions, totalSolved);

            // Update chart when button is clicked
            document.getElementById('update-chart').addEventListener('click', function() {
                // Get the current schedule from inputs
                daysOfWeek.forEach(day => {
                    questionsPerDay[day] = parseInt(document.getElementById(`target-${day}`).value) || 0;
                });

                // Update the chart
                if (chart) {
                    chart.destroy(); // Destroy previous chart if exists
                }
                chart = createChart(totalNumberOfQuestions, totalSolved);

                let scheduleData = { userSchedule: Object.values(questionsPerDay) }

                //ajax call here
                $.ajax({
                    url: "api/progress/update-schedule",
                    method: "POST",
                    data: scheduleData,
                    dataType: "json",
                    xhrFields : {
                        withCredentials: true
                    },
                    success: function(data) {
                        console.log("Successfully updated the schedule.")
                    },
                    error: function(xhr, status, error) {
                        console.error("Error response:", {
                            status: xhr.status,
                            statusText: xhr.statusText,
                            responseText: xhr.responseText,
                            error: error
                        });
                    },
                });

            });

            // Initialize stats
            updateStats(questionsPerDay, completedQuestions);
        }
    });
}

function setUpCalendar(questionsPerDay, completedQuestions, daysOfWeek) {
    // Setup the day headers
    const dayHeadersContainer = document.getElementById('day-headers');
    daysOfWeek.forEach(day => {
        const dayHeader = document.createElement('div');
        dayHeader.className = 'day-header';
        dayHeader.textContent = day;
        dayHeadersContainer.appendChild(dayHeader);
    });

    // Setup target inputs
    const targetInputsContainer = document.getElementById('target-inputs');
    daysOfWeek.forEach(day => {
        const inputContainer = document.createElement('div');
        inputContainer.className = 'input-container';

        const input = document.createElement('input');
        input.type = 'number';
        input.min = '0';
        input.value = questionsPerDay[day];
        input.id = `target-${day}`;
        input.setAttribute('aria-label', `Target questions for ${day}`);
        input.addEventListener('input', function() {
            handleTargetChange(day, this.value, questionsPerDay, completedQuestions);
        });

        inputContainer.appendChild(input);
        targetInputsContainer.appendChild(inputContainer);
    });

    // Setup completion inputs
    const completionInputsContainer = document.getElementById('completion-inputs');
    daysOfWeek.forEach(day => {
        const inputContainer = document.createElement('div');
        inputContainer.className = 'input-container';

        const input = document.createElement('input');
        input.type = 'number';
        input.min = '0';
        input.value = completedQuestions[day];
        input.className = 'completion-input';
        input.id = `completion-${day}`;
        input.setAttribute('aria-label', `Completed questions for ${day}`);
        input.setAttribute('disabled', true);
        input.addEventListener('input', function() {
            handleCompletedChange(day, this.value, questionsPerDay, completedQuestions);
        });

        inputContainer.appendChild(input);
        completionInputsContainer.appendChild(inputContainer);
    });
}

// Handle target change
function handleTargetChange(day, value, questionsPerDay, completedQuestions) {
    const numValue = Math.max(0, parseInt(value) || 0);
    questionsPerDay[day] = numValue;
    updateStats(questionsPerDay, completedQuestions);
}

// Handle completed change
function handleCompletedChange(day, value, questionsPerDay, completedQuestions) {
    const numValue = Math.max(0, parseInt(value) || 0);
    completedQuestions[day] = numValue;
    updateStats(questionsPerDay, completedQuestions);
}

// Update statistics
function updateStats(questionsPerDay, completedQuestions) {
    const weeklyTotal = Object.values(questionsPerDay).reduce((sum, val) => sum + val, 0);
    const completedTotal = Object.values(completedQuestions).reduce((sum, val) => sum + val, 0);
    const percentage = weeklyTotal > 0 ? Math.round((completedTotal / weeklyTotal) * 100) : 100;

    document.getElementById('target-total').textContent = `Target: ${weeklyTotal}`;
    document.getElementById('done-total').textContent = `Done: ${completedTotal}`;
    document.getElementById('percentage').textContent = `${percentage}%`;
    document.getElementById('progress-fill').style.width = `${percentage}%`;
}

// Generate projection data based on the weekly schedule
function generateProjection(totalQuestions, alreadyCompleted) {
    const data = [];
    const labels = [];
    let totalCompleted = alreadyCompleted; // Start with already completed questions
    let day = 0;
    let weekTotal = 0;

    // Get current values from inputs
    const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    const schedule = {};

    daysOfWeek.forEach(day => {
        const input = document.getElementById(`target-${day}`);
        let dayTargetCount = input ? (parseInt(input.value) || 0) : 0;
        weekTotal += dayTargetCount;
        schedule[day] = dayTargetCount;
    });

    if(weekTotal > 0) {
        // Add starting point with already completed questions
        data.push(totalCompleted);
        labels.push(`Start (${alreadyCompleted} already done)`);

        // Generate data until we reach the target
        while (totalCompleted < totalQuestions) {
            day++;
            const weekday = daysOfWeek[day % 7];
            const questionsToday = schedule[weekday];

            // Only add a data point if questions are completed that day
            if (questionsToday > 0) {
                totalCompleted = Math.min(totalCompleted + questionsToday, totalQuestions);

                data.push(totalCompleted);
                labels.push(`Day ${day} (${weekday})`);

                // If we've reached the total, break
                if (totalCompleted >= totalQuestions) {
                    break;
                }
            }
        }
    }

    return { data, labels, completionDay: day };
}

// Create chart
function createChart(totalQuestions, alreadyCompleted) {
    const { data, labels, completionDay } = generateProjection(totalQuestions, alreadyCompleted);

    // Calculate completion date information
    const weeksToComplete = Math.floor(completionDay / 7);
    const daysRemaining = completionDay % 7;

    // Update info box
    const completionEstimation = document.getElementById('completion-estimation');
    const alreadyCompletedInfo = document.getElementById('already-completed-info');
    const weeklyTotalInfo = document.getElementById('weekly-total-info');

    completionEstimation.textContent = `Estimated time to complete all ${totalQuestions} questions: ${weeksToComplete} weeks and ${daysRemaining} days`;
    alreadyCompletedInfo.textContent = `Starting with ${alreadyCompleted} questions already completed`;

    // Calculate weekly total
    const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    let weeklyTotal = 0;

    daysOfWeek.forEach(day => {
        const input = document.getElementById(`target-${day}`);
        weeklyTotal += input ? (parseInt(input.value) || 0) : 0;
    });

    weeklyTotalInfo.textContent = `Based on your weekly schedule of ${weeklyTotal} questions per week`;

    // Create simplified labels for better readability
    const simplifiedLabels = labels.map((label, index) => {
        if (index === 0) return 'Start';
        if (index === labels.length - 1) return `Day ${completionDay}`;

        // Extract the day number from the label
        const dayMatch = label.match(/Day (\d+)/);
        if (dayMatch && dayMatch[1]) {
            return `Day ${dayMatch[1]}`;
        }
        return label;
    });

    // Create chart
    const ctx = document.getElementById('completionChart').getContext('2d');

    // Create data for goal line
    const goalData = Array(data.length).fill(totalQuestions);

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: simplifiedLabels,
            datasets: [
                {
                    label: 'Questions Completed',
                    data: data,
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    tension: 0.1,
                    pointRadius: 5,
                    pointHoverRadius: 7
                },
                {
                    label: `Goal (${totalQuestions} questions)`,
                    data: goalData,
                    borderColor: 'red',
                    borderDash: [5, 5],
                    pointRadius: 0,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: false,
                    min: 0,
                    max: totalQuestions + 10,
                    title: {
                        display: true,
                        text: 'Questions Completed'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Days'
                    },
                    ticks: {
                        // Only show some of the labels to avoid overcrowding
                        callback: function(value, index, values) {
                            // Show first, last, and every 7th label (weekly intervals)
                            if (index === 0 || index === labels.length - 1 || index % 7 === 0) {
                                return simplifiedLabels[index];
                            }
                            return null;
                        },
                        maxRotation: 45,
                        minRotation: 45
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${context.raw} questions`;
                        },
                        title: function(tooltipItems) {
                            // Show the original detailed label in the tooltip
                            const index = tooltipItems[0].dataIndex;
                            return labels[index];
                        }
                    }
                }
            }
        }
    });
}
